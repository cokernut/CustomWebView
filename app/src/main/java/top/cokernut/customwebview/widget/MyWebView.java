package top.cokernut.customwebview.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebView extends WebView {

    private WebViewInterface mWebViewInterface;
    private OnScrollListener mOnScrollListener;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private Context mContext;
    private Activity mActivity;

    public MyWebView(Activity activity) {
        super(activity);
        mContext = activity;
        mActivity = activity;
        init();
    }

    public void setWebViewInterface(WebViewInterface baseWebViewInterface) {
        mWebViewInterface = baseWebViewInterface;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    //回调方法接口
    public interface WebViewInterface {
        void showToast(String msg);

        void getTitle(String title);
    }

    public interface OnScrollListener {
        void onTop();

        void onCenter();

        void onBottom();

        void back();
    }

    @Override
    public void goBack() {
        mOnScrollListener.back();
        super.goBack();
    }

    public void init() {
        WebSettings ws = getSettings();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true); //是否开启本地DOM存储
        ws.setDatabaseEnabled(true);   //开启 database storage API 功能
        String cacheDirPath = mContext.getFilesDir().getAbsolutePath()+"cache/";
        // 每个 Application 只调用一次 WebSettings.setAppCachePath()
        ws.setAppCachePath(cacheDirPath);// 设置缓存路径
        ws.setAppCacheEnabled(true);//开启 Application Caches 功能
        //设置自适应屏幕，两者合用
        //ws.setUseWideViewPort(true); //将图片调整到适合webview的大小
        //ws.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        ws.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        ws.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        ws.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //设置webview缓存模式
        ws.setAllowFileAccess(true); //设置可以访问文件
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
//        ws.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
//        ws.setLoadsImagesAutomatically(true); //支持自动加载图片
//        ws.setDefaultTextEncodingName("utf-8");//设置编码格式
        //注入带有Java方法的JS对象，名字可以自定义（js_obj）
        addJavascriptInterface(new InJavaScriptObj(), "js_obj");
        setWebViewClient(new MyWebViewClient());
        setWebChromeClient(new MyWebChromeClient());
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.getScrollY() <= 0) {
            mOnScrollListener.onTop();
        } else if(this.getContentHeight()*this.getScale()-(this.getHeight()+this.getScrollY())==0){
            //已经处于底端
            //mOnScrollListener.onBottom();
        } else {
            mOnScrollListener.onCenter();
        }
    }

  /*  @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                if (this.getScrollY() <= 0)
//                    this.scrollTo(0, 1);
                break;
            case MotionEvent.ACTION_MOVE:
                if (this.getScrollY() <= 0) {
                    mOnScrollListener.onTop();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }
*/
    final class InJavaScriptObj {
        //android 4.2 之后版本提供给js调用的函数必须带有注释语句@JavascriptInterface
        @JavascriptInterface
        public void showToast(String msg) {
            mWebViewInterface.showToast(msg);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //拦截url进行处理，如果不需要webview后续处理就return true
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            //拦截资源请求进行处理，如果返回null则正常处理
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //实现自己的逻辑
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //实现自己的逻辑
            super.onPageStarted(view, url, favicon);
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

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            //JS调用window.close()方法触发
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        mActivity.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }
}

