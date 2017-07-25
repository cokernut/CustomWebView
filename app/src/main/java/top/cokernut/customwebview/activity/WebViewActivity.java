package top.cokernut.customwebview.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import top.cokernut.customwebview.R;
import top.cokernut.customwebview.widget.MyWebView;

public class WebViewActivity extends AppCompatActivity implements MyWebView.WebViewInterface, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private MyWebView       mWebView;
    private ImageView       mBackIv;
    private TextView        mTitleTv;
    private TextView        mMenuTv;
    private SwipeRefreshLayout mRefreshSRL;

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
        mTitleTv    = (TextView) findViewById(R.id.tv_title);
        mRefreshSRL = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        mMenuTv.setOnClickListener(this);
        mBackIv.setOnClickListener(this);
        mWebView    = new MyWebView(this);
        mRefreshSRL.addView(mWebView);
        // 不能在onCreate中设置，这个表示当前是刷新状态，如果一进来就是刷新状态，SwipeRefreshLayout会屏蔽掉下拉事件
        //swipeRefreshLayout.setRefreshing(true);
        // 设置下拉进度的主题颜色
        mRefreshSRL.setColorSchemeColors(Color.parseColor("#3FF43F"),Color.parseColor("#709090"));
        // 设置下拉进度的背景颜色，默认就是白色的
        mRefreshSRL.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mWebView.setWebViewInterface(this);
        mWebView.setOnScrollListener(new MyWebView.OnScrollListener() {
            @Override
            public void onTop() {
                mRefreshSRL.setEnabled(true);
            }

            @Override
            public void onCenter() {
                mRefreshSRL.setEnabled(false);
            }

            @Override
            public void onBottom() {

            }

            @Override
            public void back() {
                mRefreshSRL.setRefreshing(false);
            }
        });
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
                if (Build.VERSION.SDK_INT > 18) {
                    // 因为该方法的执行不会使页面刷新，而第一种方法（loadUrl ）的执行则会。
                    //Android 4.4 后才可使用
                    mWebView.evaluateJavascript("javascript:showAlert()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //此处为 js 返回的结果
                        }
                    });
                } else {
                    mWebView.loadUrl("javascript:showAlert()");
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        mWebView.reload();
    }
}
