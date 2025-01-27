package com.oykuatak.oyku.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oykuatak.oyku.Model.MessageRequest;
import com.oykuatak.oyku.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageRequestsAdapter extends RecyclerView.Adapter<MessageRequestsAdapter.MessageRequestsHolder> {
    private ArrayList<MessageRequest> mMessageRequestsList;
    private Context mContext;
    private MessageRequest messageRequest,newMessageRequest;
    private View v;
    private int mPos;//adapter ın position ı için
    private String UID,name,profileUrl;
    private FirebaseFirestore firestore;

    public MessageRequestsAdapter(ArrayList<MessageRequest> mMessageRequestsList, Context mContext,String UID,String name,String profileUrl) {
        this.mMessageRequestsList = mMessageRequestsList;
        this.mContext = mContext;
        this.UID=UID;
        this.name=name;
        this.profileUrl=profileUrl;
        firestore= FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MessageRequestsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(mContext).inflate(R.layout.incoming_message_requests_item,parent,false);

        return new MessageRequestsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRequestsHolder holder, int position) {
        messageRequest = mMessageRequestsList.get(position);
        holder.textMessage.setText(messageRequest.getUserName() + "Kullanıcısı Size Mesaj Göndermek İstiyor.");

        if (messageRequest.getUserProfile().equals("default"))
            holder.imgProfile.setImageResource(R.mipmap.ic_launcher);
        else
            Picasso.get().load(messageRequest.getUserProfile()).resize(77,77).into(holder.imgProfile);

        holder.imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPos = holder.getAdapterPosition();
                if (mPos != RecyclerView.NO_POSITION) {
                    newMessageRequest = new MessageRequest(
                            mMessageRequestsList.get(mPos).getChannelId(),
                            mMessageRequestsList.get(mPos).getUserId(),
                            mMessageRequestsList.get(mPos).getUserName(),
                            mMessageRequestsList.get(mPos).getUserProfile()
                    );

                    firestore.collection("Users").document(UID)
                            .collection("Channel").document(mMessageRequestsList.get(mPos).getUserId())
                            .set(newMessageRequest)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        newMessageRequest = new MessageRequest(
                                                mMessageRequestsList.get(mPos).getChannelId(),
                                                UID,
                                                name,
                                                profileUrl
                                        );

                                        firestore.collection("Users").document(mMessageRequestsList.get(mPos).getUserId())
                                                .collection("Channel").document(UID)
                                                .set(newMessageRequest)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            delete(mMessageRequestsList.get(mPos).getUserId(), "Mesaj İsteği Başarıyla Kabul Edildi.");
                                                        } else {
                                                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPos = holder.getAdapterPosition();
                if (mPos!=RecyclerView.NO_POSITION)
                    delete(mMessageRequestsList.get(mPos).getUserId(),"Mesaj İsteği Reddedildi.");

            }
        });

    }

    @Override
    public int getItemCount() {
        return mMessageRequestsList.size();
    }

    class MessageRequestsHolder extends RecyclerView.ViewHolder{
        CircleImageView imgProfile;
        TextView textMessage;
        ImageView imgClose,imgCheck;


        public MessageRequestsHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.incoming_message_requests_item_imgProfile);
            textMessage = itemView.findViewById(R.id.incoming_message_requests_item_textMessage);
            imgClose=itemView.findViewById(R.id.incoming_message_requests_item_imgClose);
            imgCheck=itemView.findViewById(R.id.incoming_message_requests_item_imgCheck);
        }
    }
    private void delete(String targetUUID, final String messageContent){
        firestore.collection("MessageRequests").document(UID).collection("Requests").document(targetUUID)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           notifyDataSetChanged();
                           Toast.makeText(mContext, messageContent,Toast.LENGTH_SHORT).show();

                       }
                           else Toast.makeText(mContext,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
