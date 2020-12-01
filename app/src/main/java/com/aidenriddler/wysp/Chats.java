package com.aidenriddler.wysp;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chats extends AppCompatActivity implements ChatMessagesAdapter.OnItemClickListener {

    //declaration
    private TextView shop;
    private CardView orangeCard;
    private TextView settings;

    private FloatingActionButton newChat;
    
    private CircleImageView profileImage;
    private Uri profileImageUri;

    //firebase
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private CollectionReference chatsRef;

    //request codes
    public static final int PERMISSION_REQUEST_CODE = 100;
    public static final int SELECT_PICTURE = 102;
    private int REQUEST_CODE_CHOOSE = 300;

    //recyclerView
    private RecyclerView chatsRecycler;
    private ChatMessagesAdapter adapter;
    private ArrayList<ChatMessage> latestMessages = new ArrayList<>();
    private ArrayList<String> docIDs = new ArrayList<>();

    private String email;
    private String uid;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);
        setupWindowAnimations();

        getSupportActionBar().hide();

        //intents
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        uid = intent.getStringExtra("uid");

        if (email == null){
            mAuth = FirebaseAuth.getInstance();
            email = mAuth.getCurrentUser().getEmail();
        }

        //initialisation
        shop = findViewById(R.id.shop);
        orangeCard = findViewById(R.id.cardView);
        settings = findViewById(R.id.settings);
        profileImage = findViewById(R.id.profile_image);
        chatsRecycler = findViewById(R.id.chats_recycler);
        newChat = findViewById(R.id.newChat);

        //clicks
        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchContactSearch();
            }
        });
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShopActivityTransition();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingsActivityTransition();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(Chats.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Chats.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                    return;
                }
                startGalleryIntent();
            }
        });

        //firebase
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("Images/" + email + "/ProfileImage");

        db = FirebaseFirestore.getInstance();
        chatsRef = db.collection(email);

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("wysp","Failed!!! - ",e);
            }
        });
        setUpRecyclerView();
    }

    private void launchContactSearch() {
        Intent intent = new Intent(Chats.this,ContactSearch.class);
        startActivity(intent);
    }

    private void setUpRecyclerView() {
        chatsRef.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w("wysp", "listen:error", error);
                    return;
                }
                ArrayList<ChatMessage> chatMessages = new ArrayList<>();
                ArrayList<String> usedDeliveryEmails = new ArrayList<>();
                for (QueryDocumentSnapshot doc: value){
                    ChatMessage message = doc.toObject(ChatMessage.class);
                    chatMessages.add(message);
                }

                for (int a = 0; a <chatMessages.size(); a++){
                    Boolean usedDelivery = false;
                    final ChatMessage delChatMessage =  chatMessages.get(a);
                    for (int b=0; b<usedDeliveryEmails.size();b++){
                        if (usedDeliveryEmails.get(b).equals(delChatMessage.getEmail())){
                            usedDelivery = true;
                        }
                    }
                    usedDeliveryEmails.add(delChatMessage.getEmail());
                    if (usedDelivery == false){
                        FirebaseFirestore.getInstance().collection(delChatMessage.getEmail()).whereEqualTo("email",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    ArrayList<String> documentIDs = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ChatMessage chatMessage = document.toObject(ChatMessage.class);
                                        if (chatMessage.getDelivered() == false){
                                            documentIDs.add(document.getId());
                                        }
                                    }

                                    for (int d=0;d<documentIDs.size();d++){
                                        FirebaseFirestore.getInstance().collection(delChatMessage.getEmail()).document(documentIDs.get(d))
                                                .update("delivered",true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()){
                                                    Log.d("wysp","FAILED",task.getException());
                                                    return;
                                                }
                                                Log.d("wysp","DELIVERY UPDATE SUCCESFUL");
                                            }
                                        });
                                    }

                                }
                            }
                        });
                    }

                }



                Map<String,String> readCountMap = new HashMap<>();
                ArrayList<String> usedEmails = new ArrayList<>();
                latestMessages.clear();

                for (int i = 0; i < chatMessages.size(); i++){
                    int readCount = 0;
                    Boolean used = false;
                    ChatMessage chatMessage = chatMessages.get(i);

                    for (int k = 0; k<usedEmails.size();k++){
                        if (chatMessage.getEmail().equals(usedEmails.get(k))){
                            used = true;
                        }
                    }

                    if (used == false){
                        usedEmails.add(chatMessage.getEmail());
                        for (int j = 0; j < chatMessages.size(); j++){
                            if (chatMessage.getEmail().equals(chatMessages.get(i).getEmail())){
                                if (chatMessages.get(i).getRead() == false && chatMessages.get(i).getSentByme() == false){
                                    readCount++;
                                }
                            }
                        }
                        latestMessages.add(chatMessage);
                        readCountMap.put(chatMessage.getEmail(),String.valueOf(readCount));
                    }
                }
                adapter = new ChatMessagesAdapter(latestMessages,readCountMap,Chats.this,Chats.this);
                chatsRecycler.setHasFixedSize(true);
                chatsRecycler.setLayoutManager(new LinearLayoutManager(Chats.this));
                chatsRecycler.setAdapter(adapter);
            }
        });
    }

    private void startGalleryIntent() {
        Matisse.from(Chats.this)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(1)
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

        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                storageRef.delete();
                Uri selectedImage = data.getData();
                UploadTask uploadTask = storageRef.putFile(selectedImage);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Chats.this,"Upload Successfull",Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Chats.this,"Upload Failed!",Toast.LENGTH_SHORT);
                    }
                });
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return storageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            profileImageUri = task.getResult();
                            Picasso.get().load(profileImageUri).into(profileImage);
                        }
                    }
                });
            }else if(requestCode == REQUEST_CODE_CHOOSE){
                List<Uri> uris = Matisse.obtainResult(data);
                Uri uri = uris.get(0);

                storageRef.delete();
                UploadTask uploadTask = storageRef.putFile(uri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Chats.this,"Upload Successfull",Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Chats.this,"Upload Failed!",Toast.LENGTH_SHORT);
                    }
                });
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return storageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            profileImageUri = task.getResult();
                            Picasso.get().load(profileImageUri).into(profileImage);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startGalleryIntent();
                }else{
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("WySP Alert")
                            .setMessage("You need to grant permission in order to change profile picure")
                            .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
        }
    }

    private void startSettingsActivityTransition() {
        Intent intent = new Intent(Chats.this,Settings.class);
        View sharedView = orangeCard;
        String transitionName = getString(R.string.stc_trans);
        intent.putExtra("email",email);
        intent.putExtra("uid",uid);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Chats.this,sharedView,transitionName);
        startActivity(intent,activityOptions.toBundle());
    }

    private void startShopActivityTransition() {
        Intent intent = new Intent(Chats.this,ShopF.class);
        View sharedView = orangeCard;
        String transitionName = getString(R.string.stc_trans);

        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Chats.this,sharedView,transitionName);
        startActivity(intent,activityOptions.toBundle());
    }

    private void setupWindowAnimations() {
        Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void OnItemClick(int position) {
        latestMessages.get(position);
        Intent intent = new Intent(Chats.this,ChatView.class);
        intent.putExtra("intentEmail",latestMessages.get(position).getEmail());
        Log.d("wysp",email);
        intent.putExtra("email",email);
        intent.putExtra("uid",uid);
        startActivity(intent);
    }
}
