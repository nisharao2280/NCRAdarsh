package com.ncradarsh.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private static final String WEBSITE_URL = "https://ncradarsh.com";
    private static final int STORAGE_PERMISSION_CODE = 100;

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout noInternetLayout;
    private String pendingDownloadUrl;
    private String pendingDownloadFileName;
    private String pendingDownloadMimeType;
    private String pendingDownloadCookies;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        noInternetLayout = findViewById(R.id.noInternetLayout);
        Button retryButton = findViewById(R.id.retryButton);

        setupWebView();
        setupSwipeRefresh();

        retryButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                noInternetLayout.setVisibility(android.view.View.GONE);
                webView.setVisibility(android.view.View.VISIBLE);
                webView.reload();
            } else {
                Toast.makeText(this, "Abhi bhi internet nahi hai!", Toast.LENGTH_SHORT).show();
            }
        });

        loadWebsite();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setTextZoom(85);
        settings.setMediaPlaybackRequiresUserGesture(false);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(android.view.View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(android.view.View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    progressBar.setVisibility(android.view.View.GONE);
                    if (!isNetworkAvailable()) {
                        showNoInternet();
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (!url.contains("ncradarsh.com")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        // ignore
                    }
                    return true;
                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(android.view.View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(android.view.View.GONE);
                }
            }
        });

        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            pendingDownloadUrl = url;
            pendingDownloadFileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
            pendingDownloadMimeType = mimeType;
            pendingDownloadCookies = CookieManager.getInstance().getCookie(url);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_CODE);
                } else {
                    startDownload(url, pendingDownloadFileName, mimeType, pendingDownloadCookies);
                }
            } else {
                startDownload(url, pendingDownloadFileName, mimeType, pendingDownloadCookies);
            }
        });
    }

    private void startDownload(String url, String fileName, String mimeType, String cookies) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimeType);
        if (cookies != null) request.addRequestHeader("cookie", cookies);
        request.addRequestHeader("User-Agent", webView.getSettings().getUserAgentString());
        request.setDescription("NCRAdarsh se download ho raha hai...");
        request.setTitle(fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            dm.enqueue(request);
            Toast.makeText(this, "Download shuru: " + fileName, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.red_primary),
                ContextCompat.getColor(this, R.color.white)
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isNetworkAvailable()) {
                webView.reload();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                showNoInternet();
            }
        });
    }

    private void loadWebsite() {
        if (isNetworkAvailable()) {
            webView.setVisibility(android.view.View.VISIBLE);
            noInternetLayout.setVisibility(android.view.View.GONE);
            webView.loadUrl(WEBSITE_URL);
        } else {
            showNoInternet();
        }
    }

    private void showNoInternet() {
        webView.setVisibility(android.view.View.GONE);
        noInternetLayout.setVisibility(android.view.View.VISIBLE);
        progressBar.setVisibility(android.view.View.GONE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("App Band Karein?")
                .setMessage("Kya aap NCRAdarsh app se bahar jaana chahte hain?")
                .setPositiveButton("Haan, Band Karein", (dialog, which) -> finish())
                .setNegativeButton("Nahi, Rukein", (dialog, which) -> dialog.dismiss())
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && pendingDownloadUrl != null) {
            startDownload(pendingDownloadUrl, pendingDownloadFileName, pendingDownloadMimeType, pendingDownloadCookies);
        }
    }

    @Override
    protected void onResume() { super.onResume(); webView.onResume(); }

    @Override
    protected void onPause() { super.onPause(); webView.onPause(); }

    @Override
    protected void onDestroy() {
        if (webView != null) { webView.stopLoading(); webView.destroy(); }
        super.onDestroy();
    }
}
