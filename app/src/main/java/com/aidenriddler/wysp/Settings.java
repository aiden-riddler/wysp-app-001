package com.aidenriddler.wysp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.transition.Slide;

public class Settings extends AppCompatActivity {

    private String email;
    private String uid;

    //declaration
    private TextView shop;
    private CardView orangeCard;
    private TextView chats;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setupWindowAnimations();

        getSupportActionBar().hide();

        //intents
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        uid = intent.getStringExtra("uid");
        //initialisation
        shop = findViewById(R.id.shop);
        orangeCard = findViewById(R.id.settings);
        chats = findViewById(R.id.chats);

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShopActivityTransition();
            }
        });
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatsActivityTransition();
            }
        });
    }
    private void startChatsActivityTransition() {
        Intent intent = new Intent(Settings.this,Chats.class);
        View sharedView = orangeCard;
        String transitionName = getString(R.string.stc_trans);
        intent.putExtra("email",email);
        intent.putExtra("uid",uid);

        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Settings.this,sharedView,transitionName);
        startActivity(intent,activityOptions.toBundle());
    }
    private void startShopActivityTransition() {
        Intent intent = new Intent(Settings.this,ShopF.class);
        View sharedView = orangeCard;
        String transitionName = getString(R.string.stc_trans);

        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Settings.this,sharedView,transitionName);
        startActivity(intent,activityOptions.toBundle());
    }
    private void setupWindowAnimations() {
        Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }
}
