package top.cokernut.customwebview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebView extends WebView {

    private WebViewInterface mWebViewInterface;

    public MyWebView(Context context) {
        super(context);
        init();
    }

    public void setWebViewInterface(WebViewInterface baseWebViewInterface) {
        mWebViewInterface = baseWebViewInterface;
    }

    //回调方法接口
    public interface WebViewInterface {
        void showToast(String msg);

        void getTitle(String title);
    }

    public void init() {
        WebSettings ws = getSettings();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setBuiltInZoomControls(false);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        //注入带有Java方法的JS对象，名字可以自定义（js_obj）
        addJavascriptInterface(new InJavaScriptObj(), "js_obj");
        setWebViewClient(new MyWebViewClient());
        setWebChromeClient(new MyWebChromeClient());
    }

    final class InJavaScriptObj {
        //android 4.2 之后版本提供给js调用的函数必须带有注释语句@JavascriptInterface
        @JavascriptInterface
        public void showToast(String msg) {
            mWebViewInterface.showToast(msg);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //加载网页url，此处可以根据url进行相应的处理
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //实现自己的逻辑
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //实现自己的逻辑
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            //错误处理
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            //http错误处理
            super.onReceivedHttpError(view, request, errorResponse);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //这个方法是收到网页的title的时候会调用的，这里我们可以拿到网页的title显示处理
            mWebViewInterface.getTitle(title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            // 在这里你可以拦截网页的Alert来实现自己的逻辑，return true停止事件的继续传播，也可以使用默认实现
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            // 在这里你可以实现拦截网页的Confirm来实现自己的逻辑，return true停止事件的继续传播，也可以使用默认实现
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            //在这里你可以进度的变化实现自己的逻辑
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }
}

