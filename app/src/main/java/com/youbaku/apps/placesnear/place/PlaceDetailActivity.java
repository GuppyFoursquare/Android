/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.place;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.place.comment.CommentListFragment;
import com.youbaku.apps.placesnear.place.comment.CreateComment;
import com.youbaku.apps.placesnear.place.deal.DealListFragment;
import com.youbaku.apps.placesnear.place.favorites.FavoritesManager;
import com.youbaku.apps.placesnear.utils.SubCategory;
import com.youbaku.apps.placesnear.utils.User;

import java.io.File;

public class PlaceDetailActivity extends ActionBarActivity {
    private enum Screen {comments, newComment, deals, detail, senEmail}


    private static final int IMAGE_CHOOSED_FROM_GALLERY = 5009;
    private static final int IMAGE_CHOOSED_FROM_CAMERA = 5010;

    private static Screen screen = Screen.detail;
    private Place p;
    private CreateComment createComment;
    private BookPlaceFragment bookPlaceFrg;
    private MenuItem sendButton;
    private MenuItem favoriteButton;
    private MenuItem bookingPlace;
    private boolean directDetail = false;
    private File photoFile;
    private Activity activity;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        this.activity = this;
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

        ((RelativeLayout) findViewById(R.id.main_activity_place_detail)).setBackgroundColor(Color.parseColor(App.DefaultBackgroundColor));

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
       
    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getSupportActionBar().getCustomView().setVisibility(visible ? View.VISIBLE : View.GONE);
        if (sendButton != null)
            sendButton.setVisible(!visible);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place_detail, menu);
        bookingPlace = menu.getItem(0);




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

            //we don't need add favourites menu item visible at this time.
            favoriteButton.setVisible(false);
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
                if (!App.isAuth(activity)) {
                    loginNeededInfo();
                } else {

                    if(progress!=null){
                        progress.dismiss();
                    }
                    progress = new ProgressDialog(activity, AlertDialog.THEME_HOLO_LIGHT);
                    progress.setTitle("Sending Comment");
                    progress.setMessage("Please wait while sending comment");
                    progress.show();

                    createComment.saveComment(activity,progress);
                }
                break;

            case R.id.book_place_detail_menu:
                if (!App.checkInternetConnection(this)) {
                    App.showInternetError(this);
                    break;
                }
                bookPlaceFrg=new BookPlaceFragment();
                getSupportFragmentManager().beginTransaction().addToBackStack("bookplace").replace(R.id.main_activity_place_detail, bookPlaceFrg).commit();
                setScreen(Screen.senEmail);


                break;

            case R.id.write_review_detail_menu:
                createComment = new CreateComment();
                getSupportFragmentManager().beginTransaction().addToBackStack("createcomment").replace(R.id.main_activity_place_detail, createComment).commit();
                setScreen(Screen.newComment);
                directDetail = true;
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

    private void loginNeededInfo() {

        Toast.makeText(getApplication(), "You should login first", Toast.LENGTH_LONG).show();
        AlertDialog.Builder bu = new AlertDialog.Builder(PlaceDetailActivity.this);
        bu.setMessage(getResources().getString(R.string.loginrequirement));
        bu.setNegativeButton(getResources().getString(R.string.alertokbuttonlabel), null);
        bu.setPositiveButton(getResources().getString(R.string.loginlabel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlaceDetailActivity.this);
                LayoutInflater inflater = PlaceDetailActivity.this.getLayoutInflater();
                final View alertView = inflater.inflate(R.layout.dialog_login_layout, null);
                alertDialog.setView(alertView);

                             /* When positive  is clicked */
                alertDialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //Login Request
                        User.userLogin(PlaceDetailActivity.this);
                    }
                });

        /* When negative  button is clicked*/
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
            case senEmail:
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
            comlis.setComments(p.getComments());
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
