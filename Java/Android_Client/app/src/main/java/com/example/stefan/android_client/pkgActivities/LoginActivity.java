package com.example.stefan.android_client.pkgActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stefan.android_client.R;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtBandName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setComponentReferences();
    }

    private void setComponentReferences() {
        txtUsername = (EditText) this.findViewById (R.id.txtUsername);
        txtPassword = (EditText) this.findViewById(R.id.txtPassword);
        txtBandName = (EditText) this.findViewById(R.id.txtBandName);
    }

    private void btnLogin_onClick() {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String bandName = txtBandName.getText().toString();

        if (/*checkCredentials*/ false) {

        }
        else {
            Toast.makeText(this, R.string.credentialsNotCorrect, Toast.LENGTH_LONG);
        }
    }
}
