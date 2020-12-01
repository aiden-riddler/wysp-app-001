package com.aidenriddler.wysp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class UserSignUp extends AppCompatActivity {

    //declaration
    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText cPasswdView;
    private Button submit;
    private Button sign_in;

    //progress bar
    ProgressBar progressBar;

    private String username;
    private String email;
    private String password;
    private String cPasswd;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //user
    private User user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_up);

        //initialisation
        usernameView = findViewById(R.id.firstName);
        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        cPasswdView = findViewById(R.id.c_passwd);
        submit = findViewById(R.id.submit);
        sign_in = findViewById(R.id.sign_in);

        //progress bar
        progressBar = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignIn();
            }
        });
    }
    private void attemptSignIn() {
        sign_in.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(UserSignUp.this,"Logging in",Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified() == true){
                        createUserAccount();
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        sign_in.setVisibility(View.VISIBLE);
                        new MaterialAlertDialogBuilder(UserSignUp.this)
                                .setTitle("WySP Alert")
                                .setMessage("Failed. Email not verified, open " + email + " to verify its you")
                                .setCancelable(true)
                                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        attemptSignIn();
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
                    sign_in.setVisibility(View.VISIBLE);
                    Toast.makeText(UserSignUp.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUserAccount() {
        final DocumentReference dr = db.collection("CurrentUserID").document("IDNumber");
        db.runTransaction(new Transaction.Function<String>() {
            @Nullable
            @Override
            public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(dr);
                String currentID = String.valueOf(snapshot.get("CurrentID"));
                user.setWySPID("WYSP" + currentID);
                db.collection("Users").document(email).set(user);
                int newID = Integer.parseInt(currentID) + 1;
                transaction.update(dr,"CurrentID",String.valueOf(newID));
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

            }
        });
    }

    private void validateData() {
        usernameView.setError(null);
        emailView.setError(null);
        passwordView.setError(null);
        cPasswdView.setError(null);

        username = usernameView.getText().toString();
        email = emailView.getText().toString();
        password = passwordView.getText().toString();
        cPasswd = cPasswdView.getText().toString();

        Boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)){
            usernameView.setError("This field is required");
            cancel = true;
            focusView = usernameView;
        }
        if (TextUtils.isEmpty(email)){
            emailView.setError("This field is required");
            cancel = true;
            focusView = emailView;
        }else if(!isEmailValid(email)){
            emailView.setError("Invalid email address");
            focusView = emailView;
            cancel = true;
        }
        if (TextUtils.isEmpty(cPasswd)){
            cPasswdView.setError("This field is required");
            cancel = true;
            focusView = cPasswdView;
        }
        if (TextUtils.isEmpty(password)){
            passwordView.setError("This field is required");
            cancel = true;
            focusView = passwordView;
        }else if (!isPasswordValid(password)){
            passwordView.setError("Password too short or does not match");
            focusView = passwordView;
            cancel = true;
        }
        if (cancel){
            focusView.requestFocus();
        }else{
            String WySPID = "";
            user = new User(username,WySPID);
            createFirebaseUser();
        }

    }

    private void createFirebaseUser() {
        submit.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    sendEmailVerification();
                }else{
                    submit.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    new MaterialAlertDialogBuilder(UserSignUp.this)
                            .setTitle("WySP Alert")
                            .setMessage("Authentification failed")
                            .setCancelable(true)
                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createFirebaseUser();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
            }
        });
    }

    private void sendEmailVerification() {
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    sign_in.setVisibility(View.VISIBLE);
                    new MaterialAlertDialogBuilder(UserSignUp.this)
                            .setTitle("WySP Alert")
                            .setMessage("Verification email sent. Open " + emailView.getText().toString() + "to verify its you")
                            .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    dialog.cancel();
                                }
                            }).setCancelable(false)
                            .show();
                }else{
                    new MaterialAlertDialogBuilder(UserSignUp.this)
                            .setTitle("WySP Alert")
                            .setMessage("Failed to send verification email to " + emailView.getText().toString())
                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendEmailVerification();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent loginIntent = new Intent(UserSignUp.this,Login.class);
                                    startActivity(loginIntent);
                                }
                            }).setCancelable(false)
                            .show();
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") ;
    }

    private boolean isPasswordValid(String password) {
        String confirmPasswd = cPasswdView.getText().toString();
        return password.equals(confirmPasswd) && password.length() > 6;
    }
}
