package com.aidenriddler.wysp;

import android.Manifest;
import android.app.ActionBar;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductViewEdit2 extends AppCompatActivity {

    //declaration
    private SliderView sliderView;
    private EditText productNameView;
    private EditText priceView;
    private EditText pTypeView;
    private EditText pDescricptionView;
    private EditText pAddInfoView;

    private String productName;
    private Boolean imageChange = false;
    private String price;
    private String pType;
    private String pDescricption;
    private String pAddInfo;
    private String currencySymbol;
    private int imagePathSize;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private String docID;

    //product
    private Product product;
    private String addInfo2;
    private String description2;
    private String price2;
    private String pname2;
    private String ptype2;
    private String pShopType;

    //progressBar
    private ProgressBar progressBar;

    //firebase
    private StorageReference storageReference;
    private String email;
    private String uid;

    //sliderAdapter
    private SliderAdapter adapter;
    private ArrayList<Uri> newImageuris = new ArrayList<>();

    private static final int PERMISSION_REQUEST_CODE = 100;

    private static final int REQUEST_CODE_CHOOSE = 300;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_view_edit2);

        //intents
        Intent intent = getIntent();
        docID = intent.getStringExtra("reference");
        productName = intent.getStringExtra("productName");
        price = intent.getStringExtra("price");
        pType = intent.getStringExtra("pType");
        pDescricption = intent.getStringExtra("pDescricption");
        pAddInfo = intent.getStringExtra("pAddInfo");
        email = intent.getStringExtra("email");
        uid = intent.getStringExtra("uid");
        pShopType = intent.getStringExtra("pShopType");
        currencySymbol = intent.getStringExtra("currencySymbol");
        imagePathSize = Integer.parseInt(intent.getStringExtra("imagePathSize"));
        for(int i = 0; i<imagePathSize;i++){
            Uri uri = Uri.parse(intent.getStringExtra(String.valueOf(i)));
            imageUris.add(uri);
        }

        sliderView = findViewById(R.id.imageSlider);
        pAddInfoView = findViewById(R.id.add_info);
        pDescricptionView = findViewById(R.id.Q_desc);
        priceView = findViewById(R.id.price);
        productNameView = findViewById(R.id.product_name);
        pTypeView = findViewById(R.id.p_type_name);

        productNameView.setText(productName);
        priceView.setText(price);
        pTypeView.setText(pType);
        pDescricptionView.setText(pDescricption);
        pAddInfoView.setText(pAddInfo);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        //firebase & slider
        adapter = new SliderAdapter(ProductViewEdit2.this);
        for(int i = 0; i<imagePathSize;i++){
            SliderItem sliderItem = new SliderItem(imageUris.get(i));
            adapter.addItem(sliderItem);
        }
        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.switchImages:
                checkForPermission();
                break;
            case R.id.save:
                validateData();
                break;
            case R.id.homeAsUp:
                checkForChanges();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkForChanges() {

        addInfo2 = pAddInfoView.getText().toString();
        description2 = pDescricptionView.getText().toString();
        price2 = priceView.getText().toString();
        pname2 = productNameView.getText().toString();
        ptype2 = pTypeView.getText().toString();

        Boolean changes = false;
        if (imageChange){
            changes = true;
        }
        if (!description2.equals(pDescricption)){
            changes = true;
        }
        if(!price2.equals(price)){
            changes = true;
        }
        if(!pname2.equals(productName)){
            changes = true;
        }
        if(!ptype2.equals(pType)){
            changes = true;
        }
        if(!addInfo2.equals(pAddInfo)){
            changes = true;
        }
        if(changes){
            new MaterialAlertDialogBuilder(ProductViewEdit2.this).setTitle("WySP Alert")
                    .setMessage("Discard changes?")
                    .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ProductViewEdit2.this,ShopF.class);
                    startActivity(intent);
                }
            }).setCancelable(true).show();
        }else{
            Intent intent = new Intent(ProductViewEdit2.this,ShopF.class);
            startActivity(intent);
        }
    }

    private void checkForPermission() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Change Images?")
                .setMessage("Select atleast 3 images starting with the main image")
                .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(ActivityCompat.checkSelfPermission(ProductViewEdit2.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(ProductViewEdit2.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                            return;
                        }
                        startGalleryIntent();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setCancelable(true).show();

    }

    private void startGalleryIntent() {
        Matisse.from(ProductViewEdit2.this)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case REQUEST_CODE_CHOOSE:
                imageChange = true;

                List<Uri> uris = Matisse.obtainResult(data);

                for(int i = 0; i<imagePathSize;i++){
                    adapter.deleteItem(i);
                }

                for(int i =0;i<uris.size();i++){
                    Uri uri = uris.get(i);
                    newImageuris.add(uri);
                    SliderItem sliderItem = new SliderItem(uri);
                    adapter.addItem(sliderItem);
                }
                break;
            case PERMISSION_REQUEST_CODE:
                checkForPermission();
                break;
        }
    }

    private void validateData() {
        pDescricptionView.setError(null);
        priceView.setError(null);
        productNameView.setError(null);
        pTypeView.setError(null);

        addInfo2 = pAddInfoView.getText().toString();
        description2 = pDescricptionView.getText().toString();
        price2 = priceView.getText().toString();
        pname2 = productNameView.getText().toString();
        ptype2 = pTypeView.getText().toString();

        Boolean cancel = false;
        Boolean change = false;
        View focusView = null;

        if (TextUtils.isEmpty(description2)){
            pDescricptionView.setError("This field is required");
            focusView = pDescricptionView;
            cancel = true;
        }else if (!description2.equals(pDescricption)){
            change = true;
        }
        if (TextUtils.isEmpty(price2)){
            priceView.setError("This field is required");
            focusView = priceView;
            cancel = true;
        }else if(!price2.equals(price)){
            change = true;
        }
        if (TextUtils.isEmpty(pname2)){
            productNameView.setError("This field is required");
            focusView = productNameView;
            cancel = true;
        }else if(!pname2.equals(productName)){
            change = true;
        }
        if (TextUtils.isEmpty(ptype2)){
            pTypeView.setError("This field is required");
            focusView = pTypeView;
            cancel = true;
        }else if(!ptype2.equals(pType)){
            change = true;
        }
        if (cancel){
            focusView.requestFocus();
        }else if(change){
            progressBar.setVisibility(View.VISIBLE);
            if (imageChange){
                uploadImagesToFirebase();
            }else{
                updateChangedData();
            }
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            new MaterialAlertDialogBuilder(ProductViewEdit2.this).setTitle("WySP Alert")
                    .setMessage("No changes have been made")
                    .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            return;
        }
    }

    private void updateChangedData() {

        ArrayList<String> oldUris = new ArrayList<>();
        for(int i=0;i<imageUris.size();i++){
            oldUris.add(String.valueOf(imageUris.get(i)));
        }
        product = new Product(pname2,price2,ptype2,description2,addInfo2,pShopType,oldUris,currencySymbol);

        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        DocumentReference w1 = FirebaseFirestore.getInstance().collection("Products").document(docID);
        batch.set(w1,product);
        DocumentReference w2 = FirebaseFirestore.getInstance().collection("SellerProducts").document(uid).collection(email).document(docID);
        batch.set(w2,product);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(ProductViewEdit2.this,ShopF.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(ProductViewEdit2.this,"Failed!",Toast.LENGTH_LONG);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void uploadImagesToFirebase() {
        ArrayList<String> newImagePaths = new ArrayList<>();
        for(int i= 0; i<newImageuris.size(); i++){
            Uri image = newImageuris.get(i);
            UUID id = UUID.randomUUID();
            final String path = "Images/" + id;
            newImagePaths.add(path);
            storageReference = FirebaseStorage.getInstance().getReference().child(path);
            UploadTask uploadTask = storageReference.putFile(image);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("wysp","upload success");
                    Toast.makeText(ProductViewEdit2.this,"Upload Successfull",Toast.LENGTH_SHORT);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("wysp","upload failed!!",e);
                    Toast.makeText(ProductViewEdit2.this,"Upload Failed!",Toast.LENGTH_SHORT);
                }
            });
        }
        product = new Product(pname2,price2,ptype2,description2,addInfo2,pShopType,newImagePaths,currencySymbol);
        sendProductDataToFirebase();
    }

    private void sendProductDataToFirebase() {

        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        DocumentReference w1 = FirebaseFirestore.getInstance().collection("Products").document(docID);
        batch.set(w1,product);
        DocumentReference w2 = FirebaseFirestore.getInstance().collection("SellerProducts").document(uid).collection(email).document(docID);
        batch.set(w2,product);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(ProductViewEdit2.this,ShopF.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(ProductViewEdit2.this,"Failed!",Toast.LENGTH_LONG);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkForChanges();
    }
}
