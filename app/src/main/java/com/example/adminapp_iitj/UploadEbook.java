package com.example.adminapp_iitj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadEbook extends AppCompatActivity {
    private CardView choosepdf;
    private Button uplaodpdfbtn;
    private Uri pdfData;
    private EditText pdfTitle;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private ProgressDialog pd;
    private String pdfName,title;
    public static final int REQ = 1001;
    private TextView pdfTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ebook);
        getSupportActionBar().setTitle("Uplaod Ebook");

        choosepdf=findViewById(R.id.addpdf);
        pdfTitle=findViewById(R.id.pdfTitle);
        uplaodpdfbtn=findViewById(R.id.uploadpdfbtn);
        pdfTextView=findViewById(R.id.pdfTextView);

        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        pd=new ProgressDialog(this);

        choosepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uplaodpdfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=pdfTitle.getText().toString();

                if(title.isEmpty())
                {
                    pdfTitle.setError("Empty!");
                    pdfTitle.requestFocus();
                }else if(pdfData==null)
                {
                    Toast.makeText(UploadEbook.this, "Choose Pdf file", Toast.LENGTH_SHORT).show();
                }else
                {
                    uploadPdf();
                }
            }
        });
    }

    private void uploadPdf() {
        pd.setTitle("Please wait...");
        pd.setMessage("Uplaoding pdf");
        pd.show();
        StorageReference reference=storageReference.child("pdf/"+ pdfName +"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();

                while(!uriTask.isComplete());
                Uri uri=uriTask.getResult();
                uploadData(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadEbook.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String downloadUrl) {
        String uniqueKey=databaseReference.child("pdf").push().getKey();
        HashMap data=new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfUrl",downloadUrl);

        databaseReference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadEbook.this, "Pdf uploaded successfully!", Toast.LENGTH_SHORT).show();
                pdfTitle.setText(title);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadEbook.this, "Failed to upload pdf!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void openGallery() {
         Intent intent=new Intent();
         intent.setType("docs/pdf/ppt");
         intent.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(Intent.createChooser(intent,"Select Pdf File"),REQ);

    }
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK ) {
            pdfData = data.getData();
            if(pdfData.toString().startsWith("content://"))
            {
                Cursor cursor=null;
                try {
                    cursor=UploadEbook.this.getContentResolver().query(pdfData,null,null,null,null);

                    if(cursor!=null && cursor.moveToFirst())
                    {
                        pdfName=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(pdfData.toString().startsWith("file://"))
            {
                pdfName=new File(pdfData.toString()).getName();

            }
            pdfTextView.setText(pdfName);

        }

    }
}