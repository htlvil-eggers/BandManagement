package com.example.stefan.android_client.pkgFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stefan.android_client.R;
import com.example.stefan.android_client.pkgModel.AppearanceRequestWrapper;
import com.example.stefan.android_client.pkgModel.Appointment;
import com.example.stefan.android_client.pkgModel.EnumAppointmentType;
import com.example.stefan.android_client.pkgModel.Location;
import com.example.stefan.android_client.pkgModel.RehearsalRequest;
import com.example.stefan.android_client.pkgModel.WebserviceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RehearsalRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RehearsalRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RehearsalRequestFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";
    private static final String ARG_BAND_NAME = "bandName";

    // TODO: Rename and change types of parameters
    private String username;
    private String password;
    private String bandName;

    private Spinner spnRehearsalRequest;
    private WebView wbvwCalendar;
    private EditText txtFrom;
    private EditText txtTo;
    private Button btnAddAvailableTime;

    private OnFragmentInteractionListener mListener;

    public RehearsalRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RehearsalRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RehearsalRequestFragment newInstance(String username, String password, String bandName) {
        RehearsalRequestFragment fragment = new RehearsalRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_PASSWORD, password);
        args.putString(ARG_BAND_NAME, bandName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rehearsal_request, container, false);

        setComponentReferences(view);
        initializeComponents();
        setEventHandlers();

        return view;
    }

    private void setEventHandlers() {
        spnRehearsalRequest.setOnItemSelectedListener(this);
        btnAddAvailableTime.setOnClickListener(this);
    }

    private void initializeComponents() {
        initializeSpnRehearsalRequest();
        initializeWbvwCalendar();
    }

    private void initializeSpnRehearsalRequest() {
        final Context context = this.getContext();

        new AsyncTask<String,Void,RehearsalRequest[]>() {
            @Override
            protected RehearsalRequest[] doInBackground(String... credentials) {
                return WebserviceManager.getRehearsalRequests(credentials[0]);
            }

            protected void onPostExecute(RehearsalRequest[] rehearsalRequests)
            {
                ArrayAdapter<RehearsalRequest> adapter = new ArrayAdapter<RehearsalRequest> (context, android.R.layout.simple_spinner_dropdown_item, rehearsalRequests);

                spnRehearsalRequest.setAdapter(adapter);
            }
        }.execute(bandName);
    }

    private void initializeWbvwCalendar() {
        WebSettings webSettings = wbvwCalendar.getSettings();
        webSettings.setJavaScriptEnabled(true);

        final RehearsalRequestFragment rehearsalRequestFragment = this;
        final Context me = this.getContext();

        wbvwCalendar.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void getAvailableTime(final long timestamp) {
                new AsyncTask<String,Void,Void>() {
                    @Override
                    protected Void doInBackground(String... credentials) {
                        Date date = new Date(timestamp);
                        Date []finalDates = rehearsalRequestFragment.generateAvailableTimesFromDateAndTimeFields(date);

                        WebserviceManager.addAvailableTime(credentials[0], credentials[1], finalDates[0], finalDates[1]);

                        return null;
                    }

                    protected void onPostExecute(Void v)
                    {
                        Toast.makeText(me, R.string.availableTimeAdded, Toast.LENGTH_SHORT).show();
                    }
                }.execute(bandName, username);
            }
        }, "Android");

        wbvwCalendar.loadUrl("file:///android_res/raw/site.html");
    }

    public Date[] generateAvailableTimesFromDateAndTimeFields (Date date) {
        Date []dates = new Date[2];
        Calendar calendar = Calendar.getInstance();
        int hours, minutes;
        String timeFrom = txtFrom.getText().toString();
        String timeTo = txtTo.getText().toString();
        String []timeData;

        calendar.setTime(date);
        timeData = timeFrom.split(":");
        hours = Integer.parseInt(timeData[0]);
        minutes = Integer.parseInt(timeData[1]);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE, minutes);
        dates[0] = calendar.getTime();

        calendar.setTime(date);
        timeData = timeTo.split(":");
        hours = Integer.parseInt(timeData[0]);
        minutes = Integer.parseInt(timeData[1]);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE, minutes);
        dates[1] = calendar.getTime();

        return dates;
    }

    private void setComponentReferences(View view) {
        spnRehearsalRequest = (Spinner) view.findViewById(R.id.spnRehearsalRequest);
        wbvwCalendar = (WebView) view.findViewById(R.id.wbvwCalendar);
        txtFrom = (EditText) view.findViewById(R.id.txtFrom);
        txtTo = (EditText) view.findViewById(R.id.txtTo);
        btnAddAvailableTime = (Button) view.findViewById(R.id.btnAddAvailableTime);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

            Intent intent = ((AppCompatActivity) context).getIntent();

            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
            bandName = intent.getStringExtra("bandName");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnAddAvailableTime:
                wbvwCalendar.loadUrl("javascript:getAvailableTimes();");
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spnRehearsalRequest:
                String dateFrom, dateTo;
                RehearsalRequest rehearsalRequest = (RehearsalRequest) spnRehearsalRequest.getSelectedItem();
                Date startTime = rehearsalRequest.getStartTime(), endTime = rehearsalRequest.getEndTime();
                Calendar calendar = Calendar.getInstance();

                calendar.setTime(startTime);
                dateFrom = "new Date (" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH) + "," + calendar.get(Calendar.HOUR_OF_DAY) + "," + calendar.get(Calendar.MINUTE) + "," + calendar.get(Calendar.SECOND) + "," + calendar.get(Calendar.MILLISECOND) + ")";
                calendar.setTime(endTime);
                dateTo = "new Date (" + calendar.get(Calendar.YEAR) + "," + calendar.get(Calendar.MONTH) + "," + calendar.get(Calendar.DAY_OF_MONTH) + "," + calendar.get(Calendar.HOUR_OF_DAY) + "," + calendar.get(Calendar.MINUTE) + "," + calendar.get(Calendar.SECOND) + "," + calendar.get(Calendar.MILLISECOND) + ")";

                wbvwCalendar.loadUrl("javascript:generateCalendar(" + dateFrom + "," + dateTo + ");");

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch (parent.getId()) {
            case R.id.spnRehearsalRequest:
                wbvwCalendar.loadUrl("javascript:removeCalendar();");

                break;
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
        void onFragmentInteraction(Uri uri);
    }
}
