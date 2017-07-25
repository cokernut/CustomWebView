package top.cokernut.customwebview.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.cokernut.customwebview.R;
import top.cokernut.customwebview.util.HtmlUtil;
import top.cokernut.customwebview.widget.MyWebView;

public class OWWebViewActivity extends AppCompatActivity implements MyWebView.WebViewInterface {

    private LinearLayout    mToolbarLl;
    private MyWebView       mWebView;
    private Toolbar         mToolBarTb;
    private ImageView       mBackIv;
    private ImageView       mCloseIv;
    private TextView        mTitleTv;

    private String          url;
    private boolean         isGoBack = false;
    private List<Btn>       mBtns    = new ArrayList<>();
    private List<Property>  mPropertys    = new ArrayList<>();
    private List<String>    mMetas   = new ArrayList<>();
    private List<String>    mTitleProperty   = new ArrayList<>();
    private HashMap<String, String> mTitles = new HashMap<>();

    private static final int PAGE_FINISH        = 0x100;
    private static final int SET_TITLE          = 0x101;
    private static final int SET_GOBACK_TITLE   = 0x102;
    public static final String CHANGE_HREF = "change_href";
    private boolean isChangeHref = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PAGE_FINISH) {
                initToolBar();
            } else if (msg.what == SET_TITLE) {
                setMyTitle((String) msg.obj);
            } else if (msg.what == SET_GOBACK_TITLE) {
                String title = mTitles.get(mWebView.getUrl());
                setMyTitle(title);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ow_webview);
        initView();
    }

    private void initView() {
        mTitles.clear();
        url = getIntent().getStringExtra("url");
        isChangeHref = getIntent().getBooleanExtra(CHANGE_HREF, false);
        mToolBarTb  = (Toolbar) findViewById(R.id.toolbar);
        mToolbarLl  = (LinearLayout) findViewById(R.id.ll_menu);
        mBackIv     = (ImageView) findViewById(R.id.iv_back);
        mCloseIv    = (ImageView) findViewById(R.id.iv_close);
        mTitleTv    = (TextView) findViewById(R.id.tv_title);
        mWebView = (MyWebView) findViewById(R.id.wv_webview);
        setSupportActionBar(mToolBarTb);
        mWebView.setWebViewInterface(this);
        mWebView.setIsChangeHref(isChangeHref);
        setListener();
        mWebView.loadUrl(url);
    }

    private void setListener() {
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OWWebViewActivity.this.finish();
            }
        });
    }

    @Override
    public void onPageFinish(List<String> metas) {
        mMetas = metas;
        if (isGoBack) {
            if (mTitles.size() > 0) {
                Message message = new Message();
                message.what = SET_GOBACK_TITLE;
                mHandler.sendMessage(message);
            }
        }
        isGoBack = false;
        parseMetaStr();
    }

    @Override
    public void onPageStart() {
        mToolbarLl.removeAllViews();
        mMetas.removeAll(mMetas);
        mBtns.removeAll(mBtns);
    }

    @Override
    public void showCloseBtn(boolean flag) {
        if (flag) {
            mCloseIv.setVisibility(View.VISIBLE);
        } else {
            mCloseIv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTitleProperty(List<String> titleProperty) {
        mTitleProperty = titleProperty;
        parseTitleProperty();
    }

    private void parseTitleProperty() {
        for (String metaStr : mTitleProperty) {
            Property property = new Property();
            property.property = HtmlUtil.getProperty(metaStr);
            property.content = HtmlUtil.getContent(metaStr);
            mPropertys.add(property);
        }
        Message message = new Message();
        message.what = PAGE_FINISH;
        mHandler.sendMessage(message);
    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void getTitle(String title) {
        mTitles.put(mWebView.getUrl(), title);
        Message message = new Message();
        message.what = SET_TITLE;
        message.obj = title;
        mHandler.sendMessage(message);
    }

    private void setMyTitle(String title) {
        mTitleTv.setText(title);
    }

    private void parseMetaStr() {
        for (String metaStr : mMetas) {
            Btn btn = new Btn();
            btn.title = HtmlUtil.getTitle(metaStr);
            btn.icon = HtmlUtil.getIcon(metaStr);
            btn.content = HtmlUtil.getContent(metaStr);
            mBtns.add(btn);
        }
        Message message = new Message();
        message.what = PAGE_FINISH;
        mHandler.sendMessage(message);
    }

    class Btn {
        private String title;
        private String icon;
        private String content;
    }

    class Property {
        private String property;
        private String content;
    }



    private void initToolBar() {
        for (Property property: mPropertys) {
            if (property != null && property.property != null && property.property.equals("visibility")) {
                if (property.content != null && property.content.equals("hidden")) {
                    this.mToolBarTb.setVisibility(View.GONE);
                    return;
                }
            }
        }
        for (Btn btn : mBtns) {
            if (btn != null && (btn.title != null || btn.icon != null)) {
                initBtn(btn);
            }
        }
    }

    private void initBtn(final Btn btn) {
        TextView tv = new TextView(this);
        if (btn.title != null) {
            tv.setText(btn.title);
        }
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(10, 10, 10, 10);
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER);
        if (btn.icon != null) {
            Drawable d = getResources().getDrawable(getResource(btn.icon));
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            tv.setCompoundDrawables(d, null, null, null);
        }
        mToolbarLl.addView(tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:" + btn.content);
            }
        });
    }

    public int getResource(String imageName) {
        Context ctx = getBaseContext();
        imageName = getFileNameNoEx(imageName);
        int resId = getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        return resId;
    }


    public String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (mWebView.canGoBack()) {
            if (mTitles.size() > 0) {
                mTitles.remove(mWebView.getUrl());
            }
            mWebView.goBack();
            isGoBack = true;
        } else {
            OWWebViewActivity.this.finish();
            mTitles.clear();
        }
    }
}
