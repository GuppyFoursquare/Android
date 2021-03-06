/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.utils.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfilActivity extends ActionBarActivity {
    private ActionBar actionBar;
    private Activity activity;
    private AlertDialog.Builder logoutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profil);
        this.activity = this;

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
        ((TextView)findViewById(R.id.profile_user_contact)).setText(User.getInstance().getUser_contact());

        ImageView profileImg=(ImageView)findViewById(R.id.imageview_profile);


        //Load Profile Image
        String imgUrl = App.SitePath+"uploads/profile_images/"+ User.getInstance().getUser_profile_picture().toString(); // URL of the image
        Picasso.with(getApplication()).load(imgUrl).placeholder(R.drawable.placeholder_user).into(profileImg);




        // -------------------------------------------------------------------- //
        // -------------------------------------------------------------------- //
        // -------------------------------------------------------------------- //
        logoutDialog = new AlertDialog.Builder(this);
        View logoutView = getLayoutInflater().inflate(R.layout.dialog_logout_layout, null);
        logoutDialog.setView(logoutView);

            /* When positive  is clicked */
        logoutDialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel(); // Your custom code

                Map<String, String> map = new HashMap<String,String>();
                map.put("op", "logout");

                User.userLogout(
                        App.SitePath + "api/auth.php?token=" + App.getYoubakuToken() + "&apikey=" + App.getYoubakuAPIKey(),
                        map,
                        ProfilActivity.this
                );

            }
        });

            /* When negative  button is clicked*/
        logoutDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Your custom code
            }
        });





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
                logoutDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
