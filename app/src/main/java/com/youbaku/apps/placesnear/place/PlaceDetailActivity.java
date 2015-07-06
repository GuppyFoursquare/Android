//
//  PlaceDetailActivity
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.MainActivity;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;
import com.youbaku.apps.placesnear.place.comment.CommentListFragment;
import com.youbaku.apps.placesnear.place.comment.CreateComment;
import com.youbaku.apps.placesnear.place.deal.DealListFragment;
import com.youbaku.apps.placesnear.place.favorites.FavoritesManager;
import com.youbaku.apps.placesnear.utils.SubCategory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PlaceDetailActivity extends ActionBarActivity {
    private enum Screen {comments, newComment, deals, detail}

    ;
    private static final int IMAGE_CHOOSED_FROM_GALLERY = 5009;
    private static final int IMAGE_CHOOSED_FROM_CAMERA = 5010;

    private static Screen screen = Screen.detail;
    private Place p;
    private CreateComment createComment;
    private MenuItem sendButton;
    private MenuItem favoriteButton;
    private boolean directDetail = false;
    private File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_place_detail);
        p = Place.FOR_DETAIL;

        ActionBar act = ((ActionBar) getSupportActionBar());
        act.setBackgroundDrawable(new ColorDrawable(Color.parseColor(App.GreenColor)));
        act.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.buttonback));
        act.setTitle(p.name);
        act.setDisplayShowCustomEnabled(true);
        act.setSubtitle(SubCategory.SELECTED_SUB_CATEGORY_NAME);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = 0x05;
        ProgressBar pro = new ProgressBar(this);
        pro.setVisibility(View.GONE);
        pro.setIndeterminate(true);
        act.setCustomView(pro, params);

        ((RelativeLayout) findViewById(R.id.main_activity_place_detail)).setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));

        PlaceDetailFragment det = new PlaceDetailFragment(Color.parseColor(App.GreenColor));
        det.setOnCommentClick(toCommentList);
        det.setOnDealClick(toDealList);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_place_detail, det).commit();
        if (screen == Screen.deals) {
            DealListFragment deals = new DealListFragment();
            deals.setDeals(p.deals);
            setScreen(Screen.deals);
            getSupportFragmentManager().beginTransaction().addToBackStack("deals").replace(R.id.main_activity_place_detail, deals).commit();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if(resultCode==RESULT_OK && requestCode==IMAGE_CHOOSED_FROM_GALLERY){
            if(!App.checkInternetConnection(this)){
                App.showInternetError(this);
                return;
            }
            setSupportProgressBarIndeterminateVisibility(true);
            Toast.makeText(getApplicationContext(),R.string.uplaodinglabel,Toast.LENGTH_LONG).show();

            Uri ur=data.getData();
            String adres=App.getPath(this,ur);
            File fi=new File(adres);
            p.file=fi;
            BitmapFactory.Options opt=new BitmapFactory.Options();
            opt.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(adres,opt);
            opt.inSampleSize=App.getInSampleSize(opt,App.MaximumPhotoHeight,App.MaximumPhotoHeight);
            opt.inJustDecodeBounds=false;
            p.photo= BitmapFactory.decodeFile(adres,opt);

            ByteArrayOutputStream out=new ByteArrayOutputStream();
            p.photo.compress(Bitmap.CompressFormat.JPEG,100,out);
            byte[] arr=out.toByteArray();
            ParseFile file=new ParseFile(p.file.getName(), arr);
            ParseObject obj = new ParseObject("Photos");
            obj.put(Place.PLACE, p.id);
            obj.put(Place.PHOTO, file);
            obj.put(Place.ISACTIVE,!App.moderatePhotos);
            obj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    setSupportProgressBarIndeterminateVisibility(false);
                    if(e!=null){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),R.string.uploadfailedlabel,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(),R.string.uploadsuccesslabel,Toast.LENGTH_SHORT).show();
                }
            });
        }else if(resultCode==RESULT_OK && requestCode==IMAGE_CHOOSED_FROM_CAMERA){
            if(!App.checkInternetConnection(this)){
                App.showInternetError(this);
                return;
            }
            setSupportProgressBarIndeterminateVisibility(true);
            Toast.makeText(getApplicationContext(),R.string.uplaodinglabel,Toast.LENGTH_LONG).show();

            p.file=photoFile;
            String adres=photoFile.getAbsolutePath();
            BitmapFactory.Options opt=new BitmapFactory.Options();
            opt.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(adres,opt);
            opt.inSampleSize=App.getInSampleSize(opt,App.MaximumPhotoHeight,App.MaximumPhotoHeight);
            opt.inJustDecodeBounds=false;
            p.photo= BitmapFactory.decodeFile(adres,opt);

            ByteArrayOutputStream out=new ByteArrayOutputStream();
            p.photo.compress(Bitmap.CompressFormat.JPEG,100,out);
            byte[] arr=out.toByteArray();
            ParseFile file=new ParseFile(p.file.getName(), arr);
            ParseObject obj = new ParseObject("Photos");
            obj.put(Place.PLACE, p.id);
            obj.put(Place.PHOTO, file);
            obj.put(Place.ISACTIVE,!App.moderatePhotos);
            obj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    setSupportProgressBarIndeterminateVisibility(false);
                    if(e!=null){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),R.string.uploadfailedlabel,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(),R.string.uploadsuccesslabel,Toast.LENGTH_SHORT).show();
                }
            });
        }*/
    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getSupportActionBar().getCustomView().setVisibility(visible ? View.VISIBLE : View.GONE);
        if (sendButton != null)
            sendButton.setVisible(!visible);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (p.liked)
            getMenuInflater().inflate(R.menu.menu_place_detail_liked, menu);
        else
            getMenuInflater().inflate(R.menu.menu_place_detail, menu);
        favoriteButton = menu.findItem(R.id.favourites_detail_menu);
        if (p.isFavourite)
            favoriteButton.setTitle(getResources().getString(R.string.removefromfavoriteslabel));
        else
            favoriteButton.setTitle(getResources().getString(R.string.addtofavoriteslabel));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if ((screen == Screen.newComment && createComment.isSaving()) || (createComment != null && createComment.isSaving())) {
            return true;
        }
        menu.clear();
        if (screen != Screen.detail) {
            getMenuInflater().inflate(R.menu.second_menu_place_detail, menu);
            sendButton = menu.getItem(3);
            switch (screen) {
                case comments:
                    menu.getItem(0).setVisible(true);
                    break;
                case newComment:
                    sendButton.setVisible(true);
                    break;
            }
        } else {
            if (p.liked)
                getMenuInflater().inflate(R.menu.menu_place_detail_liked, menu);
            else
                getMenuInflater().inflate(R.menu.menu_place_detail, menu);

            favoriteButton = menu.findItem(R.id.favourites_detail_menu);
            if (p.isFavourite)
                favoriteButton.setTitle(getResources().getString(R.string.removefromfavoriteslabel));
            else
                favoriteButton.setTitle(getResources().getString(R.string.addtofavoriteslabel));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (screen == Screen.newComment && createComment.isSaving()) {
            return true;
        }
        int id = item.getItemId();
        final Activity tt = this;

        switch (id) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                if (screen != Screen.detail) {
                    getSupportFragmentManager().popBackStack();
                    goBack();
                    return true;
                }
                return false;

            case R.id.write_review_second_detail_menu:
                createComment = new CreateComment();
                getSupportFragmentManager().beginTransaction().addToBackStack("createcomment").replace(R.id.main_activity_place_detail, createComment).commit();
                setScreen(Screen.newComment);
                break;

            case R.id.send_review_second_detail_menu:
                if (!App.checkInternetConnection(this)) {
                    App.showInternetError(this);
                    break;
                }
                if (App.userapikey != null) {
                    createComment.saveComment();
                } else {
                    login();
                }
                break;

            case R.id.like_place_detail_menu:
                if (!App.checkInternetConnection(this)) {
                    App.showInternetError(this);
                    break;
                }
                tt.closeOptionsMenu();
                setSupportProgressBarIndeterminateVisibility(true);
                ParseQuery obj = new ParseQuery(App.PARSE_PLACES);
                obj.whereEqualTo(Place.ID, p.id);
                obj.getFirstInBackground(new GetCallback() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        int l = Integer.parseInt(parseObject.getString(Place.LIKES)) + 1;
                        parseObject.put(Place.LIKES, l + "");
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    e.printStackTrace();
                                    return;
                                }
                                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(tt).edit();
                                edit.putBoolean(p.id, true);
                                edit.commit();
                                p.liked = true;
                                supportInvalidateOptionsMenu();
                                setSupportProgressBarIndeterminateVisibility(false);

                            }
                        });
                    }
                });
                break;

            case R.id.write_review_detail_menu:
                createComment = new CreateComment();
                getSupportFragmentManager().beginTransaction().addToBackStack("createcomment").replace(R.id.main_activity_place_detail, createComment).commit();
                setScreen(Screen.newComment);
                directDetail = true;
                break;

            case R.id.upload_photo_detail_menu:
                if (!App.checkInternetConnection(this)) {
                    App.showInternetError(this);
                    break;
                }
                final String[] items = new String[3];
                items[0] = getResources().getString(R.string.from_gallery);
                items[1] = getResources().getString(R.string.from_camera);
                items[2] = getResources().getString(R.string.alertcancelbuttonlabel);

                AlertDialog.Builder builder = new AlertDialog.Builder(tt);
                builder.setTitle(getResources().getString(R.string.selectphotobuttonlabel));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals(getResources().getString(R.string.from_gallery))) {
                            Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(in, IMAGE_CHOOSED_FROM_GALLERY);
                        } else if (items[which].equals(getResources().getString(R.string.from_camera))) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(tt.getPackageManager()) != null) {
                                photoFile = new File(tt.getExternalFilesDir(null), "temp.jpg");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                startActivityForResult(intent, IMAGE_CHOOSED_FROM_CAMERA);
                            }
                        } else {

                        }
                    }
                });
                builder.show();
                break;

            case R.id.favourites_detail_menu:
                if (p.isFavourite) {
                    FavoritesManager.removeFavorite(this, p.id);
                } else {
                    FavoritesManager.saveFavorite(this, p.id);
                }

                p.isFavourite = p.isFavourite ? false : true;
                invalidateOptionsMenu();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void login() {

        Toast.makeText(getApplication(), "You should login first", Toast.LENGTH_LONG).show();
        AlertDialog.Builder bu = new AlertDialog.Builder(PlaceDetailActivity.this);
        bu.setMessage(getResources().getString(R.string.loginrequirement));
        bu.setNegativeButton(getResources().getString(R.string.alertokbuttonlabel), null);
        bu.setPositiveButton(getResources().getString(R.string.loginlabel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                //Toast.makeText(getApplicationContext(), "Login is clicked", Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlaceDetailActivity.this);
                LayoutInflater inflater = PlaceDetailActivity.this.getLayoutInflater();
                final View alertView = inflater.inflate(R.layout.dialog_login_layout, null);
                alertDialog.setView(alertView);

                             /* When positive  is clicked */
                alertDialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel(); // Your custom code

                        String loginUrl = App.SitePath + "api/auth.php?op=login";

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", ((EditText) alertView.findViewById(R.id.username)).getText().toString());
                        map.put("pass", ((EditText) alertView.findViewById(R.id.password)).getText().toString());

                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.POST, loginUrl, new JSONObject(map), new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {

                                            if (response.getString("status").equalsIgnoreCase("SUCCESS")) {

                                                JSONObject responseContent = response.getJSONObject("content");
                                                App.username = responseContent.getString("usr_username");
                                                App.userapikey = responseContent.getString("usr_apikey");

                                                MainActivity.doLogin.setIcon(R.drawable.placeholder_user);
                                                Toast.makeText(getApplication(), App.username + " - " + App.userapikey, Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(getApplication(), response.getString("status"), Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (JSONException e) {

                                            AlertDialog.Builder bu = new AlertDialog.Builder(PlaceDetailActivity.this);
                                            bu.setMessage(getResources().getString(R.string.loadingdataerrormessage));
                                            bu.setNegativeButton(getResources().getString(R.string.alertokbuttonlabel), null);
                                            bu.setPositiveButton(getResources().getString(R.string.retrybuttonlabel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            bu.show();
                                            e.printStackTrace();
                                            return;
                                        }
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub

                                    }
                                });

                        // Add the request to the queue
                        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);

                    }
                });

        /* When negative  button is clicked*/
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Your custom code
                    }
                });

                alertDialog.show();

            }
        });
        bu.setIcon(android.R.drawable.ic_dialog_alert);
        bu.show();

    }

    @Override
    public void onBackPressed() {
        if (screen == Screen.newComment && createComment.isSaving()) {
            return;
        }
        if (screen != Screen.detail) {
            goBack();
        }
        super.onBackPressed();
    }

    public void goBack() {
        switch (screen) {
            case comments:
                setScreen(Screen.detail);
                break;
            case newComment:
                if (!directDetail)
                    setScreen(Screen.comments);
                else {
                    setScreen(Screen.detail);
                    directDetail = false;
                }
                break;
            case deals:
                setScreen(Screen.detail);
                break;
        }
    }

    private void setScreen(Screen sc) {
        screen = sc;
        supportInvalidateOptionsMenu();
    }


    View.OnClickListener toCommentList = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CommentListFragment comlis = new CommentListFragment();
            comlis.setComments(p.comments);
            setScreen(Screen.comments);
            getSupportFragmentManager().beginTransaction().addToBackStack("comments").replace(R.id.main_activity_place_detail, comlis).commit();
        }
    };

    View.OnClickListener toDealList = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DealListFragment deals = new DealListFragment();
            deals.setDeals(p.deals);
            setScreen(Screen.deals);
            getSupportFragmentManager().beginTransaction().addToBackStack("deals").replace(R.id.main_activity_place_detail, deals).commit();
        }
    };
}
