package com.example.socializer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    Button save;
    EditText name,phone;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseDatabase =FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        save=findViewById(R.id.save);
        String Uname=name.getText().toString();
        String Uphone=phone.getText().toString();

        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("name",Uname);
        hashMap.put("phone",Uphone);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Users")
                        .child("userDetails")
                        .setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ProfileActivity.this, "Data submitted", Toast.LENGTH_SHORT).show();
                                goToHome();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "An error occured with data submission", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void goToHome() {
        Intent intent=new Intent(ProfileActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();
    }
}