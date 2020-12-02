package com.aidenriddler.wysp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopF extends AppCompatActivity {

    //declaration
    private TextView chats;
    private CardView orangeCard;
    private TextView settings;

    private TextView add_product;
    private CardView addProductCard;
    private TextView setup_location;
    private CardView setupLocationCard;

    //shops
    private Map<String, String> shopTypes;
    private String shopName;
    private  Shop shop;
    private String currencySymbol;

    //recycler view
    private RecyclerView productRecycler;
    private ProductAdapter adapter;

    //firebase
    private FirebaseAuth mAuth;
    private CollectionReference productRef;
    private DocumentReference documentReference;
    private String uid;
    private String email;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);

        setupWindowAnimations();
        getSupportActionBar().hide();

        //initialisation
        chats = findViewById(R.id.chats);
        orangeCard = findViewById(R.id.cardView);
        settings = findViewById(R.id.settings);

        add_product = findViewById(R.id.firstTextView);
        setup_location = findViewById(R.id.second_TextView);
        addProductCard = findViewById(R.id.add_product_card);
        setupLocationCard = findViewById(R.id.setup_location_card);
        productRecycler = findViewById(R.id.product_recycler);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();
        uid = mAuth.getCurrentUser().getUid();
        productRef = FirebaseFirestore.getInstance().collection("SellerProducts").document(uid).collection(email);
        documentReference = FirebaseFirestore.getInstance().collection("Shops").document(uid);
        Source source = Source.CACHE;

        documentReference.get(source).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                shop = documentSnapshot.toObject(Shop.class);
                shopTypes = shop.getShopTypes();
                shopName = shop.getShopName();
                Log.d("wysp",shop.getCountry());
                for(int i=0; i < shopTypes.size(); i++){
                    Log.d("wysp", shopTypes.get(String.valueOf(i)));
                }
                currencySymbol = shop.getCurrency();
                Log.d("wysp",currencySymbol);
                chats.setClickable(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("wysp","FAILED!!! ",e);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        shop = documentSnapshot.toObject(Shop.class);
                        shopTypes = shop.getShopTypes();
                        shopName = shop.getShopName();
                        currencySymbol = shop.getCurrency();
                    }
                });
            }
        });

        //clicks
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatsActivityTransition();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingsActivityTransition();
            }
        });
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddProduct();
            }
        });
        addProductCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddProduct();
            }
        });

        setUpRecycler();
    }

    private void setUpRecycler() {
        Query query = productRef;
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query,Product.class).build();
        adapter = new ProductAdapter(options);
        productRecycler.setHasFixedSize(true);
        productRecycler.setLayoutManager(new GridLayoutManager(ShopF.this,3));
        productRecycler.setAdapter(adapter);

        adapter.setOnItemClickListener1(new ProductAdapter.OnItemClickListener1() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Product product = documentSnapshot.toObject(Product.class);
                DocumentReference reference = documentSnapshot.getReference();
                Intent intent = new Intent(ShopF.this,ProductViewEdit1.class);
                intent.putExtra("reference", reference.getId());
                intent.putExtra("productName",product.getProductName());
                intent.putExtra("price",product.getPrice());
                intent.putExtra("pType",product.getpType());
                intent.putExtra("pDescricption",product.getpDescricption());
                intent.putExtra("pAddInfo",product.getpDescricption());
                intent.putExtra("pShopType",product.getpShopType());
                intent.putExtra("currencySymbol",product.getCurrencySymbol());
                intent.putExtra("email",email);
                intent.putExtra("uid",uid);
                ArrayList<String> imagePaths = product.getImagePaths();
                for(int i = 0; i<imagePaths.size();i++){
                    intent.putExtra(String.valueOf(i),imagePaths.get(i));
                }
                intent.putExtra("imagePathSize",String.valueOf(imagePaths.size()));
                startActivity(intent);
            }
        });
    }

    private void openAddProduct() {
        Intent intent = new Intent(ShopF.this,Add_product_page.class);
        for(int i=0; i < shopTypes.size(); i++){
            intent.putExtra(String.valueOf(i),shopTypes.get(String.valueOf(i)));
        }
        intent.putExtra("ShopTypesSize",String.valueOf(shopTypes.size()));
        intent.putExtra("uid",uid);
        Log.d("wysp",currencySymbol);
        intent.putExtra("currencySymbol",currencySymbol);
        intent.putExtra("email",email);
        startActivity(intent);
    }

    private void startSettingsActivityTransition() {
        Intent intent = new Intent(ShopF.this,Settings.class);
        View sharedView = orangeCard;
        String transitionName = getString(R.string.stc_trans);
        intent.putExtra("email",email);
        intent.putExtra("uid",uid);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ShopF.this,sharedView,transitionName);
        startActivity(intent,activityOptions.toBundle());
    }

    private void startChatsActivityTransition() {
        Intent intent = new Intent(ShopF.this,Chats.class);
        View sharedView = orangeCard;
        String transitionName = getString(R.string.stc_trans);
        intent.putExtra("email",email);
        intent.putExtra("uid",uid);
        intent.putExtra("shopName",shopName);

        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ShopF.this,sharedView,transitionName);
        startActivity(intent,activityOptions.toBundle());
    }

    private void setupWindowAnimations() {
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(slide);
    }
}
