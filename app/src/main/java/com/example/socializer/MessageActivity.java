package com.example.socializer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.example.socializer.databinding.ActivityMessageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    String receiverId;
    DatabaseReference databaseReferenceSender,databaseReferenceReceiver;
    String senderRoom,receiverRoom;
    MessageAdapter messageAdapter;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            receiverId=extras.getString("id");
        }


        senderRoom=FirebaseAuth.getInstance().getUid()+receiverId;
        receiverRoom=receiverId+FirebaseAuth.getInstance().getUid();

        messageAdapter=new MessageAdapter(this);
        binding.recycler.setAdapter(messageAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        databaseReferenceSender= FirebaseDatabase.getInstance("https://socializer-820d9-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Chats").child(senderRoom);
        databaseReferenceReceiver= FirebaseDatabase.getInstance("https://socializer-820d9-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Chats").child(receiverRoom);

        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MessageModel messageModel;
                    messageModel=dataSnapshot.getValue(MessageModel.class);
                    messageAdapter.add(messageModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=binding.message.getText().toString();
                if(message.trim().length()>0){
                    sendMessage(message);
                }
            }
        });

    }

    private void sendMessage(String message){
        String messageId= String.valueOf(calendar.getInstance().getTimeInMillis());
        MessageModel messageModel= new MessageModel(messageId,FirebaseAuth.getInstance().getUid(),message);
        messageAdapter.add(messageModel);
        databaseReferenceSender.child(messageId).setValue(messageModel);
        databaseReferenceReceiver.child(messageId).setValue(messageModel);
    }
}