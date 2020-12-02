package com.aidenriddler.wysp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder> {

    private ArrayList<ChatMessage> chatMessages;
    private Map<String, String> readCountMap;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public ChatMessagesAdapter(ArrayList<ChatMessage> chatMessages, Map<String, String> readCountMap, Context context, OnItemClickListener onItemClickListener) {
        this.chatMessages = chatMessages;
        this.readCountMap = readCountMap;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_adapter,parent,false);
        return new ViewHolder(v,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String readCount = readCountMap.get(chatMessages.get(position).getEmail());
        holder.messageView.setText(chatMessages.get(position).getMessage());
        if (Integer.parseInt(readCount) > 0){
            holder.chatsNumber.setText(readCount);
        }else {
            holder.chatsNumber.setVisibility(View.INVISIBLE);
        }
        holder.senderNameView.setText(chatMessages.get(position).getUserID());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String time = simpleDateFormat.format(chatMessages.get(position).getTimestamp());
        holder.timeView.setText(time);
        Picasso.get().load(chatMessages.get(position).getProfileImage()).into(holder.profilePhotoView);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //declaration
        TextView messageView;
        TextView timeView;
        ImageView profilePhotoView;
        TextView senderNameView;
        TextView chatsNumber;
        OnItemClickListener listener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener){
            super(itemView);

            messageView = itemView.findViewById(R.id.message);
            timeView = itemView.findViewById(R.id.time);
            profilePhotoView = itemView.findViewById(R.id.profile_image);
            senderNameView = itemView.findViewById(R.id.senderName);
            chatsNumber = itemView.findViewById(R.id.chatsNumber);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.OnItemClick(getAdapterPosition());
        }
    }
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
}
