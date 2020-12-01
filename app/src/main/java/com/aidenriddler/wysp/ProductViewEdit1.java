package com.aidenriddler.wysp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class ProductViewEdit1 extends AppCompatActivity {

    //declaration
    private TextView productNameView;
    private TextView priceView;
    private TextView pTypeView;
    private TextView pDescricptionView;
    private TextView pAddInfoView;
    private TextView currencySymbolView;

    private String productName;
    private String price;
    private String pType;
    private String pDescricption;
    private String pAddInfo;
    private String pShopType;
    private String currencySymbol;
    private int imagePathSize;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private ArrayList<Uri> imageUris = new ArrayList<>();

    //slider
    private SliderView sliderView;

    //firebase
    private StorageReference storageReference;
    private DocumentReference documentReference;
    private String docID;
    private String email;
    private String uid;

    //toolbar
//    private MaterialToolbar toolbar;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_view_edit1);

        //intents
        final Intent intent = getIntent();
        docID = intent.getStringExtra("reference");
        email = intent.getStringExtra("email");
        uid = intent.getStringExtra("uid");
        productName = intent.getStringExtra("productName");
        price = intent.getStringExtra("price");
        pType = intent.getStringExtra("pType");
        pShopType = intent.getStringExtra("pShopType");
        pDescricption = intent.getStringExtra("pDescricption");
        pAddInfo = intent.getStringExtra("pAddInfo");
        currencySymbol = intent.getStringExtra("currencySymbol");
        imagePathSize = Integer.parseInt(intent.getStringExtra("imagePathSize"));
        for(int i = 0; i<imagePathSize;i++){
            String path = intent.getStringExtra(String.valueOf(i));
            imagePaths.add(path);
        }


        //initialisation
//        toolbar = findViewById(R.id.top_app_bar);
//        setSupportActionBar(toolbar);
        sliderView = findViewById(R.id.imageSlider);
        productNameView = findViewById(R.id.product_name);
        priceView = findViewById(R.id.price);
        pTypeView = findViewById(R.id.p_type_name);
        pDescricptionView = findViewById(R.id.Q_desc);
        pAddInfoView = findViewById(R.id.add_info);
        currencySymbolView = findViewById(R.id.currencySymbol);
        progressBar = findViewById(R.id.progressBar);

        productNameView.setText(productName);
        priceView.setText(price);
        pTypeView.setText(pType);
        pDescricptionView.setText(pDescricption);
        pAddInfoView.setText(pAddInfo);
        currencySymbolView.setText(currencySymbol);



        //firebase & slider
        final SliderAdapter adapter = new SliderAdapter(ProductViewEdit1.this);
        for(int i = 0; i<imagePathSize;i++){
            storageReference = FirebaseStorage.getInstance().getReference().child(imagePaths.get(i));
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageUris.add(uri);
                    SliderItem sliderItem = new SliderItem(uri);
                    adapter.addItem(sliderItem);
                }
            });
        }
        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit:
                Intent intent1 = new Intent(ProductViewEdit1.this,ProductViewEdit2.class);
                intent1.putExtra("reference", docID);
                intent1.putExtra("productName",productName);
                intent1.putExtra("price",price);
                intent1.putExtra("pType",pType);
                intent1.putExtra("pDescricption",pDescricption);
                intent1.putExtra("pAddInfo",pAddInfo);
                intent1.putExtra("pShopType",pShopType);
                intent1.putExtra("currencySymbol",currencySymbol);
                intent1.putExtra("imagePathSize",String.valueOf(imagePathSize));
                intent1.putExtra("email",email);
                intent1.putExtra("uid",uid);
                for(int i = 0; i<imagePathSize;i++){
                    intent1.putExtra(String.valueOf(i),String.valueOf(imageUris.get(i)));
                }
                startActivity(intent1);

                break;
            case R.id.delete:
                new MaterialAlertDialogBuilder(ProductViewEdit1.this)
                        .setTitle("Delete this product?")
                        .setMessage("This product will be deleted from your account")
                        .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setIndeterminate(true);
                        progressBar.setVisibility(View.VISIBLE);
                        documentReference = FirebaseFirestore.getInstance().collection("Products").document(uid).collection(email).document(docID);
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("wysp","Documnet deleted succesfully");
                                for(int i = 0;i<imagePathSize;i++){
                                    storageReference = FirebaseStorage.getInstance().getReference().child(imagePaths.get(i));
                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("wysp","Delete successful");
                                        }
                                    });
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent2 = new Intent(ProductViewEdit1.this,ShopF.class);
                                startActivity(intent2);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ProductViewEdit1.this,"Failed",Toast.LENGTH_LONG);
                            }
                        });
                    }
                }).setCancelable(true).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
