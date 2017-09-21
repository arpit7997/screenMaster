package com.codingblocks.screenshot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = (RecyclerView) findViewById(R.id.notification_recycler);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(userID).child("screenshot");

        adapter = new NotificationAdapter(String.class,R.layout.notification_row,NotificationViewHolder.class,databaseReference);

        recyclerView.setAdapter(adapter);

    }
}
