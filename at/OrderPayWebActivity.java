package com.cloudhome.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudhome.R;

public class OrderPayWebActivity extends BaseActivity {
    private WebView webView;
    private String url, title;
    private RelativeLayout jsback;
    private RelativeLayout rl_right;
    private TextView jstext;

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.javascript);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");

        jstext = (TextView) findViewById(R.id.tv_title);
        jstext.setText(title);
        jsback = (RelativeLayout) findViewById(R.id.rl_back);
        rl_right=(RelativeLayout) findViewById(R.id.rl_share);
        rl_right.setVisibility(View.INVISIBLE);
        jsback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        webView = (WebView) this.findViewById(R.id.wv_protocol);
        // button = (Button) this.findViewById(R.id.button);
        WebSettings setting = webView.getSettings();

        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDomStorageEnabled(true);


        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });


        webView.setWebViewClient(new WebViewClient());


        webView.loadUrl(url);

    }



}