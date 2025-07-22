package com.oykuatak.oyku.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oykuatak.oyku.databinding.ActivityUploadBinding;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private Uri imageData;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityUploadBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();
    }

    public void selectImage(View view) {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33 ve üzeri için READ_MEDIA_IMAGES
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            // API 32 ve öncesi için READ_EXTERNAL_STORAGE
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // İzin daha önce reddedildiyse açıklama göster
                Snackbar.make(view, "Galeriye erişim izni gerekiyor.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin Ver", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                permissionLauncher.launch(permission);
                            }
                        }).show();
            } else {
                // İzin daha önce hiç istenmediyse veya "Bir Daha Sorma" seçilmişse
                permissionLauncher.launch(permission);
            }
        } else {
            // İzin zaten verilmişse galeriyi aç
            openGallery();
        }
    }

    private void openGallery() {
        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intentToGallery);
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intentFromResult = result.getData();
                            if (intentFromResult != null) {
                                imageData = intentFromResult.getData();
                                binding.imageView.setImageURI(imageData);
                            }
                        }
                    }
                });

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean isGranted) {
                        if (isGranted) {
                            // İzin verildiyse galeriyi aç
                            openGallery();
                        } else {
                            // İzin verilmediyse uyarı göster
                            Toast.makeText(UploadActivity.this, "İzin versene lo!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void uploadButtonClicked(View view) {
        if (imageData != null) {
            // Benzersiz bir dosya adı oluştur
            UUID uuid = UUID.randomUUID();
            String imageName = "images/" + uuid + ".jpg";

            // Firebase Storage'a yükleme işlemi
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(imageName);
            storageReference.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Fotoğrafın indirme URL'sini al
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            String title = binding.editTextText.getText().toString(); // Başlık alanı
                            String description = binding.editTextTextMultiLine.getText().toString(); // Açıklama alanı

                            // Firebase Authentication'dan oturum açmış kullanıcıyı al
                            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                            // Firestore için verileri hazırlama
                            HashMap<String, Object> postData = new HashMap<>();
                            postData.put("imageUrl", downloadUrl);
                            postData.put("title", title);
                            postData.put("description", description);
                            postData.put("userEmail", userEmail); // Kullanıcı email adresi
                            postData.put("timestamp", FieldValue.serverTimestamp()); // Zaman damgası

                            // Firestore'a ekleme
                            FirebaseFirestore.getInstance().collection("Posts")
                                    .add(postData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(UploadActivity.this, "Yükleme başarılı!", Toast.LENGTH_SHORT).show();
                                            // Yükleme sonrası başka bir aktiviteye geçiş
                                            Intent intent = new Intent(UploadActivity.this, FeedActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Lütfen bir fotoğraf seçin.", Toast.LENGTH_SHORT).show();
        }
    }


}
