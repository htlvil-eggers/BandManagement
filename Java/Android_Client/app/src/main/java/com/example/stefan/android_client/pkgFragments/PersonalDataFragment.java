package com.example.stefan.android_client.pkgFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stefan.android_client.R;
import com.example.stefan.android_client.pkgModel.Instrument;
import com.example.stefan.android_client.pkgModel.Location;
import com.example.stefan.android_client.pkgModel.Musician;
import com.example.stefan.android_client.pkgModel.WebserviceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalDataFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";
    private static final String ARG_BAND_NAME = "bandName";

    // TODO: Rename and change types of parameters
    private String username;
    private String password;
    private String bandName;

    private EditText txtPassword;
    private EditText txtFirstName;
    private EditText txtLastName;
    private DatePicker dtpckrBirthdate;
    private Spinner spnHabitation;
    private ListView lstInstruments;
    private Button btnSave;

    private int musicianId;

    private OnFragmentInteractionListener mListener;

    public PersonalDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PersonalDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalDataFragment newInstance(String username, String password, String bandName) {
        PersonalDataFragment fragment = new PersonalDataFragment();
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

    private void setComponentReferences(View view) {
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        txtFirstName = (EditText) view.findViewById(R.id.txtFirstName);
        txtLastName = (EditText) view.findViewById(R.id.txtLastName);
        dtpckrBirthdate = (DatePicker) view.findViewById(R.id.dtpckrBirthdate);
        spnHabitation = (Spinner) view.findViewById(R.id.spnHabitation);
        lstInstruments = (ListView) view.findViewById(R.id.lstInstruments);
        btnSave = (Button) view.findViewById(R.id.btnSave);
    }

    private void initializeComponents(Musician musician) {
        initializeTxtPassword(musician);
        initializeTxtFirstName(musician);
        initializeTxtLastName(musician);
        initializeDtpckrBirthdate(musician);
        initializeSpnHabitation(musician);
        initializeLstInstruments(musician);
    }

    private void initializeDtpckrBirthdate(Musician musician) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(musician.getBirthdate());

        dtpckrBirthdate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void initializeTxtLastName(Musician musician) {
        txtLastName.setText(musician.getLastName());
    }

    private void initializeTxtFirstName(Musician musician) {
        txtFirstName.setText(musician.getFirstName());
    }

    private void initializeTxtPassword(Musician musician) {
        txtPassword.setText(musician.getPassword());
    }

    private void initializeLstInstruments(final Musician musician) {
        final Context context = this.getContext();

        new AsyncTask<String,Void,Instrument[]>() {
            @Override
            protected Instrument[] doInBackground(String... credentials) {
                return WebserviceManager.getInstruments();
            }

            protected void onPostExecute(Instrument[] instruments)
            {
                ArrayAdapter<Instrument> adapter = new ArrayAdapter<Instrument> (context, android.R.layout.simple_list_item_multiple_choice, instruments);

                lstInstruments.setAdapter(adapter);

                setListViewHeightBasedOnChildren(lstInstruments);

                for (Instrument instrument : musician.getSkills()) {
                    lstInstruments.setItemChecked(adapter.getPosition(instrument), true);
                }
            }
        }.execute();
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (int) (totalHeight * 1.5) + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void initializeSpnHabitation(final Musician musician) {
        final Context context = this.getContext();

        new AsyncTask<String,Void,Location[]>() {
            @Override
            protected Location[] doInBackground(String... credentials) {
                return WebserviceManager.getLocations();
            }

            protected void onPostExecute(Location[] locations)
            {

                ArrayAdapter<Location> adapter = new ArrayAdapter<Location> (context, android.R.layout.simple_spinner_item, locations);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnHabitation.setAdapter(adapter);

                spnHabitation.setSelection(adapter.getPosition(musician.getHabitation()));
            }
        }.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_data, container, false);

        setComponentReferences(view);

        new AsyncTask<String,Void,Musician>() {
            @Override
            protected Musician doInBackground(String... credentials) {
                return WebserviceManager.getMusician(credentials[0], credentials[1]);
            }

            protected void onPostExecute(Musician musician)
            {
                musicianId = musician.getId();

                initializeComponents(musician);
            }
        }.execute(username, password);

        setEventHandlers();

        return view;
    }

    private void setEventHandlers() {
        btnSave.setOnClickListener(this);
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
            case R.id.btnSave:
                btnSave_onClick();
                break;
        }
    }

    private void btnSave_onClick() {
        String password = txtPassword.getText().toString();
        String firstName = txtFirstName.getText().toString();
        String lastName = txtLastName.getText().toString();

        Calendar calendar = Calendar.getInstance();
        calendar.set(dtpckrBirthdate.getYear(), dtpckrBirthdate.getMonth(), dtpckrBirthdate.getDayOfMonth());
        Date birthdate = calendar.getTime();

        Location habitation = (Location) spnHabitation.getSelectedItem();

        Vector<Instrument> skills = new Vector<Instrument>();
        SparseBooleanArray checked = lstInstruments.getCheckedItemPositions();
        int size = checked.size(); // number of name-value pairs in the array
        for (int i = 0; i < size; i++) {
            int key = checked.keyAt(i);
            boolean value = checked.get(key);
            if (value)
                skills.add((Instrument) lstInstruments.getItemAtPosition(key));
        }

        Musician updatedMusician = new Musician (musicianId, username, password, firstName, lastName, skills, habitation, birthdate, null);

        final Context context = this.getContext();

        new AsyncTask<Musician,Void,Void>() {
            @Override
            protected Void doInBackground(Musician... musician) {
                WebserviceManager.updateMusician(musician[0]);

                return null;
            }

            protected void onPostExecute(Void v)
            {
                Toast.makeText(context, R.string.personalDataUpdated, Toast.LENGTH_SHORT).show();
            }
        }.execute(updatedMusician);
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
