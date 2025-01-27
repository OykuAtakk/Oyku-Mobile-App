package com.oykuatak.oyku.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.oykuatak.oyku.Adapter.BlogAdapter;
import com.oykuatak.oyku.Model.Blog;
import com.oykuatak.oyku.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private BlogAdapter blogAdapter;
    private ArrayList<Blog> blogList;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        blogList = new ArrayList<>();
        blogAdapter = new BlogAdapter(blogList);
        recyclerView.setAdapter(blogAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();

        return view;
    }

    private void getData() {
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (value != null) {
                    blogList.clear();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Map<String, Object> data = document.getData();
                        String title = (String) data.get("title");
                        String description = (String) data.get("description");
                        String imageUrl = (String) data.get("imageUrl");
                        String userEmail = (String) data.get("userEmail");

                        String timestampFormatted = "";
                        Object timestampObj = data.get("timestamp");
                        if (timestampObj instanceof com.google.firebase.Timestamp) {
                            com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) timestampObj;
                            Date date = timestamp.toDate();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
                            timestampFormatted = dateFormat.format(date);
                        }

                        Blog blog = new Blog(userEmail, title, description, imageUrl, timestampFormatted);
                        blogList.add(blog);
                    }
                    blogAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}