package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.youbaku.apps.placesnear.adapter.Adapter;
import com.youbaku.apps.placesnear.utils.User;

import java.util.HashMap;
import java.util.Map;


public class ProfilActivity extends ActionBarActivity {
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profil);

        // ********** ********** ********** ********** **********
        // Initilization ActionBar
        // ********** ********** ********** ********** **********
        actionBar =(ActionBar) getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(App.DefaultActionBarColor)));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.app_logo);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.buttonback));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity=0x05;
        ProgressBar pro=new ProgressBar(this);
        pro.setVisibility(View.GONE);
        pro.setIndeterminate(true);
        actionBar.setCustomView(pro, params);


        //Set Contents
        String nameSurname=User.getInstance().getUser_firstName()+" "+User.getInstance().getUser_lastName();
        ((TextView) findViewById(R.id.profile_useremail)).setText(User.getInstance().getUser_email());
        ((TextView)findViewById(R.id.profile_username)).setText(User.getInstance().getUser_name());
        ((TextView)findViewById(R.id.profile_usersurname)).setText(nameSurname);
        ImageView profileImg=(ImageView)findViewById(R.id.imageview_profile);

        //- INVISIBLE CONTACT -No contact data for user at this time
        ((TextView)findViewById(R.id.profile_user_contact)).setVisibility(View.INVISIBLE);


        //Load Profile Image
        String imgUrl = App.SitePath+"uploads/profile_images/"+ User.getInstance().getUser_profile_picture().toString(); // URL of the image
        Picasso.with(getApplication()).load(imgUrl).placeholder(R.drawable.placeholder_user).into(profileImg);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.logout_profile_menu:
                Map<String, String> map = new HashMap<String,String>();
                map.put("op", "logout");

                User.userLogout(
                        App.SitePath + "api/auth.php?token=" + App.youbakuToken + "&apikey=" + App.youbakuAPIKey,
                        map,
                        ProfilActivity.this
                );



                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
