package com.example.adminapp_iitj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText adminname,email,password,passwordagain;
    private Button signUp;
    private ProgressDialog pd;

    private static final  String TAG="RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Regsiter Page");

        adminname=findViewById(R.id.Name);
        email=findViewById(R.id.adminemailId);
        password=findViewById(R.id.adminpwd);
        passwordagain=findViewById(R.id.pwdagain);
        pd=new ProgressDialog(this);


        signUp=findViewById(R.id.btnsignup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=adminname.getText().toString();
                String newemail=email.getText().toString();
                String pwd=password.getText().toString();
                String pwdagain=passwordagain.getText().toString();


                if(TextUtils.isEmpty(name)){
                    adminname.setError("Empty!");
                    adminname.requestFocus();
                }else if(TextUtils.isEmpty(newemail))
                {
                    email.setError("Empty!");
                    email.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(newemail).matches())
                {
                    email.setError("Enter valid E-mail");
                    email.requestFocus();
                }else if(TextUtils.isEmpty(pwd)){
                    password.setError("Empty!");
                    password.requestFocus();
                }else if(pwd.length()<=6)
                {
                    password.setError("Password should be grater than 6 digits.");
                    password.requestFocus();
                }else if(!pwdagain.equals(pwd))
                {
                    passwordagain.setError("Password not matches");
                    passwordagain.requestFocus();
                }
                else {
                    registerUser(name,newemail,pwd);
                }
            }
        });
    }

    private void registerUser(String name, String newemail, String pwd) {
        pd.setTitle("Please wait");
        pd.setMessage("loading...");
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(newemail, pwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            // enter user data firebase in the real time
                            ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails(name);

                            /// extracting reference for databse registered users
                            DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {


                                        // open user homepage after registered succesfully
                                        pd.dismiss();
                                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);

                                        // upon registration user can't go to register page
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();// to close register activity
                                    }else {
                                        pd.dismiss();

                                        Toast.makeText(RegisterActivity.this,"Registration failed , try again",Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                        }
                        else {
                            pd.dismiss();
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthWeakPasswordException e){
                                password.setError("Your password is too weak. Kindly use numeric,alphabets and special characters");
                                password.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                email.setError("Your e-mail is invalid or already in use. Kindly re-enter");
                                email.requestFocus();

                            }catch(FirebaseAuthUserCollisionException e){
                                email.setError("User is already registered with this e-mail, use another");
                                email.requestFocus();
                            }

                            catch (Exception e) {
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
    }
}