package com.aidenriddler.wysp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ChatView extends AppCompatActivity {

    //declaration
    private CardView sendCard;
    private ImageView sendImage;
    private EditText messageView;

    //firebase
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference storageReference;
    private CollectionReference chatsRef;
    private CollectionReference otherChatsRef;
    private Uri profileImageUri;
    private String email;
    private String intentEmail;


    private String contact;
    private Uri contactUri;

    private String controlEmail;

    //recyclerView
    private RecyclerView chatsRecycler;
    private MessagesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        contact = intent.getStringExtra("Contact");
        intentEmail = intent.getStringExtra("intentEmail");
        if (contact != null){
            getSupportActionBar().setTitle(contact);
            controlEmail = contact;
        }else if (intentEmail != null){
            getSupportActionBar().setTitle(intentEmail);
            controlEmail = intentEmail;
        }
        email = intent.getStringExtra("email");
        Log.d("wysp",email);
        uid = intent.getStringExtra("uid");



        //initialization
        sendCard = findViewById(R.id.sendButton);
        sendImage = findViewById(R.id.send);
        messageView = findViewById(R.id.message);
        chatsRecycler = findViewById(R.id.chats_recycler);

        //firebase
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("Images/" + email + "/ProfileImage");
        storageReference = storage.getReference().child("Images/" + controlEmail + "/ProfileImage" );
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileImageUri = uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("wysp", "FAILED ERROR: ",e);
            }
        });

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                contactUri = uri;
            }
        });

        chatsRef = FirebaseFirestore.getInstance().collection(email);


        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateMessage();
            }
        });

        sendCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateMessage();
            }
        });
        setupRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.homeAsUp:
                Log.d("wysp","HOME AS UP PRESSED");
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {

        chatsRef.whereEqualTo("email",String.valueOf(controlEmail)).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("wysp", "listen:error", error);
                    return;
                }
                ArrayList<ChatMessage> chatMessages = new ArrayList<>();
                for (QueryDocumentSnapshot doc: value){
                    ChatMessage message = doc.toObject(ChatMessage.class);
                    chatMessages.add(message);
                }

                ArrayList<Date> dates = new ArrayList<>();
                Log.d("wysp","Dates before arraylist.sort");
                for (int a=0; a <chatMessages.size();a++){
                    dates.add(chatMessages.get(a).getTimestamp());
                    Log.d("wysp",chatMessages.get(a).getTimestamp().toString());
                }
                Collections.sort(dates);
                Log.d("wysp","Dates after arraylist.sort");

                for (int a=0; a <chatMessages.size();a++){
                    Log.d("wysp",dates.get(a).toString());
                    for (int b=0;b<chatMessages.size();b++){
                        if (chatMessages.get(b).getTimestamp().toString().equals(dates.get(a).toString())){
                            ChatMessage temp = chatMessages.get(a);
                            chatMessages.set(a,chatMessages.get(b));
                            chatMessages.set(b,temp);
                        }
                    }
                }

                adapter = new MessagesAdapter(chatMessages,email);
                chatsRecycler.setHasFixedSize(true);
                chatsRecycler.setLayoutManager(new LinearLayoutManager(ChatView.this));
                chatsRecycler.setAdapter(adapter);

            }
        });
    }

    private void validateMessage() {
        messageView.setError(null);
        String message = messageView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(message)){
            cancel = true;
            focusView = messageView;
        }

        if (cancel){
            focusView.requestFocus();
        }else{
            messageView.setText(" ");
            sendMessage(message);
        }
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message,uid,email,profileImageUri.toString(),new Date(),false,false,false,false);
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(controlEmail);
        collectionReference.add(chatMessage);
        CollectionReference collectionReference1 = FirebaseFirestore.getInstance().collection(email);
        chatMessage.setEmail(controlEmail);
        chatMessage.setSentByme(true);
        chatMessage.setProfileImage(String.valueOf(contactUri));
        collectionReference1.add(chatMessage);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatView.this,Chats.class);
        intent.putExtra("email",email);
        Log.d("wysp",email);
        intent.putExtra("uid",uid);
        startActivity(intent);
    }
}
