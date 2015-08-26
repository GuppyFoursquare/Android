/**
 * COPYRIGHT (C) 2015 Caspian Soft. All Rights Reserved.
 */

package com.youbaku.apps.placesnear.place;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.youbaku.apps.placesnear.App;
import com.youbaku.apps.placesnear.R;
import com.youbaku.apps.placesnear.apicall.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookPlaceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookPlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookPlaceFragment extends Fragment implements OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;

    Button buttonSend;
    EditText contact;
    EditText textSubject;
    EditText textMessage;

    private EditText date;
    private EditText time;
    private EditText peopleNum;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    // ----- ALERT DIALOG PARAMETER -----
    // -----------------------------------
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;

    private SimpleDateFormat dateFormatter;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookPlaceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookPlaceFragment newInstance(String param1, String param2) {
        BookPlaceFragment fragment = new BookPlaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BookPlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_book_place, container, false);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");


        date = (EditText) view.findViewById(R.id.editTextDate);
        date.setInputType(InputType.TYPE_NULL);

        time=(EditText)view.findViewById(R.id.editTextTime);
        time.setInputType(InputType.TYPE_NULL);

        peopleNum=(EditText)view.findViewById(R.id.editTextNumberPeople);

        buttonSend = (Button) view.findViewById(R.id.buttonSend);

        textSubject = (EditText) view.findViewById(R.id.editTextSubject);
        textMessage = (EditText) view.findViewById(R.id.editTextMessage);

        contact=(EditText)view.findViewById(R.id.editTextContact);

        setDateTimeField();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            String pEmail;

            @Override
            public void onClick(View v) {

                Map<String,String> parameters = new HashMap<String,String>();
                parameters.put("op", "book");
                parameters.put("plc_id", getArguments().getString("PLCID"));
                parameters.put("book_comer_number", peopleNum.getText().toString());
                parameters.put("book_date", date.getText().toString());
                parameters.put("book_time", time.getText().toString());
                parameters.put("book_contact", contact.getText().toString());
                parameters.put("book_detail", textMessage.getText().toString());

                String url = App.SitePath + "api/auth.php?token=" + App.getYoubakuToken() + "&apikey=" + App.getYoubakuAPIKey();

                // Request a json response
                JSONObject params = (parameters!=null) ? (new JSONObject(parameters)) : (null);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {


                                if (App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("SUCCESS")) {

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                                        // set title
                                        alertDialogBuilder.setTitle("Reservation Info");

                                        // set dialog message
                                        alertDialogBuilder
                                                .setMessage("SUCCESS Operation")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //getFragmentManager().beginTransaction().addToBackStack("bookplace").replace(R.id.main_activity_place_detail, new PlaceDetailFragment()).commit();
                                                        getFragmentManager().popBackStack();
                                                    }
                                                });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();

                                }else if(App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("FAILURE_AUTH")){

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                                        // set title
                                        alertDialogBuilder.setTitle("Reservation Info");

                                        // set dialog message
                                        alertDialogBuilder
                                                .setMessage("You should first login")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // if this button is clicked, close
                                                        // current activity
                                                        //MainActivity.this.finish();
                                                    }
                                                })
                                                .setNegativeButton("Cance", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // if this button is clicked, just close
                                                        // the dialog box and do nothing
                                                        dialog.cancel();
                                                    }
                                                });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();

                                }else if(App.getJsonValueIfExist(response, App.RESULT_STATUS).equalsIgnoreCase("FAILURE_PARAM_MISMATCH")){

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                                        // set title
                                        alertDialogBuilder.setTitle("Reservation Info");

                                        // set dialog message
                                        alertDialogBuilder
                                                .setMessage(App.getJsonValueIfExist(response, App.RESULT_CONTENT))
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                }


                            }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            //App.sendErrorToServer(getActivity(), getClass().getName(), "fetchGenericPlaceList", "onErrorResponse----" + error.getMessage());
                        }
                    });

                VolleySingleton.getInstance().getRequestQueue().add(jsObjRequest);

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View v) {

        if(v == date) {
            datePickerDialog.show();
        }
        else if(v==time){
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(),"TimePicker");
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void setDateTimeField() {
        date.setOnClickListener(this);
        time.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        //Date Area
        datePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        //Time Area


    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);


            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user

            EditText time=(EditText)getActivity().findViewById(R.id.editTextTime);
            time.setText(String.valueOf(hourOfDay)
                    + ":" + String.valueOf(minute) + "\n");
        }
    }
}
