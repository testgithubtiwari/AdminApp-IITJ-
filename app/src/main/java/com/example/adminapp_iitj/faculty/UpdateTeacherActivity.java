package com.example.adminapp_iitj.faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.adminapp_iitj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UpdateTeacherActivity extends AppCompatActivity {

    private ImageView updateTeacherImg;
    private ProgressDialog pd;
    String uniqueKey,category;
    private Bitmap bitmap=null;
    private  String  newname,newemail,newpost;
    public static final int REQUEST_PICK_IMAGE = 1001;
    private EditText updateTeacherName,updateTeacherEmail,updateTeacherPost;
    private Button updateTeacher,deleteTeacher;
    private String name,email,post,image,downloadUrl="";
    private StorageReference storageReference;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);

        getSupportActionBar().setTitle("Update Faculty");

        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        post=getIntent().getStringExtra("post");
        image=getIntent().getStringExtra("image");

        updateTeacherImg=findViewById(R.id.updateTecherImage);
        updateTeacherName=findViewById(R.id.updateTeacherName);
        updateTeacherEmail=findViewById(R.id.updateTeacherEmail);
        updateTeacherPost=findViewById(R.id.updateTeacherPost);
        updateTeacher=findViewById(R.id.update);
        deleteTeacher=findViewById(R.id.delete);
        pd=new ProgressDialog(this);
        reference= FirebaseDatabase.getInstance().getReference().child("Teacher's");
        storageReference= FirebaseStorage.getInstance().getReference();

        uniqueKey=getIntent().getStringExtra("key");
        category=getIntent().getStringExtra("category");

        try {
            Picasso.get().load(image).into(updateTeacherImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateTeacherName.setText(name);
        updateTeacherEmail.setText(email);
        updateTeacherPost.setText(post);

        updateTeacherImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updateTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newname=updateTeacherName.getText().toString();
                newemail=updateTeacherEmail.getText().toString();
                newpost=updateTeacherPost.getText().toString();
                checkValidation();
            }
        });

        deleteTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

    }

    private void deleteData() {
        pd.setMessage("Deleting..");
        pd.show();
        reference.child(category).child(uniqueKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UpdateTeacherActivity.this, "Teacher deleted successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(UpdateTeacherActivity.this,UpdateFaculty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UpdateTeacherActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkValidation() {
        if(newname.isEmpty())
        {
            updateTeacherName.setError("Empty!");
            updateTeacherName.requestFocus();
        }else if(newemail.isEmpty())
        {
            updateTeacherEmail.setError("Empty!");
            updateTeacherEmail.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(newemail).matches())
        {
            updateTeacherEmail.setError("Enter valid Email");
            updateTeacherEmail.requestFocus();
        }else if(newpost.isEmpty())
        {
            updateTeacherPost.setError("Empty!");
            updateTeacherPost.requestFocus();
        }else if(bitmap==null)
        {
            updateData(image);
        }else
        {
            uploadImage();
        }
    }

    private void uploadImage() {
        pd.setMessage("Updating...");
        pd.show();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte [] finalimg=baos.toByteArray();
        final StorageReference filePath;
        filePath=storageReference.child("Teacher's").child(finalimg + "jpg");
        final UploadTask uploadTask=filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(UpdateTeacherActivity.this,new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    updateData(downloadUrl);
                                }
                            });
                        }
                    });
                }else {
                    pd.dismiss();
                    Toast.makeText(UpdateTeacherActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updateData(String s) {
        HashMap hp=new HashMap();
        hp.put("name",newname);
        hp.put("email",newemail);
        hp.put("post",newpost);
        hp.put("image",s);

        reference.child(category).child(uniqueKey).updateChildren(hp).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UpdateTeacherActivity.this, "Teacher updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(UpdateTeacherActivity.this,UpdateFaculty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
            updateTeacherImg.setImageBitmap(bitmap);
        }

    }
}