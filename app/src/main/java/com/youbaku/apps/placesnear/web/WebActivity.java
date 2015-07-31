/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.web;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;

public class WebActivity extends ActionBarActivity {
    public static final String URL="url";
    public static final String TITLE="title";
    public static final String COLOR="color";
    public static final String SUBTITLE="subtitle";

    private WebView web;
    private String url="";
    private String title="";
    private int color=0;
    private String subtitle="";
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_web);

        Intent in=getIntent();
        url=in.getStringExtra(URL);
        title=in.getStringExtra(TITLE);
        color=in.getIntExtra(COLOR, 0x003399);
        subtitle=in.getStringExtra(SUBTITLE);

        web=(WebView)findViewById(R.id.web_activity_web);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new WebViewClient());
        final ActionBarActivity tt=this;
        web.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                if(progress == 100)
                    tt.setSupportProgressBarIndeterminateVisibility(false);
            }
        });

        ActionBar act=((ActionBar)getSupportActionBar());
        if(subtitle.equals("Facebook")){
            System.out.println(url);
            act.setBackgroundDrawable(new ColorDrawable(Color.parseColor(App.FacebookColor)));
            web.loadUrl("https://m.facebook.com/"+url);
        }else if(subtitle.equals("Twitter")){
            act.setBackgroundDrawable(new ColorDrawable(Color.parseColor(App.TwitterColor)));
            web.loadUrl("https://mobile.twitter.com/"+url);
        }else {
            if(url.length()>8 && !url.substring(0,7).equals("http://") && !url.substring(0,8).equals("https://"))
                url="http://"+url;
            web.loadUrl(url);
            act.setBackgroundDrawable(new ColorDrawable(color));
        }
        act.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.buttonback));
        act.setTitle(title);
        act.setSubtitle(subtitle);
        act.setDisplayShowCustomEnabled(true);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity=0x05;
        ProgressBar pro=new ProgressBar(this);
        pro.setVisibility(View.GONE);
        pro.setIndeterminate(true);
        act.setCustomView(pro, params);
        setSupportProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getSupportActionBar().getCustomView().setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        item=menu.getItem(0);
        if(subtitle.equals("Facebook") || subtitle.equals("Twitter")){
            item.setVisible(true);
        }else
            item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }else if(id== R.id.open_with_app){
            if(subtitle.equals("Facebook")){
                Intent intent;
                try {
                    this.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/{" + url+"}"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + url));
                }
                startActivity(intent);
            }else{
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + url)));
                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + url)));
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
