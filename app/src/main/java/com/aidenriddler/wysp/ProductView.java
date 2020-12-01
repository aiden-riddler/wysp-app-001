package com.aidenriddler.wysp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Type;

public class ProductView extends AppCompatActivity {

    //declaration
    private TextInputEditText first;
    private TextInputEditText second;
    private TextInputEditText third;
    private TextInputEditText fourth;
    private TextInputLayout fourthIL;
    private Button submit;

    private String shopTypeName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_view);

        Intent intent = getIntent();
        shopTypeName = intent.getStringExtra("ShopTypeName");

        //initialisation
        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);
        fourthIL = findViewById(R.id.fourthIL);

        switch (shopTypeName){
            case "GAS STATION":
                first.setHint("Fuel/Service Name");
                second.setHint("Price");
                second.setInputType(InputType.TYPE_CLASS_NUMBER);
                third.setHint("Additional Info");
                fourthIL.setVisibility(View.INVISIBLE);
                submit.setTop(8);
                break;
            case "HAIRDRESSER/BARBER SHOP":
            case "CAR WASH":
            case "GARAGE":
                first.setHint("Service Name");
                second.setHint("Price");
                second.setInputType(InputType.TYPE_CLASS_NUMBER);
                third.setHint("Additional Info");
                fourthIL.setVisibility(View.INVISIBLE);
                submit.setTop(8);
                break;
            case "NEWSAGENT":
                first.setHint("Service Name");
                second.setHint("Price");
                second.setInputType(InputType.TYPE_CLASS_NUMBER);
                third.setHint("Additional Info");
                fourthIL.setVisibility(View.INVISIBLE);
                submit.setTop(8);
                break;
            case "MOVERS AND PARKERS":
            case "TRAVEL AGENT/TRAVEL AGENCY":
                first.setHint("From");
                second.setHint("To");
                third.setInputType(InputType.TYPE_CLASS_NUMBER);
                third.setHint("Price");
                fourthIL.setHint("Additional Info");
                break;
            case "DRY CLEANER":
                first.setHint("Service Name");
                second.setHint("Price");
                second.setInputType(InputType.TYPE_CLASS_NUMBER);
                third.setHint("Cloth type");
                fourthIL.setHint("Additional Info/Duration");
                break;
        }
    }
}
