package com.example.adminapp_iitj.faculty;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AddTeacher extends AppCompatActivity {
    private ImageView addteacherImg;
    private Bitmap bitmap;
    private  String name,email,post,downloadUrl;
    private StorageReference storageReference;
    private DatabaseReference reference,dbref ;
    public static final int REQUEST_PICK_IMAGE = 1001;
    private EditText addteachername,addteacheremail,addteacherpost;
    private Spinner addteacherBranch;

    private Button addTeacherbtn;
    private String teacherBranch;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        addteacherImg=findViewById(R.id.addTecaherImage);
        addteachername=findViewById(R.id.addTecahername);
        addteacheremail=findViewById(R.id.teacherEmail);
        addteacherpost=findViewById(R.id.teacherpost);
        addTeacherbtn=findViewById(R.id.addTeacherbtn);
        addteacherBranch=findViewById(R.id.addTecaherCategory);
        reference= FirebaseDatabase.getInstance().getReference().child("Teacher's");
        storageReference= FirebaseStorage.getInstance().getReference();
        pd=new ProgressDialog(this);

        String[] items=new String[]{"Select Branch","Computer Science","Electrical Engineering","Mechanical Engineering","Chemical Engineering",
        "Civil and Infrastructure Engineering","Materials Engineering","Physics","Chemistry","BioSciences Engineering","Other"};
        addteacherBranch.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));


        addteacherBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teacherBranch=addteacherBranch.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addteacherImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        addTeacherbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=addteachername.getText().toString();
                email=addteacheremail.getText().toString();
                post=addteacherpost.getText().toString();

                if(name.isEmpty())
                {
                    addteachername.setError("Empty!");
                    addteachername.requestFocus();
                }else if(email.isEmpty())
                {
                    addteacheremail.setError("Empty");
                    addteacheremail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    addteacheremail.setError("Enter valid email");
                    addteacheremail.requestFocus();
                }else if(post.isEmpty())
                {
                    addteacherpost.setError("Empty!");
                    addteacherpost.requestFocus();
                }else if(Objects.equals(teacherBranch, "Select Branch"))
                {
                    Toast.makeText(AddTeacher.this, "Select teacher Branch", Toast.LENGTH_SHORT).show();
                }else if(bitmap==null){
                    Toast.makeText(AddTeacher.this, "Choose Teacher Image!", Toast.LENGTH_SHORT).show();
                }else
                {
                    insertPhoto();
                }
            }
        });

    }

    private void insertPhoto() {
        pd.setMessage("Adding...");
        pd.show();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte [] finalimg=baos.toByteArray();
        final StorageReference filePath;
        filePath=storageReference.child("Teacher's").child(finalimg + "jpg");
        final UploadTask uploadTask=filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(AddTeacher.this,new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                    Toast.makeText(AddTeacher.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadData() {
        dbref = reference.child(teacherBranch);
        final String uniqueKey=dbref.push().getKey();
        TeacherData teacherData=new TeacherData(name,email,post,downloadUrl,uniqueKey);

        dbref.child(uniqueKey).setValue(teacherData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this, "Teacher added Successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
            addteacherImg.setImageBitmap(bitmap);
        }

    }
}