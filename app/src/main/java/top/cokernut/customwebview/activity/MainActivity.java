package top.cokernut.customwebview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import top.cokernut.customwebview.R;


public class MainActivity extends AppCompatActivity {

    private TextView mBtnTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnTv = (TextView) findViewById(R.id.tv_btn);
        mBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
               // intent.putExtra("url", "file:///android_asset/test.html");
                intent.putExtra("url", "file:///android_asset/test.html");
                startActivity(intent);
            }
        });
    }
}
