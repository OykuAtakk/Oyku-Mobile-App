package com.oykuatak.oyku.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oykuatak.oyku.Activity.ChatActivity;
import com.oykuatak.oyku.Model.MessageRequest;
import com.oykuatak.oyku.Model.User;
import com.oykuatak.oyku.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private ArrayList<User> userList;
    private Context context;
    private View v;
    private User user;
    private int uPos;

    private FirebaseFirestore firestore;
    private DocumentReference ref;
    private String UID,name,profileUrl, channelId,messageDocId;
    private MessageRequest messageRequest;
    private HashMap<String , Object> mData;

    private Dialog messageDialog;
    private ImageView imgClose;
    private LinearLayout postLinear;
    private CircleImageView imgProfile;
    private EditText editMessage;
    private String textMessage;
    private Window window;
    private TextView textName;
    private Intent chatIntent;



    public UserAdapter(ArrayList<User> userList, Context context,String UID, String name, String profileUrl) {
        this.userList = userList;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        this.UID = UID;
        this.name=name;
        this.profileUrl= profileUrl;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        user = userList.get(position);
        holder.userName.setText(user.getUserName());

        if(user.getUserProfile().equals("default"))
            holder.userProfile.setImageResource(R.mipmap.ic_launcher);
        else
            Picasso.get().load(user.getUserProfile()).resize(66,66).into(holder.userProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uPos=holder.getAdapterPosition();
                if(uPos != RecyclerView.NO_POSITION){
                    ref = firestore.collection("Users").document(UID).collection("Channel").document(userList.get(uPos).getUserId());
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                //chat Activity ye
                                chatIntent = new Intent(context, ChatActivity.class);
                                chatIntent.putExtra("channelId",documentSnapshot.getData().get("channelId").toString());
                                chatIntent.putExtra("targetId",userList.get(uPos).getUserId());
                                chatIntent.putExtra("targetProfile",userList.get(uPos).getUserProfile());
                                chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(chatIntent);

                            }else postMessageDialog(userList.get(uPos));
                        }
                    });
                }

            }
        });

    }

    private void postMessageDialog(final User user) {
        messageDialog = new Dialog(context);
        messageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window = messageDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        messageDialog.setContentView(R.layout.custom_dialog_post_message);

        imgClose = messageDialog.findViewById(R.id.imgClose);
        postLinear = messageDialog.findViewById(R.id.custom_dialog_post_message_linearPost);
        imgProfile=messageDialog.findViewById(R.id.custom_dialog_post_message_imgUserProfile);
        editMessage= messageDialog.findViewById(R.id.custom_editText);
        textName=messageDialog.findViewById(R.id.custom_dialog_post_message_textUserName);

        textName.setText(user.getUserName());

        if (user.getUserProfile().equals("default"))
            imgProfile.setImageResource(R.mipmap.ic_launcher);
        else
            Picasso.get().load(user.getUserProfile()).resize(126,126).into(imgProfile);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });

        postLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textMessage = editMessage.getText().toString();

                if (!TextUtils.isEmpty(textMessage)){
                    //Mesaj Gönderme İşlemi
                    channelId = UUID.randomUUID().toString();
                    messageRequest = new MessageRequest(channelId,UID,name,profileUrl);
                    firestore.collection("MessageRequests").document(user.getUserId())
                            .collection("Requests").document(UID)
                            .set(messageRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //Chat
                                        messageDocId = UUID.randomUUID().toString();
                                        mData = new HashMap<>();
                                        mData.put("mesajIcerigi",textMessage);
                                        mData.put("gonderen", UID);
                                        mData.put("alici", user.getUserId());
                                        mData.put("MesajTarihi", FieldValue.serverTimestamp());
                                        mData.put("docId",messageDocId);

                                        firestore.collection("ChatChannels").document(channelId).collection("Messages")
                                                .document(messageDocId).set(mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(context,"Mesaj İsteğiniz Başarıyla İletildi",Toast.LENGTH_SHORT).show();
                                                            if (messageDialog.isShowing())
                                                                messageDialog.dismiss();
                                                        }else Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });

                }else Toast.makeText(context, "Boş Mesaj Gönderemezsiniz!", Toast.LENGTH_SHORT).show();
            }
        });
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT);
        messageDialog.show();

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder{
        TextView userName;
        CircleImageView userProfile;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_item_text);
            userProfile = itemView.findViewById(R.id.user_item_imgUserProfile);
        }


    }
}
