package com.youbaku.apps.placesnear;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.youbaku.apps.placesnear.utils.User;


public class ProfilActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_profil);

        //Set Contents
        ((TextView)findViewById(R.id.profile_useremail)).setText(User.getInstance().getUser_email());
        ((TextView)findViewById(R.id.profile_username)).setText(User.getInstance().getUser_name());
        ((TextView)findViewById(R.id.profile_usersurname)).setText(User.getInstance().getUser_lastName());

        Toast.makeText(this, "Profile picture :: " + User.getInstance().getUser_profile_picture() , Toast.LENGTH_LONG).show();

        ImageButton backBtn=(ImageButton)findViewById(R.id.profileBactBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
