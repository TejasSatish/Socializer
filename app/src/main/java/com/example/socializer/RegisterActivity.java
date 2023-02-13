package com.example.socializer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextView alreadyHaveAccount;
    EditText uName,uEmail,uPassword,uConfirmPassword;
    Button register;
    String emailValidateString="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        alreadyHaveAccount=findViewById(R.id.alreadyHaveAccount);

        uName=findViewById(R.id.name);
        uEmail=findViewById(R.id.email);
        uPassword=findViewById(R.id.password);
        uConfirmPassword=findViewById(R.id.confirmPassword);
        register=findViewById(R.id.registerButton);
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();


        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAuth();
            }
        });
    }

    private void performAuth() {
        String name=uName.getText().toString();
        String email=uEmail.getText().toString();
        String password=uPassword.getText().toString();
        String confirmPassword=uConfirmPassword.getText().toString();

        if(!email.matches(emailValidateString)){
            uEmail.setError("Enter a valid email address");
            uEmail.requestFocus();
        }else if(password.isEmpty() || password.length()<8){
            uPassword.setError("Enter a valid password of atl east 8 characters");
            uPassword.requestFocus();
        }else if(!password.equals(confirmPassword)){
            uConfirmPassword.setError("Passwords do not match");
            uConfirmPassword.requestFocus();
        }else{
            progressDialog.setMessage("Please wait while we register you");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String uid=firebaseUser.getUid();

                        firebaseDatabase =FirebaseDatabase.getInstance();
                        databaseReference = firebaseDatabase.getReferenceFromUrl("https://socializer-820d9-default-rtdb.asia-southeast1.firebasedatabase.app");

                        UserModel userModel= new UserModel(uid,name,email,password);
                        databaseReference.child("Users").child(uid).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    sendToHome();
                                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void sendToHome() {
        Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();
    }
}