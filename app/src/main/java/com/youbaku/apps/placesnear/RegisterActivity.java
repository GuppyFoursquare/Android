/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.youbaku.apps.placesnear.utils.User;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends Activity {

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Set views
        setContentView(R.layout.activity_register);
        switchToMain();

        Button backBtn=(Button)findViewById(R.id.registerGoBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button registerBtn=(Button)findViewById(R.id.registerButton);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get register value first
                String registerName =  ((EditText) findViewById(R.id.registerName)).getText().toString();
                String registerSurname = ((EditText) findViewById(R.id.registerSurname)).getText().toString();
                String registerUsername = ((EditText) findViewById(R.id.registerUsername)).getText().toString();
                String registerPassword = ((EditText) findViewById(R.id.registerPassword)).getText().toString();
                String registerAgainPassword = ((EditText) findViewById(R.id.registerAgainPassword)).getText().toString();
                String registerEmail = ((EditText) findViewById(R.id.registerEmail)).getText().toString();

                //Check input values
                if(registerName.isEmpty()||registerSurname.isEmpty()||registerUsername.isEmpty()||registerPassword.isEmpty()||registerAgainPassword.isEmpty()||registerEmail.isEmpty()){
                    Toast.makeText(getApplicationContext() , getResources().getString(R.string.formvalidationmessage) , Toast.LENGTH_LONG).show();
                    switchToMain();
                }
                else {
                    switchToLoading();
                }



                if(registerPassword.equalsIgnoreCase(registerAgainPassword)){

                    Map<String, String> map = new HashMap<String,String>();
                    map.put("op", "register");
                    map.put("mem_first_name", registerName);
                    map.put("mem_last_name", registerSurname);
                    map.put("mem_user_name", registerUsername);
                    map.put("mem_email", registerEmail);
                    map.put("mem_password", registerPassword);
                    map.put("cmem_password", registerAgainPassword);

                    User.userRegister(
                            App.SitePath + "api/auth.php?token=" + App.youbakuToken + "&apikey=" + App.youbakuAPIKey,
                            map,
                            activity
                    );

                }else{
                    switchToMain();
                    Toast.makeText(getApplicationContext() , "Password Mismatch" , Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void switchToLoading(){
        activity.findViewById(R.id.register_load).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.register_main).setVisibility(View.INVISIBLE);
    }

    private void switchToMain(){
        activity.findViewById(R.id.register_main).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.register_load).setVisibility(View.INVISIBLE);
    }

    public static void switchToLoading(Activity activity){
        activity.findViewById(R.id.register_load).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.register_main).setVisibility(View.INVISIBLE);
    }

    public static void switchToMain(Activity activity){
        activity.findViewById(R.id.register_main).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.register_load).setVisibility(View.INVISIBLE);
    }
}
