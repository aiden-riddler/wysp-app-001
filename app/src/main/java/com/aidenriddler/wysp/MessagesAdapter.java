package com.aidenriddler.wysp;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageHolder> {

    private ArrayList<ChatMessage> chatMessages;
    private String email;

    public MessagesAdapter(ArrayList<ChatMessage> chatMessages, String email) {
        this.chatMessages = chatMessages;
        this.email = email;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_message,parent,false);
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.messageView.setText(chatMessages.get(position).getMessage());
        Boolean sentByme = chatMessages.get(position).getSentByme();
        Boolean read = chatMessages.get(position).getRead();
        Boolean delivered = chatMessages.get(position).getDelivered();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String time = simpleDateFormat.format(chatMessages.get(position).getTimestamp());
        holder.timeView.setText(time);
        if (sentByme == true){
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(holder.constraintLayout);
            constraintSet.connect(R.id.messageCard,ConstraintSet.END,R.id.parent_layout,ConstraintSet.END,16);
            constraintSet.clear(R.id.messageCard,ConstraintSet.START);
            constraintSet.applyTo(holder.constraintLayout);
            if (read == true){
                holder.mesageStatus.setImageResource(R.drawable.read);
            }else if(delivered == true){
                holder.mesageStatus.setImageResource(R.drawable.delivered);
            }
        }else{
            holder.mesageStatus.setVisibility(View.INVISIBLE);
            final String senderEmail = chatMessages.get(position).getEmail();
            FirebaseFirestore.getInstance().collection(senderEmail).whereEqualTo("email",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> docIDs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.toObject(ChatMessage.class).getRead() == false){
                                docIDs.add(document.getId());
                            }
                        }
                        for(int i=0; i<docIDs.size(); i++){
                            FirebaseFirestore.getInstance().collection(senderEmail).document(docIDs.get(i)).update("read",true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        Log.d("wysp","FAILED",task.getException());
                                        return;
                                    }else {
                                        Log.d("wysp","UPDATE READ SUCCESSFUL");
                                    }
                                }
                            });
                        }
                    }
                });
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder{

        //declaration
        TextView messageView;
        CardView messageCard;
        ConstraintLayout constraintLayout;
        ImageView mesageStatus;
        TextView timeView;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            //initialisation
            messageView = itemView.findViewById(R.id.messageHolder);
            messageCard = itemView.findViewById(R.id.messageCard);
            mesageStatus = itemView.findViewById(R.id.messageStatus);
            constraintLayout = itemView.findViewById(R.id.parent_layout);
            timeView = itemView.findViewById(R.id.timeHolder);
        }
    }
}
