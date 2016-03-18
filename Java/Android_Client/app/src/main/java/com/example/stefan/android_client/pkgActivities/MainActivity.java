package com.example.stefan.android_client.pkgActivities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.stefan.android_client.R;
import com.example.stefan.android_client.pkgFragments.AppearanceRequestsFragment;
import com.example.stefan.android_client.pkgFragments.PersonalDataFragment;
import com.example.stefan.android_client.pkgFragments.RehearsalRequestFragment;

public class MainActivity extends AppCompatActivity implements PersonalDataFragment.OnFragmentInteractionListener, AppearanceRequestsFragment.OnFragmentInteractionListener, RehearsalRequestFragment.OnFragmentInteractionListener {
    private TabHost tabHost;

    private String username;
    private String password;
    private String bandName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        bandName = getIntent().getStringExtra("bandName");

        setComponentReferences();
        initializeTabHost();
    }

    private void setComponentReferences() {
        tabHost = (TabHost) findViewById(R.id.tabHost);
    }

    private void initializeTabHost() {
        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Personal Data");
        spec.setContent(R.id.frgmntPersonalData);
        spec.setIndicator("Personal Data");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Appearance Requests");
        spec.setContent(R.id.frgmntAppearanceRequests);
        spec.setIndicator("Appearance Requests");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Rehearsal Requests");
        spec.setContent(R.id.frgmntRehearsalRequests);
        spec.setIndicator("Rehearsal Requests");
        tabHost.addTab(spec);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
