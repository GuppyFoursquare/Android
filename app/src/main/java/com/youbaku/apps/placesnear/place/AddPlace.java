//
//  AddPlace
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.place;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.utils.Category;
import com.youbaku.apps.placesnear.category.CategoryList;
import com.youbaku.apps.placesnear.category.adapters.CategorySpinnerAdapter;
import com.youbaku.apps.placesnear.location.MapsActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddPlace extends Fragment {
    public static final int LOCATION_SELECTED=13524;
    public static final int IMAGE_CHOOSED_FROM_GALLERY=13525;
    public static final int IMAGE_CHOOSED_FROM_CAMERA=13526;
    public Place p;
    private CategoryList cList;
    private File photoFile;
    public boolean editable=true;

    public AddPlace() {}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_place,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((App)getActivity().getApplication()).track(App.ANALYSIS_ADD_PLACE);

        ((RelativeLayout)getView().findViewById(R.id.main_add_place)).setBackgroundColor(Color.parseColor(App.BackgroundGrayColor));
        ((EditText)getView().findViewById(R.id.name_add_place)).addTextChangedListener(nameWatcher);
        ((EditText)getView().findViewById(R.id.address_add_place)).addTextChangedListener(addressWatcher);
        ((EditText)getView().findViewById(R.id.description_add_place)).addTextChangedListener(descWatcher);
        ((EditText)getView().findViewById(R.id.web_page_add_place)).addTextChangedListener(webWatcher);
        ((EditText)getView().findViewById(R.id.phone_add_palace)).addTextChangedListener(phoneWatcher);
        ((EditText)getView().findViewById(R.id.facebook_add_place)).addTextChangedListener(faceWatcher);
        ((EditText)getView().findViewById(R.id.twitter_add_place)).addTextChangedListener(twitWatcher);

        cList=CategoryList.getCategoryList();
        CategorySpinnerAdapter adap=new CategorySpinnerAdapter(getActivity(), cList);
        Spinner cat=((Spinner)getView().findViewById(R.id.categories_add_place));
        cat.setAdapter(adap);
        cat.setOnItemSelectedListener(categorySelected);

        setEditable(true);

        p=new Place();
        if(App.moderatePlaces)
            p.isActive=false;
        else
            p.isActive=true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==getActivity().RESULT_OK && requestCode==LOCATION_SELECTED){
            double a=data.getDoubleExtra(MapsActivity.LONGTİTUDE,0.0);
            double b=data.getDoubleExtra(MapsActivity.LATITUDE,0.0);

            try {
                List<Address> geo=new Geocoder(getActivity()).getFromLocation(b, a, 1);
                if(!geo.isEmpty()){
                    p.setLocation(b,a);
                    String adres=geo.get(0).getAddressLine(0)+" "+geo.get(0).getAddressLine(1);
                    ((TextView)getView().findViewById(R.id.select_location_add_place)).setText(b+" , "+a);
                    ((TextView) getView().findViewById(R.id.address_add_place)).setText(adres);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(resultCode==getActivity().RESULT_OK && requestCode==IMAGE_CHOOSED_FROM_CAMERA){
            ((TextView)getView().findViewById(R.id.add_photo_text_add_place)).setVisibility(View.GONE);
            ImageView im=((ImageView)getView().findViewById(R.id.photo_add_place));
            im.setVisibility(View.VISIBLE);
            p.file=photoFile;
            BitmapFactory.Options opt=new BitmapFactory.Options();
            opt.inJustDecodeBounds=true;
            String adres=photoFile.getAbsolutePath();
            BitmapFactory.decodeFile(adres,opt);
            opt.inSampleSize=App.getInSampleSize(opt,App.MaximumPhotoHeight,App.MaximumPhotoHeight);
            opt.inJustDecodeBounds=false;
            p.photo= BitmapFactory.decodeFile(adres,opt);
            im.setImageBitmap(p.photo);
        }else if(resultCode==getActivity().RESULT_OK && requestCode==IMAGE_CHOOSED_FROM_GALLERY){
            Uri ur=data.getData();
            String adres=App.getPath(getActivity(),ur);
            ((TextView)getView().findViewById(R.id.add_photo_text_add_place)).setVisibility(View.GONE);
            ImageView im=((ImageView)getView().findViewById(R.id.photo_add_place));
            im.setVisibility(View.VISIBLE);
            File fi=new File(adres);
            p.file=fi;
            BitmapFactory.Options opt=new BitmapFactory.Options();
            opt.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(adres,opt);
            opt.inSampleSize=App.getInSampleSize(opt,App.MaximumPhotoHeight,App.MaximumPhotoHeight);
            opt.inJustDecodeBounds=false;
            p.photo= BitmapFactory.decodeFile(adres,opt);
            im.setImageBitmap(p.photo);
        }
    }

    @Override
    public void onDestroy() {
        ((ActionBarActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        super.onDestroy();
    }

    public boolean savePlace(){
        if(!App.checkInternetConnection(getActivity()))
            App.showInternetError(getActivity());
        if(p.name.equals("") || p.name==null){
            Toast.makeText(getActivity(), getResources().getString(R.string.formvalidationmessage), Toast.LENGTH_LONG).show();
            return false;
        }
        if(p.category.equals("") || p.category==null){
            Toast.makeText(getActivity(),getResources().getString(R.string.formvalidationmessage),Toast.LENGTH_LONG).show();
            return false;
        }
        if(!p.isLocationSet()){
            Toast.makeText(getActivity(),getResources().getString(R.string.formvalidationmessage),Toast.LENGTH_LONG).show();
            return false;
        }
        ((ActionBarActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(true);
        setEditable(false);

        ParseGeoPoint pGeo=new ParseGeoPoint(p.getLatitude(),p.getLongitude());
        final ParseObject place=new ParseObject("Places");
        place.put(Place.POSITION,pGeo);
        place.put(Place.NAME,p.name);
        place.put(Place.CATEGORY,p.category);
        place.put(Place.DESCRIPTION,p.description);
        place.put(Place.ADDRESS,p.address);
        place.put(Place.WEBPAGE,p.web);
        place.put(Place.PHONE,p.phone);
        place.put(Place.TWITTER,p.twitter);
        place.put(Place.FACEBOOK,p.facebook);
        place.put(Place.ISACTIVE,p.isActive);
        place.put(Place.LIKES,p.likes+"");
        place.put(Place.RATING,p.rating+"");
        place.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(p.photo==null) {
                    saveDone();
                    return;
                }
                ByteArrayOutputStream out=new ByteArrayOutputStream();
                p.photo.compress(Bitmap.CompressFormat.JPEG,100,out);
                byte[] arr=out.toByteArray();
                ParseFile file=new ParseFile(p.file.getName(), arr);
                ParseObject obj = new ParseObject("Photos");
                obj.put(Place.PLACE, place.getObjectId());
                obj.put(Place.PHOTO, file);
                obj.put(Place.ISACTIVE,true);
                obj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null) {
                            e.printStackTrace();
                            return;
                        }
                        saveDone();
                    }
                });
            }
        });
        return true;
    }

    private void saveDone(){
        ((ActionBarActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        editable=true;
        Toast.makeText(getActivity(), getResources().getString(R.string.donebuttonlabel), Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void setEditable(boolean editable){
        ((EditText)getView().findViewById(R.id.name_add_place)).setFocusable(editable);
        ((EditText)getView().findViewById(R.id.address_add_place)).setFocusable(editable);
        ((EditText)getView().findViewById(R.id.description_add_place)).setFocusable(editable);
        ((EditText)getView().findViewById(R.id.web_page_add_place)).setFocusable(editable);
        ((EditText)getView().findViewById(R.id.phone_add_palace)).setFocusable(editable);
        ((EditText)getView().findViewById(R.id.facebook_add_place)).setFocusable(editable);
        ((EditText)getView().findViewById(R.id.twitter_add_place)).setFocusable(editable);
        ((Spinner)getView().findViewById(R.id.categories_add_place)).setEnabled(editable);
        ((TextView)getView().findViewById(R.id.select_location_add_place)).setOnClickListener(editable ? toMap:null);
        ((TextView)getView().findViewById(R.id.add_photo_text_add_place)).setOnClickListener(editable ? addPhoto:null);
        ((ImageView)getView().findViewById(R.id.photo_add_place)).setOnClickListener(editable ? addPhoto:null);
        this.editable=editable;
    }

    AdapterView.OnItemSelectedListener categorySelected=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ArrayList<Category> c=cList.getList();
            p.category=c.get(position).objectId;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    View.OnClickListener toMap=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                        Intent in=new Intent(getActivity(), MapsActivity.class);
                        startActivityForResult(in,LOCATION_SELECTED);

        }
    };

    View.OnClickListener addPhoto=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String[] items=new String[3];
            items[0]=getResources().getString(R.string.from_gallery);
            items[1]=getResources().getString(R.string.from_camera);
            items[2]=getResources().getString(R.string.alertcancelbuttonlabel);

            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.selectphotobuttonlabel));
            builder.setItems(items,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (items[which].equals(getResources().getString(R.string.from_gallery))){
                        ((ActionBarActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                        Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(in,IMAGE_CHOOSED_FROM_GALLERY);
                    }else if (items[which].equals(getResources().getString(R.string.from_camera))){
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if(intent.resolveActivity(getActivity().getPackageManager())!=null){
                            photoFile=new File(getActivity().getExternalFilesDir(null),"temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                            startActivityForResult(intent,IMAGE_CHOOSED_FROM_CAMERA);
                        }
                    }else {

                    }
                }
            });
            builder.show();
        }
    };

    TextWatcher nameWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            p.name=s.toString();
        }
    };

    TextWatcher addressWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            p.address=s.toString();
        }
    };

    TextWatcher descWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            p.description=s.toString();
        }
    };

    TextWatcher webWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            p.web=s.toString();
        }
    };

    TextWatcher phoneWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            p.phone=s.toString();
        }
    };

    TextWatcher faceWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            p.facebook=s.toString();
        }
    };

    TextWatcher twitWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            p.twitter=s.toString();
        }
    };

}
