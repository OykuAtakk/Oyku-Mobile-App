package com.oykuatak.oyku.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.oykuatak.oyku.Adapter.ChatAdapter;
import com.oykuatak.oyku.Model.Chat;
import com.oykuatak.oyku.Model.User;
import com.oykuatak.oyku.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    private FirebaseUser mUser;
    private HashMap<String,Object> mData;
    private RecyclerView recyclerView;
    private EditText editMessage;
    private String txtMessage,messageDocId;
    private CircleImageView targetProfile;
    private TextView targetName;
    private Intent incomingIntent;
    private String targetId ,channelId,targetProfileUrl;
    private DocumentReference targetRef;
    private User targetUser;
    private FirebaseFirestore firestore;
    private Query chatQuery;
    private ArrayList<Chat> chatList;
    private Chat chat;
    private ChatAdapter chatAdapter;

    private void init(){
        recyclerView = (RecyclerView)findViewById(R.id.chat_activity_recyclerView);
        editMessage = (EditText)findViewById(R.id.chat_activity_editMessage);
        targetProfile= (CircleImageView) findViewById(R.id.chat_activity_imgTargetProfile);
        targetName=(TextView) findViewById(R.id.chat_activity_textTargetName);

        mUser= FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        incomingIntent = getIntent();
        targetId = incomingIntent.getStringExtra("targetId");
        channelId= incomingIntent.getStringExtra("channelId");
        targetProfileUrl = incomingIntent.getStringExtra("targetProfile");
        chatList = new ArrayList<>();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();

        targetRef = firestore.collection("Users").document(targetId);
        targetRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value!=null && value.exists()){
                    targetName.setText(targetUser.getUserName());
                    if (targetUser!=null){
                        targetName.setText(targetUser.getUserName());
                        if (targetUser.getUserProfile().equals("default"))
                            targetProfile.setImageResource(R.mipmap.ic_launcher);
                        else Picasso.get().load(targetUser.getUserProfile()).resize(76,76).into(targetProfile);
                    }
                }
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        chatQuery = firestore.collection("ChatChannels").document(channelId).collection("Messages")
                .orderBy("mesajTarihi", Query.Direction.ASCENDING);
        chatQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(ChatActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value!=null){
                    chatList.clear();
                    for (DocumentSnapshot snapshot: value.getDocuments()){
                        chat = snapshot.toObject(Chat.class);

                        assert chat !=null;
                        chatList.add(chat);
                    }
                    chatAdapter = new ChatAdapter(chatList,ChatActivity.this,mUser.getUid(),targetProfileUrl);
                    recyclerView.setAdapter(chatAdapter);
                }

            }
        });


    }
    public void sendMessageButton(View v){
        txtMessage = editMessage.getText().toString();
        if (!TextUtils.isEmpty(txtMessage)){
            messageDocId = UUID.randomUUID().toString();
            mData = new HashMap<>();
            mData.put("mesajIcerigi",txtMessage);
            mData.put("gonderen", mUser.getUid());
            mData.put("alici", targetId);
            mData.put("MesajTarihi", FieldValue.serverTimestamp());
            mData.put("docId",messageDocId);

            firestore.collection("ChatChannel").document(channelId).collection("Messages").document(messageDocId)
                    .set(mData).addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                editMessage.setText("");
                            else Toast.makeText(ChatActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }else Toast.makeText(ChatActivity.this,"Mesaj Göndermek için Bir Şeyler Yazın",Toast.LENGTH_SHORT).show();

    }

    public void chatCloseButton(View v){
        finish();

    }
}