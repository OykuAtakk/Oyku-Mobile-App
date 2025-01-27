package com.oykuatak.oyku.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.oykuatak.oyku.Adapter.BlogAdapter;
import com.oykuatak.oyku.Adapter.MessageRequestsAdapter;
import com.oykuatak.oyku.Fragment.MessageFragment;
import com.oykuatak.oyku.Fragment.ProfileFragment;
import com.oykuatak.oyku.Fragment.UsersFragment;
import com.oykuatak.oyku.Model.Blog;
import com.oykuatak.oyku.Model.MessageRequest;
import com.oykuatak.oyku.Model.User;
import com.oykuatak.oyku.R;
import com.oykuatak.oyku.databinding.ActivityFeedBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private UsersFragment usersFragment;
    private ProfileFragment profileFragment;
    private MessageFragment messageFragment;
    ArrayList<Blog> blogList;
    private ActivityFeedBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private BottomNavigationView bottomView;
    private FragmentTransaction transaction;
    private Toolbar toolbar;
    private RelativeLayout relaNoti;
    private TextView textNotiNumber;
    private FirebaseFirestore firestore;
    private Query query;
    private FirebaseUser mUser;
    private MessageRequest mMessageRequest;
    private ArrayList<MessageRequest> messageRequestArrayList;

    private Dialog messageRequestsDialog;
    private ImageView messageRequestClose;
    private RecyclerView messageRequestsRecyclerView;
    private MessageRequestsAdapter adapter;
    private DocumentReference ref;
    private User user;

    private BlogAdapter blogAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        ref = firestore.collection("Users").document(mUser.getUid());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists())
                    user= documentSnapshot.toObject(User.class);
            }
        });

        messageRequestArrayList = new ArrayList<>();

        toolbar = (Toolbar)findViewById(R.id.bar);
        relaNoti = (RelativeLayout)findViewById(R.id.toolbar_layout_relaNoti);
        textNotiNumber = (TextView)findViewById(R.id.toolbar_notiNumber);

        setSupportActionBar(toolbar);
        blogList = new ArrayList<>();
        usersFragment = new UsersFragment();
        profileFragment =new ProfileFragment();
        messageFragment = new MessageFragment();


        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        // Blog listesi ve adapter
        blogList = new ArrayList<>();
        blogAdapter = new BlogAdapter(blogList); // BlogAdapter başlatılıyor

        // RecyclerView ayarları
        RecyclerView recyclerView = findViewById(R.id.feed_activity); // XML'deki RecyclerView ID'si
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Dikey düzenleme
        recyclerView.setAdapter(blogAdapter); // Adapter'ı RecyclerView'a bağlama

        getData();

        query = firestore.collection("MessageRequests").document(mUser.getUid()).collection("Requests");
        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(FeedActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value!=null){
                    textNotiNumber.setText(String.valueOf(value.getDocuments().size()));
                    messageRequestArrayList.clear();

                    for (DocumentSnapshot snapshot:value.getDocuments()){
                        mMessageRequest = snapshot.toObject(MessageRequest.class);
                        messageRequestArrayList.add(mMessageRequest);
                    }

                }
            }
        });
        //bildirime tıklanınca
        relaNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageRequestsDialog();
            }
        });


        //menu1 için
        bottomView = (BottomNavigationView)findViewById(R.id.feed_activity_bottomView);
        bottomView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.people) {
                    relaNoti.setVisibility(View.INVISIBLE);
                    setFragment(usersFragment);
                    return true;
                } else if (item.getItemId() == R.id.message) {
                    relaNoti.setVisibility(View.VISIBLE);
                    setFragment(messageFragment);
                    return true;
                } else if (item.getItemId() == R.id.profile) {
                    relaNoti.setVisibility(View.INVISIBLE);
                    setFragment(profileFragment);
                    return true;
                } else {
                    return false;
                }

            }
        });
    }

    private void getData() {
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (value != null) {
                    blogList.clear(); // Listeyi temizleyerek güncel verilerle doldur
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Map<String, Object> data = document.getData();
                        String title = (String) data.get("title"); // Başlık
                        String description = (String) data.get("description"); // Açıklama
                        String imageUrl = (String) data.get("imageUrl"); // Fotoğraf URL'si
                        String userEmail = (String) data.get("userEmail"); // Kullanıcı email adresi

                        // Zaman damgasını insan tarafından okunabilir formata çevir
                        Object timestampObj = data.get("timestamp");
                        String timestampFormatted = "";
                        if (timestampObj instanceof com.google.firebase.Timestamp) {
                            com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) timestampObj;
                            Date date = timestamp.toDate();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
                            timestampFormatted = dateFormat.format(date);
                        }

                        // Blog nesnesine ekle
                        Blog blog = new Blog(userEmail, title, description, imageUrl, timestampFormatted);
                        blogList.add(blog);
                    }

                    // RecyclerView veya ListView adaptörüne değişiklikleri bildirin (örneğin notifyDataSetChanged)
                    blogAdapter.notifyDataSetChanged();
                }
            }
        });
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.add_blog){
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);

        } else if (item.getItemId()==R.id.logout) {
            auth.signOut();
            Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();


        }


        return super.onOptionsItemSelected(item);
    }

    private void setFragment(Fragment fragment){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.feed_activity,fragment);
        transaction.commit();
    }

    private void messageRequestsDialog(){
         messageRequestsDialog = new Dialog(this,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
         messageRequestsDialog.setContentView(R.layout.custom_dialog_incoming_message_requests);

         messageRequestClose = messageRequestsDialog.findViewById(R.id.custom_dialog_incoming_message_requests_imgClose);
         messageRequestsRecyclerView = messageRequestsDialog.findViewById(R.id.custom_dialog_incoming_message_requests_recyclerView);

         messageRequestClose.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 messageRequestsDialog.dismiss();
             }
         });

        messageRequestsRecyclerView.setHasFixedSize(true);
        messageRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        if (messageRequestArrayList.size()>0){
            adapter= new MessageRequestsAdapter(messageRequestArrayList,this,user.getUserId(),user.getUserName(),user.getUserProfile());
            messageRequestsRecyclerView.setAdapter(adapter);
        }
        messageRequestsDialog.show();
    }
}