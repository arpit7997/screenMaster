package com.codingblocks.screenshot;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {
    public static final String ANONYMOUS = "anonymous";
    final static int REQ_CODE = 123;
    public static final int RC_SIGN_IN = 123;
    private static final int RC_PHOTO_PICKER = 2;
    public int write_extern;
    final static String TAG = "SCREENSHOTTT";
    public String User_uid, User_name;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;

    private String mUserName;
    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ChildEventListener mChildEventListner;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoStorageReference;

    ImageView personalImage;
    TextView personalName, text;
    Button btn;

    private ProgressDialog progress;

    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        personalImage = (ImageView) findViewById(R.id.personalImage);
        personalName = (TextView) findViewById(R.id.personalName);
        btn = (Button) findViewById(R.id.btn);
        text = (TextView) findViewById(R.id.text);

        mMessageListView = (ListView) findViewById(R.id.messageListView);

        mUserName = ANONYMOUS;
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();


        final ArrayList<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        mMessageAdapter.setClickListener(this);

        write_extern = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (write_extern != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE);
        }

        mMessageListView.setVisibility(View.GONE);
        text.setVisibility(View.GONE);

        findViewById(R.id.ripple).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (i == 0) {
                    mMessageListView.setVisibility(View.GONE);
                    text.setVisibility(View.GONE);
                    i = 1;
                    btn.setText("View All Profile");
                } else {
                    mMessageListView.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    i = 0;
                    btn.setText("Hide All Profile");
                }
            }
        });
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                //if user is signed in
                if (firebaseUser != null) {

                    User_uid = firebaseUser.getUid();
                    User_name = firebaseUser.getDisplayName();
                    mDatabaseReference = mFirebaseDatabase.getReference();

                    mDatabaseReference.child(User_uid).child("name").setValue(User_name);
                    mDatabaseReference.child(User_uid).child("uid").setValue(User_uid);

                    DatabaseReference ref = mDatabaseReference.child(User_uid).child("photoUrl");

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = dataSnapshot.getValue(String.class);
                            if (s != null) {
                                Glide.with(personalImage.getContext()).load(s).into(personalImage);
                            } else {
                                personalImage.setImageResource(R.drawable.def);
                                FriendlyMessage friendlyMessage = new FriendlyMessage(mUserName, null, User_uid);
                                //mDatabaseReference.child(User_uid).setValue(friendlyMessages);
                                mDatabaseReference.child(User_uid).child("photoUrl").setValue(null);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //setting name
                    mUserName = User_name;
                    mPhotoStorageReference = mFirebaseStorage.getReference().child(User_uid);

                    Signed_in();

                    personalName.setText(User_name);

                } else {
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).
                            setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                            .build(), 123);
                }

            }

        };

    }

    public void Signed_in() {
        if (mChildEventListner == null) {
            mChildEventListner = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (!(dataSnapshot.child("uid").getValue().toString().equals(User_uid))) {
                        FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                        mMessageAdapter.add(friendlyMessage);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListner);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.sign_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(MainActivity.this, SplashScreen.class));
                            finish();
                        }
                    });
        } else if (id == R.id.notification) {
            startActivity(new Intent(this, NotificationActivity.class));
        } else if (id == R.id.uploadImage) {
            if (write_extern == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);

            } else {
                Log.d("SCREENSHOTTT", "ask for per " + write_extern);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d("SCREENSHOTTT", "screen Shot per");
        if (requestCode == REQ_CODE) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission not given", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Log.d(TAG, "onRequestPermission");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Sign in Successful! ", Toast.LENGTH_SHORT).show();
            } else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in Cancelled ", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            progress=new ProgressDialog(this);
            progress.setMessage("Downloading image, please wait....");
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
            Uri selectedImageUri = data.getData();
            StorageReference photoRef = mPhotoStorageReference.child(selectedImageUri.getLastPathSegment());

            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mDatabaseReference.child(User_uid).child("photoUrl").setValue(downloadUri.toString());

                    Glide.with(personalImage.getContext()).load(downloadUri.toString()).into(personalImage);

                    FriendlyMessage friendlyMessage = new FriendlyMessage(mUserName, downloadUri.toString(), User_uid);
//                    mDatabaseReference.child(User_uid).setValue(friendlyMessage);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    }, 1500);

                }
            });


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListner);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            Log.d(TAG, "connected");
        } else {
            Log.d(TAG, "Disconnected");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListner != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListner);
        }
        if (mChildEventListner != null) {
            mDatabaseReference.removeEventListener(mChildEventListner);
            mChildEventListner = null;
        }
        mMessageAdapter.clear();
    }

    @Override
    public void onItemClicked(Bundle bundle) {

        FriendlyMessage mess = bundle.getParcelable("data");
        String userID = mess.getUid();

        Intent intent = new Intent(this, OnClickActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }
}
