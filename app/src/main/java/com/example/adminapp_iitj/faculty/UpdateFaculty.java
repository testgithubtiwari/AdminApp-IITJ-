package com.example.adminapp_iitj.faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.adminapp_iitj.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateFaculty extends AppCompatActivity {

    FloatingActionButton fab;
    private RecyclerView csDepartment,eeDepartment,otherDepartment;
    private LinearLayout csNodata,eeNodata,otherNodata;
    private List<TeacherData> list1,list2,list3;

    private TeacherAdapter adapter;
    private ProgressDialog pd;
    private DatabaseReference reference,dbref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);

        getSupportActionBar().setTitle("Uplaod Faculty");

        fab=findViewById(R.id.fab);
        pd=new ProgressDialog(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateFaculty.this,AddTeacher.class);
                startActivity(intent);
            }
        });
        csDepartment=findViewById(R.id.csDepartment);
        csNodata=findViewById(R.id.csNoData);

        eeDepartment=findViewById(R.id.eeDepartment);
        eeNodata=findViewById(R.id.eeNoData);

        otherDepartment=findViewById(R.id.otherDepartment);
        otherNodata=findViewById(R.id.otherNoData);

        reference= FirebaseDatabase.getInstance().getReference().child("Teacher's");

        csDepartment();
        eeDepartment();
        otherDepartment();

    }

    private void otherDepartment() {
        pd.setMessage("Loading..");
        pd.show();
        dbref=reference.child("Other Branch");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list3=new ArrayList<>();
                if(!dataSnapshot.exists())
                {
                    pd.dismiss();
                    otherNodata.setVisibility(View.VISIBLE);
                    otherDepartment.setVisibility(View.GONE);
                }else
                {
                    otherNodata.setVisibility(View.GONE);
                    otherDepartment.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        TeacherData data=snapshot.getValue(TeacherData.class);
                        list3.add(data);
                    }
                    otherDepartment.setHasFixedSize(true);
                    otherDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapter=new TeacherAdapter(list3,UpdateFaculty.this,"Other Branch");
                    otherDepartment.setAdapter(adapter);
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eeDepartment() {
        dbref=reference.child("Electrical Engineering");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list2=new ArrayList<>();
                if(!dataSnapshot.exists())
                {
                    eeNodata.setVisibility(View.VISIBLE);
                    eeDepartment.setVisibility(View.GONE);
                }else
                {
                    eeNodata.setVisibility(View.GONE);
                    eeDepartment.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        TeacherData data=snapshot.getValue(TeacherData.class);
                        list2.add(data);
                    }
                    eeDepartment.setHasFixedSize(true);
                    eeDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapter=new TeacherAdapter(list2,UpdateFaculty.this,"Electrical Engineering");
                    eeDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void csDepartment() {
        dbref=reference.child("Computer Science");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list1=new ArrayList<>();
                if(!dataSnapshot.exists())
                {
                    csNodata.setVisibility(View.VISIBLE);
                    csDepartment.setVisibility(View.GONE);
                }else
                {
                    csNodata.setVisibility(View.GONE);
                    csDepartment.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        TeacherData data=snapshot.getValue(TeacherData.class);
                        list1.add(data);
                    }
                    csDepartment.setHasFixedSize(true);
                    csDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapter=new TeacherAdapter(list1,UpdateFaculty.this,"Computer Science");
                    csDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}