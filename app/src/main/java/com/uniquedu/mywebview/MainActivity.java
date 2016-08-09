package com.uniquedu.mywebview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.button_load)
    Button buttonLoad;
    @InjectView(R.id.button_js)
    Button buttonJs;
    @InjectView(R.id.progressbar)
    ProgressBar progressbar;
    @InjectView(R.id.webview)
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        webview.getSettings().setJavaScriptEnabled(true);//支持js
        webview.addJavascriptInterface(new MyJs(), "myJsObj");//设置坚挺js中方法的对象
        //监听html上的js方法的调用，不添加webview不会弹出alert
        webview.onKeyDown(WebView.KEEP_SCREEN_ON,null);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //添加网页加载进度
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                } else {
                    if (progressbar.getVisibility() == View.GONE)
                        progressbar.setVisibility(View.VISIBLE);
                    progressbar.setProgress(newProgress);
                }
            }
        });
        //原因是html网页中混合了http和https的网络连接，这里会导致图片不显示，特此说明
        webview.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //允许混合开发
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        webView.getSettings().setDomStorageEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //接受证书
                handler.proceed();//接受证书
            }
        });
    }

    @OnClick({R.id.button_load, R.id.button_js})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_load:
                webview.loadUrl("file:///android_asset/index.html");
                break;
            case R.id.button_js:
                webview.loadUrl("javascript:myAlert()");
                break;
        }
    }

    class MyJs {
        //4.4以后必须添加这个注解，不然事件无响应
        @JavascriptInterface
        public void useAndroidMethod(String content) {
            Toast.makeText(getApplicationContext(), "调用到android的方法" + content, Toast.LENGTH_SHORT).show();
        }
    }
}
