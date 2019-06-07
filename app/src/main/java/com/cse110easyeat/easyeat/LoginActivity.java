package com.cse110easyeat.easyeat;

import com.cse110easyeat.controller.EasyEatController;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110easyeat.easyeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {
    private Button loginButton;
    private EditText emailField;
    private EditText passwordField;
    private EasyEatController backendController;
    private FirebaseAuth mAuth;
    private CheckBox showPassword;

    private TextView signUpLink;

    private ProgressDialog progressBar;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        mAuth = FirebaseAuth.getInstance();
        progressBar = new ProgressDialog(this);


        loginButton = (Button) findViewById(R.id.loginBtn);
        emailField = (EditText) findViewById(R.id.login_emailid);
        passwordField = (EditText) findViewById(R.id.login_password);
        signUpLink = (TextView) findViewById(R.id.createAccount);
        showPassword = (CheckBox) findViewById(R.id.show_hide_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginSuccess = false;
                // Check for input
                Log.d(TAG, "LOGIN BUTTON CLICKED");

                if (emailField.getText().length() > 0 && passwordField.getText().length() > 0) {
                    String email = emailField.getText().toString();
                    String password = passwordField.getText().toString();
                    progressBar.setMessage("Logging in...");
                    progressBar.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.hide();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user.isEmailVerified()) {
                                            progressBar.dismiss();
                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra("email", user.getEmail());
                                            setResult(RESULT_OK, returnIntent);
                                            finish();

                                            //startActivity(new Intent(getApplicationContext(), inputFragment.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please verify your email!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });

        // TODO: NEED TO HAVE A BACK BUTTON
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "SHOW PASSWORD FIELD CHECKED");
               if (isChecked) {
                   passwordField.setTransformationMethod(null);
               } else {
                   passwordField.setTransformationMethod(new PasswordTransformationMethod());
               }
            }
        });

        // TODO: IMPLEMENT FORGET PASSWORD


    }
}
