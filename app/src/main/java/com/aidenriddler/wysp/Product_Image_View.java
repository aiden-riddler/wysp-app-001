package com.aidenriddler.wysp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Product_Image_View extends AppCompatActivity {
    //declaration
    private SliderView sliderView;
    private List<Uri> imageUris = new ArrayList<>();
    private EditText productNameView;
    private EditText priceView;
    private EditText pTypeView;
    private EditText pDescricptionView;
    private EditText pAddInfoView;
    private Button submit;
    private String productShopType;

    private int numberOfImages;

    //intent
    private String email;
    private String uid;
    private String currencySymbol;

    //spinkit
    private ProgressBar progressBar;

    //firebase
    private StorageReference storageReference;

    //product
    private Product product;
    private String addInfo;
    private String description;
    private String price;
    private String pname;
    private String ptype;

    private FirebaseFirestore db;
    private DocumentReference dr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_image_view);

        //initialisation
        sliderView = findViewById(R.id.imageSlider);
        pAddInfoView = findViewById(R.id.add_info);
        pDescricptionView = findViewById(R.id.Q_desc);
        priceView = findViewById(R.id.price);
        productNameView = findViewById(R.id.product_name);
        pTypeView = findViewById(R.id.p_type_name);
        submit = findViewById(R.id.submit);


        Intent intent = getIntent();
        productShopType =  intent.getStringExtra("productShopType");
        numberOfImages = Integer.parseInt(intent.getStringExtra("NumberOfImages"));

        email = intent.getStringExtra("email");
        uid = intent.getStringExtra("uid");
        currencySymbol = intent.getStringExtra("currencySymbol");
        Log.d("wysp",currencySymbol);

        //spinkit
        progressBar = findViewById(R.id.spin_kit);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);

        //sliderview
        SliderAdapter adapter = new SliderAdapter(Product_Image_View.this);
        for(int i = 0; i < numberOfImages;i++){
            Uri uri = Uri.parse(getIntent().getStringExtra(String.valueOf(i)));
            imageUris.add(uri);
            SliderItem sliderItem = new SliderItem(uri);
            adapter.addItem(sliderItem);

        }
        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();

        //firebase


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {
        pDescricptionView.setError(null);
        priceView.setError(null);
        productNameView.setError(null);
        pTypeView.setError(null);

        addInfo = pAddInfoView.getText().toString();
        description = pDescricptionView.getText().toString();
        price = priceView.getText().toString();
        pname = productNameView.getText().toString();
        ptype = pTypeView.getText().toString();

        Boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(description)){
            pDescricptionView.setError("This field is required");
            focusView = pDescricptionView;
            cancel = true;
        }
        if (TextUtils.isEmpty(price)){
            priceView.setError("This field is required");
            focusView = priceView;
            cancel = true;
        }
        if (TextUtils.isEmpty(pname)){
            productNameView.setError("This field is required");
            focusView = productNameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(ptype)){
            pTypeView.setError("This field is required");
            focusView = pTypeView;
            cancel = true;
        }
        if (cancel){
            focusView.requestFocus();
        }else{
            submit.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            uploadImagesToFirebase();
        }
    }

    private void uploadImagesToFirebase() {
        ArrayList<String> imagePaths = new ArrayList<>();
        for(int i= 0; i<imageUris.size(); i++){
            Uri image = imageUris.get(i);
            UUID id = UUID.randomUUID();
            final String path = "Images/" + id;
            imagePaths.add(path);
            storageReference = FirebaseStorage.getInstance().getReference().child(path);
            UploadTask uploadTask = storageReference.putFile(image);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("wysp","upload success");
                    Toast.makeText(Product_Image_View.this,"Upload Successfull",Toast.LENGTH_SHORT);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("wysp","upload failed!!",e);
                    Toast.makeText(Product_Image_View.this,"Upload Failed!",Toast.LENGTH_SHORT);
                }
            });
        }
        product = new Product(pname,price,ptype,description,addInfo,productShopType,imagePaths,currencySymbol);
        sendProductDataToFirebase();
    }

    private void sendProductDataToFirebase() {

        dr = db.collection("CurrentProductID").document("PID");
        db.runTransaction(new Transaction.Function<String>() {
            @Nullable
            @Override
            public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(dr);
                String currentID = String.valueOf(snapshot.get("ID"));
                String productID = "PID" + currentID;
                db.collection("Products").document(productID).set(product);
                db.collection("SellerProducts").document(uid).collection(email).document(productID).set(product);
                int newID = Integer.parseInt(currentID) + 1;
                transaction.update(dr,"ID",String.valueOf(newID));
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(Product_Image_View.this,ShopF.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(Product_Image_View.this,"Failed!",Toast.LENGTH_LONG);
                    submit.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
