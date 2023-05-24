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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView register;
    private EditText email,password;
    private Button btnSign;
    private ProgressBar progressBar;
    private FirebaseAuth authprofile;
    private static final  String TAG="LoginActivity";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login Page");

        register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        email=findViewById(R.id.emailId);
        password=findViewById(R.id.emailPwd);
        btnSign=findViewById(R.id.btnsign);
        pd=new ProgressDialog(this);
        authprofile= FirebaseAuth.getInstance();


        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newemail=email.getText().toString();
                String newpassword=password.getText().toString();

                if(TextUtils.isEmpty(newemail))
                {
                    email.setError("Empty!");
                    email.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(newemail).matches())
                {
                    email.setError("Valid e-mail is required");
                    email.requestFocus();
                }else if(TextUtils.isEmpty(newpassword))
                {
                    password.setError("Empty!");
                    password.requestFocus();
                }else
                {
                    loginUser(newemail,newpassword);

                }
            }
        });
    }

    private void loginUser(String newemail, String newpassword) {
        pd.setMessage("Loading...");
        pd.show();
        authprofile.signInWithEmailAndPassword(newemail,newpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,"You have successfully Loged in",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else {
                    pd.dismiss();

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        email.setError("User doesn't exist or no longer valid. Please register again");
                        email.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        password.setError("Invalid credentials. kindly check and re-enter");
                        password.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
    }
}