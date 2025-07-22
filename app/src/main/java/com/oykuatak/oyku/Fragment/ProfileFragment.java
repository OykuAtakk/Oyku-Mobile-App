package com.oykuatak.oyku.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.oykuatak.oyku.Model.User;
import com.oykuatak.oyku.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private EditText editName,editEmail;
    private CircleImageView imgProfile;
    private View v;

    private FirebaseFirestore firestore;
    private DocumentReference ref;
    private FirebaseUser mUser;
    private User user;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile,container,false);

        editName = v.findViewById(R.id.profile_fragment_editName);
        editEmail = v.findViewById(R.id.profile_fragment_editEmail);
        imgProfile = v.findViewById(R.id.profile_fragment_imgUserProfile);

        mUser= FirebaseAuth.getInstance().getCurrentUser();
        firestore =FirebaseFirestore.getInstance();

        ref = firestore.collection("Users").document(mUser.getUid());
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(v.getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value !=null && value.exists()){
                    user = value.toObject(User.class);

                    if (user!=null){
                        editName.setText(user.getUserName());
                        editEmail.setText(user.getUserEmail());

                        if (user.getUserProfile().equals("default"))
                            imgProfile.setImageResource(R.mipmap.ic_launcher);
                        else Picasso.get().load(user.getUserProfile()).resize(156,156).into(imgProfile);
                    }
                }
            }
        });
        return v;



    }
}