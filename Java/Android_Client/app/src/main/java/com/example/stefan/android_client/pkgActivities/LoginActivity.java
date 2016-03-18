package com.example.stefan.android_client.pkgActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stefan.android_client.R;
import com.example.stefan.android_client.pkgModel.WebserviceManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtBandName;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setComponentReferences();
        setEventHandlers();
    }

    private void setEventHandlers() {
        btnLogin.setOnClickListener(this);
    }

    private void setComponentReferences() {
        txtUsername = (EditText) this.findViewById (R.id.txtUsername);
        txtPassword = (EditText) this.findViewById(R.id.txtPassword);
        txtBandName = (EditText) this.findViewById(R.id.txtBandName);

        btnLogin = (Button) this.findViewById(R.id.btnLogin);
    }

    private void btnLogin_onClick() {
        final String username = txtUsername.getText().toString();
        final String password = txtPassword.getText().toString();
        final String bandName = txtBandName.getText().toString();
        final Activity me = this;

        new AsyncTask<String,Void,Boolean>() {
            @Override
            protected Boolean doInBackground(String... credentials) {
                return WebserviceManager.checkCredentials(credentials[0], credentials[1], credentials[2]);
            }

            protected void onPostExecute(Boolean credentialsCorrect)
            {
                if (credentialsCorrect) {
                    Intent intent = new Intent(me, MainActivity.class);

                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("bandName", bandName);

                    startActivity(intent);
                }
                else {
                    Toast.makeText(me, R.string.credentialsNotCorrect, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(username, password, bandName);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnLogin:
                btnLogin_onClick();
                break;
        }
    }
}
