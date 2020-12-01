package com.aidenriddler.wysp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.scrounger.countrycurrencypicker.library.Country;
import com.scrounger.countrycurrencypicker.library.CountryCurrencyPicker;
import com.scrounger.countrycurrencypicker.library.Currency;
import com.scrounger.countrycurrencypicker.library.Listener.CountryCurrencyPickerListener;
import com.scrounger.countrycurrencypicker.library.PickerType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Shop_Install extends AppCompatActivity {

    //declaration
    private TextInputEditText shopNameView;
    private TextInputEditText emailView;
    private TextInputEditText countryView;
    private TextInputEditText cityView;
    private TextInputEditText passwdView;
    private TextInputEditText cPasswdView;
    private ChipGroup shopTypeChips;
    private Spinner mSpinner;
    private TextFileReader reader;
    private ExtendedFloatingActionButton sign_in;
    private ExtendedFloatingActionButton submit;
    private ChipGroup chipGroup;

    //Values
    private String selectedCountry;
    private String currency;

    //reader
    private ArrayList<String> shopTypes;
    private Map<String, String> selectedShoptypes = new HashMap<>();
    private int shopTypeKey = 0;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //progress bar
    ProgressBar progressBar;

    //shop
    Shop shop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_install);

        getSupportActionBar().hide();

        //initialisation
        shopNameView = findViewById(R.id.shop_name);
        emailView = findViewById(R.id.email);
        countryView = findViewById(R.id.country);
        cityView = findViewById(R.id.city);
        passwdView = findViewById(R.id.password);
        cPasswdView = findViewById(R.id.c_passwd);
        mSpinner = findViewById(R.id.material_spinner1);
        submit = findViewById(R.id.submit);
        sign_in = findViewById(R.id.sign_in);
        chipGroup = findViewById(R.id.shopTypeChips);

        //reader
        setupSpinner();

        //firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //clicks/focus
        countryView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    pickCountryCurrency();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignIn();
            }
        });

        //progress bar
        progressBar = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
    }

    private void sendShopDataToFirebase() {

        final String uid = mAuth.getCurrentUser().getUid();
        final DocumentReference dr = db.collection("CurrentUserID").document("IDNumber");
        db.runTransaction(new Transaction.Function<String>() {
            @Nullable
            @Override
            public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(dr);
                String currentID = String.valueOf(snapshot.get("CurrentID"));
                shop.setShopID("WYSP" + currentID);
                db.collection("Shops").document(uid).set(shop);
                int newID = Integer.parseInt(currentID) + 1;
                transaction.update(dr,"CurrentID",String.valueOf(newID));
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d("wysp","Success!!");
                Intent shopIntent = new Intent(Shop_Install.this,ShopF.class);
                startActivity(shopIntent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("wysp","Failed!!!  ", e);
            }
        });
    }

    private void attemptSignIn() {
        sign_in.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(Shop_Install.this,"Logging in",Toast.LENGTH_SHORT).show();
        final String email = emailView.getText().toString();
        String password = passwdView.getText().toString();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified() == true){
                        sendShopDataToFirebase();
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        sign_in.setVisibility(View.VISIBLE);
                        new MaterialAlertDialogBuilder(Shop_Install.this)
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
                    Toast.makeText(Shop_Install.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSpinner() {

        reader = new TextFileReader();
        shopTypes = reader.ReadFile(this, R.raw.shop_types);
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.spinner_item,shopTypes);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                String st = shopTypes.get(position);
                if (!st.equals("Select one or more shopTypes")){
                    selectedShoptypes.put(String.valueOf(selectedShoptypes.size()), st);
                    final Chip chip = new Chip(Shop_Install.this);
                    chip.setText(st);
                    chip.setHeight(27);
                    chip.setCloseIconVisible(true);
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedShoptypes.remove(selectedShoptypes.size());
                            chipGroup.removeView(chip);
                        }
                    });
                    chipGroup.addView(chip);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void attemptSubmit() {
        shopNameView.setError(null);
        emailView.setError(null);
        countryView.setError(null);
        cityView.setError(null);
        passwdView.setError(null);
        cPasswdView.setError(null);

        String shopName = shopNameView.getText().toString();
        String email = emailView.getText().toString();
        String country = countryView.getText().toString();
        String city = cityView.getText().toString();
        String password = passwdView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(shopName)){
            shopNameView.setError("This field is required");
            focusView = shopNameView;
            cancel = true;
        }
        if(TextUtils.isEmpty(email)){
            emailView.setError("This field is required");
            focusView = emailView;
            cancel = true;
        }else if(!isEmailValid(email)){
            emailView.setError("Invalid email address");
            focusView = emailView;
            cancel = true;
        }
        if(TextUtils.isEmpty(country)){
            countryView.setError("This field is required");
            focusView = countryView;
            cancel = true;
        }
        if(TextUtils.isEmpty(city)){
            cityView.setError("This field is required");
            focusView = cityView;
            cancel = true;
        }
        if(TextUtils.isEmpty(password)){
            passwdView.setError("This field is required");
            focusView = passwdView;
            cancel = true;
        }else if(!isPasswordValid(password)){
            passwdView.setError("Password too short or does not match");
            focusView = passwdView;
            cancel = true;
        }
        if(selectedShoptypes.size() == 0){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("WySP Alert")
                    .setMessage("Please select atleast one shop type")
                    .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
            cancel = true;
            focusView = mSpinner;
        }
        if (cancel){
           focusView.requestFocus();
        }else {
            String shopID = " ";
            shop = new Shop(shopID,shopName,email,country,city,selectedShoptypes,new Date(),currency);
            createFirebaseUser();
        }
    }

    private void createFirebaseUser() {
        submit.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        String email = emailView.getText().toString();
        String password = passwdView.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    sendEmailVerification();
                }else{
                    submit.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    new MaterialAlertDialogBuilder(Shop_Install.this)
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
                    new MaterialAlertDialogBuilder(Shop_Install.this)
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
                    new MaterialAlertDialogBuilder(Shop_Install.this)
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
                                    Intent loginIntent = new Intent(Shop_Install.this,Login.class);
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

    private void pickCountryCurrency() {
        CountryCurrencyPicker picker = CountryCurrencyPicker.newInstance(PickerType.COUNTRYandCURRENCY, new CountryCurrencyPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
                currency = String.valueOf(country.getCurrency().getSymbol());
                countryView.setText(String.valueOf(country.getName()));
            }

            @Override
            public void onSelectCurrency(Currency currency) {

            }
        });
        picker.show(getSupportFragmentManager(),CountryCurrencyPicker.DIALOG_NAME);
    }
}
