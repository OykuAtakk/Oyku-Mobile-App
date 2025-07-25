package com.oykuatak.oyku.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.oykuatak.oyku.Adapter.MessageRequestsAdapter;
import com.oykuatak.oyku.Fragment.FeedFragment;
import com.oykuatak.oyku.Fragment.MessageFragment;
import com.oykuatak.oyku.Fragment.ProfileFragment;
import com.oykuatak.oyku.Fragment.UsersFragment;

import com.oykuatak.oyku.Model.MessageRequest;
import com.oykuatak.oyku.Model.User;
import com.oykuatak.oyku.R;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {

    private BottomNavigationView bottomView;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Query query;
    private FirebaseUser mUser;
    private Dialog messageRequestsDialog;
    private RecyclerView messageRequestsRecyclerView;
    private MessageRequestsAdapter adapter;
    private ArrayList<MessageRequest> messageRequestArrayList;
    private User user; // Kullanıcı bilgilerini tutar
    private View messageRequestClose;
    private RelativeLayout relaNoti;
    private TextView textNotiNumber;
    private MessageRequest mMessageRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Firebase başlat
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mUser = auth.getCurrentUser();

        if (mUser != null) {
            // Firestore'dan kullanıcı bilgilerini al
            firestore.collection("Users")
                    .document(mUser.getUid()) // Kullanıcının UID'sini kullanarak dökümanı seç
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Document'ı User nesnesine dönüştür
                            this.user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                Log.d("UserInfo", "Kullanıcı bilgileri alındı: " + user.getUserName());
                            }
                        } else {
                            Log.d("UserInfo", "Kullanıcı bilgileri Firestore'da bulunamadı.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Kullanıcı bilgileri alınırken hata oluştu: " + e.getMessage());
                    });
        } else {
            Log.e("AuthError", "Kullanıcı oturumu bulunamadı.");
        }

        // Toolbar başlat
        Toolbar toolbar = findViewById(R.id.bar);
        setSupportActionBar(toolbar);

        // Bildirim bileşenlerini başlat
        relaNoti = findViewById(R.id.toolbar_layout_relaNoti);
        textNotiNumber = findViewById(R.id.toolbar_notiNumber);

        // Bildirim için Firestore sorgusu
        query = firestore.collection("MessageRequests").document(mUser.getUid()).collection("Requests");
        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(FeedActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value != null) {
                    textNotiNumber.setText(String.valueOf(value.getDocuments().size()));
                    messageRequestArrayList.clear();

                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        mMessageRequest = snapshot.toObject(MessageRequest.class);
                        messageRequestArrayList.add(mMessageRequest);
                    }
                }
            }
        });

        // Bildirim butonuna tıklama işlevi
        relaNoti.setVisibility(View.VISIBLE); // Görünür hale getir
        relaNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageRequestsDialog(); // Tıklandığında dialogu göster
            }
        });

        // Varsayılan olarak FeedFragment'i başlat
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new FeedFragment())
                .commit();

        // Bottom Navigation View başlat
        bottomView = findViewById(R.id.feed_activity_bottomView);
        bottomView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.people) {
                    selectedFragment = new UsersFragment();
                } else if (item.getItemId() == R.id.message) {
                    selectedFragment = new MessageFragment();
                } else if (item.getItemId() == R.id.profile) {
                    selectedFragment = new ProfileFragment();
                } else if (item.getItemId() == R.id.feed) {
                    selectedFragment = new FeedFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            }
        });

        // Örnek olarak boş bir mesaj istek listesi oluştur
        messageRequestArrayList = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_blog) {
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        } else if (item.getItemId() == R.id.logout) {
            auth.signOut();
            Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void messageRequestsDialog() {
        // Dialog'u başlat
        messageRequestsDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        messageRequestsDialog.setContentView(R.layout.custom_dialog_incoming_message_requests);

        // Dialog bileşenlerini tanımla
        messageRequestClose = messageRequestsDialog.findViewById(R.id.custom_dialog_incoming_message_requests_imgClose);
        messageRequestsRecyclerView = messageRequestsDialog.findViewById(R.id.custom_dialog_incoming_message_requests_recyclerView);

        // Kapatma butonuna tıklama işlevi
        messageRequestClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageRequestsDialog.dismiss();
            }
        });

        // RecyclerView ayarları
        messageRequestsRecyclerView.setHasFixedSize(true);
        messageRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Adapter'ı ayarla (messageRequestArrayList daha önce doldurulmuş olmalı)
        if (messageRequestArrayList.size() > 0 && user != null) {/////
            adapter = new MessageRequestsAdapter(messageRequestArrayList, this, user.getUserId(), user.getUserName(), user.getUserProfile());
            messageRequestsRecyclerView.setAdapter(adapter);
        }

        messageRequestsDialog.show();
    }
}
