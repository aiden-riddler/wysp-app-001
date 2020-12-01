package com.aidenriddler.wysp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    
    //declaration
    private EditText emailView;
    private EditText passwordView;
    private Button sign_in;
    private TextView sign_up;
    private TextView forgotPassword;

    //firebase
    private FirebaseAuth mAuth;

    //progress bar
    private ProgressBar progressBar;
    private Sprite circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        getSupportActionBar().hide();
        
        //initialisation
        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        sign_in = findViewById(R.id.sign_in);
        sign_up = findViewById(R.id.sign_up);
        forgotPassword = findViewById(R.id.forgot_password);
        
        //clicks
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchShopInstall();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail();
            }
        });

        //firebase
        mAuth = FirebaseAuth.getInstance();

        //progressbar
        progressBar = findViewById(R.id.spin_kit);
        circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);


    }

    private void sendPasswordResetEmail() {
    }

    private void launchShopInstall() {
        Intent intent = new Intent(Login.this,AccountType.class);
        startActivity(intent);
    }

    private void attemptLogin() {
        final String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)){
            emailView.setError("This field is required");
            focusView = emailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)){
            passwordView.setError("This field is required");
            focusView = passwordView;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }else{
            sign_in.setVisibility(View.INVISIBLE);
            forgotPassword.setEnabled(false);
            sign_up.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(Login.this,"Logging in",Toast.LENGTH_SHORT).show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        if (mAuth.getCurrentUser().isEmailVerified() == true){
                            openLoadPage();
                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            sign_in.setVisibility(View.VISIBLE);
                            new MaterialAlertDialogBuilder(Login.this)
                                    .setTitle("WySP Alert")
                                    .setMessage("Failed. Email not verified, open " + email + " to verify its you")
                                    .setCancelable(true)
                                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            attemptLogin();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }

                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        sign_in.setVisibility(View.VISIBLE);
                        forgotPassword.setEnabled(true);
                        sign_up.setEnabled(true);
                        Toast.makeText(Login.this,"Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("wysp","FAILED:   ",e);
                    progressBar.setVisibility(View.INVISIBLE);
                    sign_in.setVisibility(View.VISIBLE);
                    forgotPassword.setEnabled(true);
                    sign_up.setEnabled(true);
                    Toast.makeText(Login.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openLoadPage() {
        Intent intent = new Intent(Login.this,ShopF.class);
        startActivity(intent);
    }

}