package top.cokernut.customwebview.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import top.cokernut.customwebview.R;
import top.cokernut.customwebview.widget.MyWebView;

public class WebViewActivity extends AppCompatActivity implements MyWebView.WebViewInterface, View.OnClickListener {

    private LinearLayout    mWebViewLl;
    private MyWebView mWebView;
    private ImageView       mBackIv;
    private TextView        mTitleTv;
    private TextView        mMenuTv;

    private String          url;

    private static final int SET_TITLE = 0x101;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SET_TITLE) {
                setMyTitle((String) msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();
    }

    private void initView() {
        url = getIntent().getStringExtra("url");
        mMenuTv     = (TextView) findViewById(R.id.tv_menu);
        mBackIv     = (ImageView) findViewById(R.id.iv_back);
        mWebViewLl  = (LinearLayout) findViewById(R.id.ll_webview);
        mTitleTv    = (TextView) findViewById(R.id.tv_title);
        mMenuTv.setOnClickListener(this);
        mBackIv.setOnClickListener(this);
        mWebView    = new MyWebView(this);
        mWebViewLl.addView(mWebView);
        mWebView.setWebViewInterface(this);
        mWebView.loadUrl(url);
    }


    //回调方法的实现
    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //回调方法的实现
    @Override
    public void getTitle(String title) {
        Message message = new Message();
        message.what = SET_TITLE;
        message.obj = title;
        mHandler.sendMessage(message);
    }

    private void setMyTitle(String title) {
        mTitleTv.setText(title);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                back();
                break;
            case R.id.tv_menu:
                //注入JS代码,Java调用JS
                mWebView.loadUrl("javascript:alert('网页弹窗alert');");
                break;
        }
    }
}
