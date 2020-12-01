package com.aidenriddler.wysp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;


import java.util.ArrayList;
import java.util.List;

public class Add_product_page extends AppCompatActivity {

    //declaration
    private Spinner mSpinner;
    private Button submit;

    //gallery
    private int PERMISSION_REQUEST_CODE = 100;
    private int PICK_MULTIPLE_IMAGE_REQUEST_CODE = 200;
    private static final int REQUEST_CODE_CHOOSE = 300;

    //shops
    private ArrayList<String> shopTypes = new ArrayList<>();
    private String productShopType;
    private String currencySymbol;
    private String uid;
    private String email;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_page);

        //intents
        Intent intent = getIntent();
        int shopsSize = Integer.parseInt(intent.getStringExtra("ShopTypesSize"));
        currencySymbol = intent.getStringExtra("currencySymbol");
        Log.d("wysp",currencySymbol);
        uid = intent.getStringExtra("uid");
        email = intent.getStringExtra("email");

        for(int i = 0; i < shopsSize;i++){
            String shopType = intent.getStringExtra(String.valueOf(i));
            Log.d("wysp","SHOPTYPE:   " + shopType);
            shopTypes.add(shopType);
        }

        //initialisation
        mSpinner = findViewById(R.id.material_spinner1);
        submit = findViewById(R.id.submit);
        
        setupSpinner();
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkProductShopType();
            }
        });
    }

    private void checkProductShopType() {
        
        boolean cancel = false;
        View focusView = null;
        
        if(TextUtils.isEmpty(productShopType)){
            new MaterialAlertDialogBuilder(this)
                    .setTitle("WySP Alert")
                    .setMessage("Please select product shop type")
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
        }else{
            if(ActivityCompat.checkSelfPermission(Add_product_page.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(Add_product_page.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                return;
            }
            startGalleryIntent();
        }
    }

    private void startGalleryIntent() {
        new MaterialAlertDialogBuilder(this).setTitle("WySP Alert")
                .setMessage("Select at least three images")
                .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Matisse.from(Add_product_page.this)
                                .choose(MimeType.ofAll())
                                .countable(true)
                                .maxSelectable(6)
                                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.material_emphasis_high_type))
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                .thumbnailScale(0.85f)
                                .imageEngine(new GlideEngine())
                                .showPreview(false)
                                .forResult(REQUEST_CODE_CHOOSE);
                    }
                }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case REQUEST_CODE_CHOOSE:
                List<Uri> uris = Matisse.obtainResult(data);

                if (uris.size() >= 3) {
                    Intent intent = new Intent(Add_product_page.this, Product_Image_View.class);
                    for (int i = 0; i < uris.size(); i++) {
                        intent.putExtra(String.valueOf(i),String.valueOf(uris.get(i)) );
                    }
                    intent.putExtra("NumberOfImages", String.valueOf(uris.size()));
                    intent.putExtra("productShopType", productShopType);
                    intent.putExtra("uid", uid);
                    intent.putExtra("email", email);
                    Log.d("wysp", currencySymbol);
                    intent.putExtra("currencySymbol", currencySymbol);
                    startActivity(intent);
                }else{
                    startGalleryIntent();
                }
                break;
        }
    }

    private void startProductImageView() {
    }

    private void setupSpinner() {
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.spinner_item,shopTypes);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productShopType = shopTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
