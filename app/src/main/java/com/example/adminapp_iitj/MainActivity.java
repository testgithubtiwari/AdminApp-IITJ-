package com.example.adminapp_iitj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.adminapp_iitj.faculty.UpdateFaculty;
import com.example.adminapp_iitj.notice.DeleteNotice;
import com.example.adminapp_iitj.notice.UploadNotice;

public class MainActivity extends AppCompatActivity {
    private CardView addNotice,uploadImg,uploadEbook,addfaculty,deleteNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Main Page");

        addNotice=findViewById(R.id.addNotice);
        addNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, UploadNotice.class);
                startActivity(intent);
            }
        });
        uploadImg=findViewById(R.id.addGalleryImage);
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,UploadImage.class);
                startActivity(intent);
            }
        });

        uploadEbook=findViewById(R.id.addEbook);
        uploadEbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,UploadEbook.class);
                startActivity(intent);
            }
        });

        addfaculty=findViewById(R.id.addfaculty);
        addfaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, UpdateFaculty.class);
                startActivity(intent);
            }
        });
        deleteNotice=findViewById(R.id.deleteNotice);
        deleteNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, DeleteNotice.class);
                startActivity(intent);
            }
        });

    }
}