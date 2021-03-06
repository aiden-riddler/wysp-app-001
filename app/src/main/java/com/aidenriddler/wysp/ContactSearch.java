package com.aidenriddler.wysp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ContactSearch extends AppCompatActivity {

    private ExtendedFloatingActionButton sendMessage;
    private TextInputEditText searchBar;

    private String email;
    private String uid;
    private String shopName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_search);

        //intents
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        uid = intent.getStringExtra("uid");
        shopName = intent.getStringExtra("shopName");

        sendMessage = findViewById(R.id.send_message);
        searchBar = findViewById(R.id.searchBar);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateContact();
            }
        });
    }

    private void validateContact() {
        searchBar.setError(null);
        String contact = searchBar.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(contact)){
            searchBar.setError("This field is required");
            focusView = searchBar;
            cancel = true;
        }
        if (cancel){
            focusView.requestFocus();
        }else{
            Intent intent = new Intent(ContactSearch.this,ChatView.class);
            intent.putExtra("Contact",contact);
            intent.putExtra("shopName",shopName);
            intent.putExtra("email",email);
            intent.putExtra("uid",uid);
            startActivity(intent);
        }
    }
}
