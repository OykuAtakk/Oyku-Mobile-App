package com.oykuatak.oyku.Fragment;

import android.app.Activity;
import android.app.DownloadManager;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.oykuatak.oyku.Adapter.UserAdapter;
import com.oykuatak.oyku.Model.User;
import com.oykuatak.oyku.R;

import java.util.ArrayList;


public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private View v;
    private UserAdapter adapter;
    private ArrayList<User> userList;
    private User muser;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private Query query;
    private DocumentReference mRef;
    private User mKullanici;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_users, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        userList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.users_fragment_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));

        mRef = firestore.collection("Users").document(user.getUid());
        mRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                mKullanici = documentSnapshot.toObject(User.class);

                query = firestore.collection("Users");
                query.addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        userList.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            muser = snapshot.toObject(User.class);
                            if (muser != null && !muser.getUserId().equals(user.getUid())) {
                                userList.add(muser);
                            }
                        }

                        if (adapter == null) {
                            adapter = new UserAdapter(userList, v.getContext(), mKullanici.getUserId(), mKullanici.getUserName(), mKullanici.getUserProfile());
                            recyclerView.setAdapter(adapter);
                            recyclerView.addItemDecoration(new LinearDecoration(15, userList.size()));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        return v;
    }

    class LinearDecoration extends RecyclerView.ItemDecoration{
        private int space;
        private int data;

        public LinearDecoration(int space, int data) {
            this.space = space;
            this.data = data;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);

            if(pos!= (data -1))
                outRect.bottom = space;
        }
    }


}