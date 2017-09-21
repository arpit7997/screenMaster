package com.codingblocks.screenshot;

import android.*;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OnClickActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private static final int ALL_PERMISSION_RESULT = 107;
    private static String nameOfPerson,nameOfUser;
    private static String userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click);

        imageView = (ImageView) findViewById(R.id.onclick_imaeview);
        textView = (TextView) findViewById(R.id.onclick_name);

        Bundle bundle = getIntent().getExtras();
        FriendlyMessage message = bundle.getParcelable("data");

        userUID = getIntent().getStringExtra("userID");

        if(message.getPhotoUrl() ==null){
            imageView.setImageResource(R.drawable.def);
        }else {

            Glide.with(imageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(imageView);
        }

        nameOfPerson = message.getName();
        textView.setText(nameOfPerson);

        getPermissions();

        isServiceRunning();

        startService();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        nameOfUser = user.getDisplayName();

    }
    @TargetApi(Build.VERSION_CODES.M)
    private void getPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        ALL_PERMISSION_RESULT);
                return;
            }
            else {
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case ALL_PERMISSION_RESULT:
            {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                }
                else {
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service != null) {
                if ((getPackageName() + "." + ScreenShotService.class.getSimpleName()).equals(service.service.getClassName())) {
                    return true;
                }
            }
        }

        return false;
    }
    private void startService()
    {
        if (isStoragePermissionGranted()) {
            startService(new Intent(this, ScreenShotService.class));
            isServiceRunning();
        } else {
            Log.e("TAG", "Permission Denial: requires android.permission.READ_EXTERNAL_STORAGE");
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private boolean isStoragePermissionGranted() {
        String storagePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        return ContextCompat.checkSelfPermission(this, storagePermission) == PackageManager.PERMISSION_GRANTED;
    }
    private void endService()
    {
        stopService(new Intent(this, ScreenShotService.class));
        isServiceRunning();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        endService();
    }
    public static void onScreenShotTaken(Context context)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(userUID).child("screenshot");
        ref.push().setValue(nameOfUser);
    }
}
