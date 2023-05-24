package com.example.adminapp_iitj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class UploadImage extends AppCompatActivity {
    private Spinner imageCategory;
    private Button  uploadImagebtn;
    public static final int REQUEST_PICK_IMAGE = 1001;
    private CardView addImage;
    String downloadUrl;
    private Bitmap bitmap;
    private ImageView galleryImg;
    private String category;
    private StorageReference storageReference;
    private DatabaseReference reference;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        getSupportActionBar().setTitle("Uplaod Image");

        imageCategory=findViewById(R.id.image_category);
        uploadImagebtn=findViewById(R.id.uploadImagebtn);
        addImage=findViewById(R.id.addImage);
        galleryImg=findViewById(R.id.galleryImageView);
        pd=new ProgressDialog(this);

        storageReference=FirebaseStorage.getInstance().getReference().child("Gallery");
        reference= FirebaseDatabase.getInstance().getReference().child("Gallery");

        String[] items=new String[]{"Select Category","Convocation","Independece Day","Cultural Events","Sport's Events",
        "Republic Day","Technical Events","Other's Events"};
        imageCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));

        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=imageCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uploadImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap==null)
                {
                    Toast.makeText(UploadImage.this, "First choose Image!", Toast.LENGTH_SHORT).show();
                }else if(Objects.equals(category, "Select Category"))
                {
                    Toast.makeText(UploadImage.this, "Choose Category!", Toast.LENGTH_SHORT).show();
                }else if(category.isEmpty())
                {
                    Toast.makeText(UploadImage.this, "You have not choose category yet!", Toast.LENGTH_SHORT).show();
                }
                else {
                    pd.setMessage("Uploading...");
                    pd.show();
                    uploadImage();
                }
            }
        });

    }

    private void uploadImage() {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte [] finalimg=baos.toByteArray();
        final StorageReference filePath;
        filePath=storageReference.child(finalimg + "jpg");
        final UploadTask uploadTask=filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(UploadImage.this,new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);
                                    uploadData();
                                }
                            });
                        }
                    });
                }else {
                    pd.dismiss();
                    Toast.makeText(UploadImage.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadData() {
        DatabaseReference newReference = reference.child(category);

        final String uniqueKey = newReference.getKey();

        newReference.child(uniqueKey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(UploadImage.this, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadImage.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            // Do something with the selected image URI, such as displaying it in an ImageView
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            galleryImg.setImageBitmap(bitmap);
        }

    }
}