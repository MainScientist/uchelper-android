package cl.uc.fipezoa.pucassistant.mywebview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import cl.uc.fipezoa.pucassistant.classes.DataManager;
import cl.uc.fipezoa.requests.Session;

/**
 * Created by fipezoa on 2/21/2016.
 */
public class MyWebView extends WebView {

    MyWebViewgressBar progressBar;
    MyWebViewHeader myWebViewHeader;

    public MyWebView(Context context) {
        super(context);
        configure();
    }

    public MyWebView(Context context, AttributeSet set) {
        super(context, set);
        configure();
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    public void configure(){
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (myWebViewHeader != null) {
                    myWebViewHeader.setUrl(getUrl());
                    if (getTitle() != null) {
                        myWebViewHeader.setTitle(getTitle().trim());
                    }
                    if (canGoBack()) {
                        myWebViewHeader.enableBack();
                    } else {
                        myWebViewHeader.disableBack();
                    }
                    if (canGoForward()) {
                        myWebViewHeader.enableForward();
                    } else {
                        myWebViewHeader.disableForward();
                    }
                }
                CookieManager cookieManager = CookieManager.getInstance();
                if (cookieManager != null && getUrl() != null) {
                    Log.i(getUrl(), "cook: " + cookieManager.getCookie(getUrl()));
                    Log.i("USER", DataManager.user.getSession().getCookiesHeader());
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });
        setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getContext().startActivity(intent);
            }
        });
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setSupportZoom(true);
        getSettings().setDisplayZoomControls(false);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setUseWideViewPort(true);
        setInitialScale(100);
    }

    public void setProgressBar(final MyWebViewgressBar progressBar){
        this.progressBar = progressBar;
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
    }

    public void setMyWebViewHeader(MyWebViewHeader header){
        myWebViewHeader = header;
        myWebViewHeader.setMyWebView(this);
    }

    public void loadUrlWithSession(String url, Session session){
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, DataManager.user.getSession().getCookiesHeader());
        CookieSyncManager.getInstance().sync();
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.flush();
        }else {
            CookieSyncManager.getInstance().sync();
        }
        loadUrl(url);
    }

    public void loadUrlWithCookies(String url, String[] hosts){
        CookieManager cookieManager = CookieManager.getInstance();
        for (String host : hosts) {
            cookieManager.setCookie(host, DataManager.user.getSession().getCookiesHeader());
        }
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.flush();
        }else {
            CookieSyncManager.getInstance().sync();
        }
        loadUrl(url);
    }

}
