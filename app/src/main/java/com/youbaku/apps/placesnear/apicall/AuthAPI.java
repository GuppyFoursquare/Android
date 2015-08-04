package com.youbaku.apps.placesnear.apicall;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.utils.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kemal on 03/08/15.
 */
public class AuthAPI {




//    public static void callLogin(final Activity activity) {
//
//
//        String loginUrl = App.SitePath + "api/auth.php?token=" + App.getYoubakuToken() + "&apikey=" + App.getYoubakuAPIKey() + "&op=login";
//
//        Map<String, String> map = new HashMap<>();
//        map.put("name", ((EditText) activity.findViewById(R.id.username)).getText().toString());
//        map.put("pass", ((EditText) alertView.findViewById(R.id.password)).getText().toString());
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.POST, loginUrl, new JSONObject(map), new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        try {
//
//                            if (response.getString("status").equalsIgnoreCase("SUCCESS")) {
//
//                                JSONObject responseContent = response.getJSONObject("content");
//                                User.getInstance().setUser_name(responseContent.getString("usr_username"));
//                                User.getInstance().setUser_email(responseContent.getString("usr_email"));
//
//                                MainActivity.doLogin.setIcon(R.drawable.ic_profilelogo);
//
//                            } else {
//                                Toast.makeText(currentActivity, response.getString("status"), Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//
//                            App.sendErrorToServer(currentActivity, getClass().getName(), "login Errror---", e.getMessage());
//                            Toast.makeText(currentActivity, "MainActivity login()", Toast.LENGTH_SHORT).show();
//
//                            e.printStackTrace();
//                            return;
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//
//        // Add the request to the queue
//        VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);
//
//    }


}
