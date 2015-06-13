//
//  CategoryList
//
//  Places Near
//  Created by Mobigo Bilişim Teknolojileri
//  Copyright (c) 2015 Mobigo Bilişim Teknolojileri. All rights reserved.
//

package com.youbaku.apps.placesnear.category;

import com.youbaku.apps.placesnear.App;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.util.ArrayList;
import java.util.List;

public class CategoryList{
    private ArrayList<Category> list;
    private static CategoryList instance;
    private static FavoriteCategory fav;
    private CategoryDownloaded downloader;
    private boolean inProgress=true;
    private static boolean downloadError=false;

    private CategoryList(){

    }

    public static CategoryList getCategoryList(CategoryDownloaded downloader){

        if(instance==null){
            instance=new CategoryList();
            instance.downloader=downloader;
            instance.pullList();
        }else{
            downloader.categoryDownloaded(instance.list);
        }
        if(downloadError)
            instance.pullList();
        return instance;
    }

    public static CategoryList getCategoryList(){
        return instance;
    }

    private void pullList(){
        ParseQuery<ParseObject> query=ParseQuery.getQuery(App.PARSE_CATEGORIES);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e==null){
                    downloadError=false;
                    list=new ArrayList<Category>();
                    FavoriteCategory f=new FavoriteCategory();
                    list.add(f);
                    f.color=App.DefaultActionBarColor;
                    f.favourite=true;
                    for(int i=0;i<parseObjects.size();i++){
                        ParseObject p=parseObjects.get(i);
                        final Category c=new Category();
                        c.setName(p.getJSONObject("name"));
                        c.title=p.getString("title")+"";
                        c.color=p.getString("color")+"";
                        c.objectId=p.getObjectId()+"";
                        ParseFile marker=p.getParseFile("pinicon");
                        c.markerURL=marker.getUrl();
                        ParseFile icon=p.getParseFile("icon");
                        c.iconURL=icon.getUrl();
                        list.add(c);
                    }

                    downloader.categoryDownloaded(list);
                }else{
                    downloadError=true;
                    downloader.categoryDownloadError();
                    e.printStackTrace();
                }
            }
        }

        );
    }

    public boolean isInProgress(){
        return inProgress;
    }

    public int size(){
        return list.size();
    }

    public Category get(int index){
        return list.get(index);
    }

    public ArrayList<Category> getList(){
        return list;
    }

    public static Category getCategory(String Id){
        if(instance==null)
            return null;
        for(int i=0;i<instance.list.size();i++){
            if(instance.list.get(i).objectId.equals(Id))
                return instance.list.get(i);
        }
        return null;
    }

    public static Category getFavourite(){
        if(fav==null){
            fav=new FavoriteCategory();
        }
        return fav;
    }
}