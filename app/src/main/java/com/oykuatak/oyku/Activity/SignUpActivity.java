package com.oykuatak.oyku.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oykuatak.oyku.Model.User;
import com.oykuatak.oyku.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    FirebaseUser fUser;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public void signUpClicked(View view) {
        String name = binding.nameText2.getText().toString();
        String email = binding.emailText2.getText().toString();
        String password = binding.passwordText2.getText().toString();

        // Validate input fields
        if (email.equals("") || password.equals("") || name.equals("")) {
            Toast.makeText(this, "Enter name, email, and password", Toast.LENGTH_LONG).show();
        } else {
            // Use createUserWithEmailAndPassword for registration
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    // Get the currently registered user
                    fUser = auth.getCurrentUser();
                    if (fUser != null) {
                        // Create a User object to store in Firestore
                        user = new User(name, email, fUser.getUid(), "default");

                        // Store the user data in the Firestore "users" collection
                        firestore.collection("users").document(fUser.getUid())
                                .set(user).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Notify user of successful registration
                                            Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                                            // Redirect to FeedActivity
                                            Intent intent = new Intent(SignUpActivity.this, FeedActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Notify user of error
                                            Toast.makeText(SignUpActivity.this, "Failed to save user data.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle registration failure
                    Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }





}