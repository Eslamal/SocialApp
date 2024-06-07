package com.example.socialapp.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.adapter.UserAdapter;
import com.example.socialapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    UserAdapter adapter;
    CircleImageView img_setting;
    ImageView chatBtn, groupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersArrayList = new ArrayList<>();
         chatBtn=findViewById(R.id.chatBtn);
        groupBtn = findViewById(R.id.groupBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ChatActivity.class));
            }
        });
        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Public channel", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeActivity.this, GroupChatActivity.class));
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.primary_purple));
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference reference = database.getReference().child("user").child(currentUser.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String email = snapshot.child("email").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);
                        String status = snapshot.child("status").getValue(String.class);
                        String image = snapshot.child("imageUri").getValue(String.class);
                        if (image != null) {
                            Picasso.get().load(image).placeholder(R.drawable.placeholder_profile_image).into(img_setting);
                        }
                    } else {
                        // Handle the case where user data does not exist
                        Toast.makeText(HomeActivity.this, "User data does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    Toast.makeText(HomeActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case where the current user is null
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish(); // Optionally finish the current activity
        }

        RecyclerView mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(HomeActivity.this, usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);
        img_setting = findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // If the current user is null, redirect to login activity
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish(); // Optionally finish the current activity
        }
    }
}
