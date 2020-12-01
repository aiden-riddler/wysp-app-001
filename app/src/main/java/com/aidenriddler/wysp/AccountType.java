package com.aidenriddler.wysp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AccountType extends AppCompatActivity {

    //declaration
    private Button userAccount;
    private Button businessAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_type);

        //initialisation
        userAccount = findViewById(R.id.user_account);
        businessAccount = findViewById(R.id.business_account);

        userAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AccountType.this,"Under Development",Toast.LENGTH_LONG);
            }
        });
        businessAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountType.this,Shop_Install.class);
                startActivity(intent);
            }
        });
    }
}
