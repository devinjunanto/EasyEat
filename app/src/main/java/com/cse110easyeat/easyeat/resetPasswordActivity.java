package com.cse110easyeat.easyeat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cse110easyeat.accountservices.User;
import com.cse110easyeat.database.service.FirebaseHandlerService;
import com.cse110easyeat.network.listener.NetworkListener;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class resetPasswordActivity extends AppCompatActivity {
    private EditText emailTextField;
    private Button resetButton;

    private FirebaseAuth mAuth;
    private FirebaseHandlerService firebaseDb;

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailTextField = (EditText) findViewById(R.id.reset_email_password);
        resetButton = (Button) findViewById(R.id.resetBtn);

        progressBar = new ProgressDialog(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        firebaseDb = new FirebaseHandlerService();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setMessage("Verifying...");
                progressBar.show();
                final String email = emailTextField.getText().toString();
                String id = email.replaceAll("\\.", "_");
                if (email.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email",
                            Toast.LENGTH_SHORT).show();
                }

                /** First check if the user is already registered */
                firebaseDb.getDataFromDatabase(id, new NetworkListener<User>() {
                    @Override
                    public void getResult(User result) {
                        progressBar.hide();
                        if (result.getEmail().equals(email)) {
                            mAuth.sendPasswordResetEmail(email);
                        } else {
                            Toast.makeText(getApplicationContext(), "You are not registered. " +
                                    "please sign up!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
