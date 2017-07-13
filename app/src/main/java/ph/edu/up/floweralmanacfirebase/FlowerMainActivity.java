package ph.edu.up.floweralmanacfirebase;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FlowerMainActivity extends AppCompatActivity {
    private ListView listView;
    private ListFlowerAdapter flowerAdapter;
    private List<Flower> flowerArrayList;

    private String mUsername;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mListener;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    public static final int RC_SIGN_IN = 1;
    public static final int VIEW = 2;
    public static final int ADD = 3;

    public static  boolean alreadyEnabled;
    public static String uniqueKey = "";

    public final static String NAMEFLOWER = "ph.edu.up.flowermainactivity.NAMEFLOWER";
    public final static String EASEFLOWER = "ph.edu.up.flowermainactivity.EASEFLOWER";
    public final static String INSTFLOWER = "ph.edu.up.flowermainactivity.INSTFLOWER";
    public final static String KEYFLOWER = "ph.edu.up.flowermainactivity.KEYFLOWER";
    public final static String URLFLOWER = "ph.edu.up.flowermainactivity.URLFLOWER";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView =(ListView) findViewById(R.id.layout_main);

        try {
            if (!alreadyEnabled) {
                //Enable offline access to database
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                alreadyEnabled = true;
            }
        } catch (Exception ex) {}

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mReference = mDatabase.getReference().child("flowers");

        //Keeps app synced
        mReference.keepSynced(true);
        mStorageReference = mStorage.getReference().child("flower_photos");

        /*showMessage();*/

        flowerArrayList = new ArrayList<>();
        flowerAdapter = new ListFlowerAdapter(this, R.layout.content_flower_main, flowerArrayList);
        listView.setAdapter(flowerAdapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Flower flower = (Flower) parent.getAdapter().getItem(position);
                String nameFlower = flower.getFlowerName();
                String easeFlower = flower.getEase();
                String instFlower = flower.getInstructions();
                String keyFlower = flower.getPostKey();
                String urlFlower = flower.getPhotoUrl();

                Intent intent = new Intent(FlowerMainActivity.this, ViewActivity.class);
                intent.putExtra(NAMEFLOWER, nameFlower);
                intent.putExtra(EASEFLOWER, easeFlower);
                intent.putExtra(INSTFLOWER, instFlower);
                intent.putExtra(KEYFLOWER, keyFlower);
                intent.putExtra(URLFLOWER, urlFlower);

                startActivityForResult(intent, VIEW);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FlowerMainActivity.this, AddActivity.class);
                startActivityForResult(intent, ADD);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignInInitilaize(user.getDisplayName());
                } else {
                    onSignOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }

        if (requestCode == VIEW && resultCode == Activity.RESULT_OK) {

            String status = intent.getStringExtra(ViewActivity.DEL);
            String saveName = intent.getStringExtra(ViewActivity.NAME);
            String saveEase = intent.getStringExtra(ViewActivity.EASE);
            String saveInst = intent.getStringExtra(ViewActivity.INST);
            String saveKey = intent.getStringExtra(ViewActivity.KEY);
            String saveUrl = intent.getStringExtra(ViewActivity.URL);

            if (status.equals("true")) {
                if (!saveUrl.equals("dummyData")) {
                    StorageReference photoRef = mStorage.getReferenceFromUrl(saveUrl);
                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("SENDER", "Photo deleted.");
                            }
                        });
                }
                    mReference.child(saveKey).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                        }
                    });
            } else if (status.equals("false")) {

                Intent intent1 = new Intent(FlowerMainActivity.this, AddActivity.class);
                intent1.putExtra(NAMEFLOWER, saveName);
                intent1.putExtra(EASEFLOWER, saveEase);
                intent1.putExtra(INSTFLOWER, saveInst);
                intent1.putExtra(KEYFLOWER, saveKey);
                intent1.putExtra(URLFLOWER, saveUrl);

                startActivityForResult(intent1, ADD);
            }
        }

        if (requestCode == ADD && resultCode == Activity.RESULT_OK) {

            final String path = intent.getStringExtra(AddActivity.PATH);
            final String flowerName = intent.getStringExtra(AddActivity.NAME);
            final String saveEase = intent.getStringExtra(AddActivity.EASE);
            final String saveInst = intent.getStringExtra(AddActivity.INST);
            final String saveKey = intent.getStringExtra(AddActivity.KEY);
            final String photoUrl = intent.getStringExtra(AddActivity.URL);
            final String delPhoto = intent.getStringExtra(AddActivity.DEL);

            if (!saveKey.isEmpty() && !saveKey.equals("")) { //Updating information

                if (!path.equals("") && !path.isEmpty()) { // Replacing old photo
                    if (photoUrl.equals("dummyData")) { // NO Photo previously uploaded
                        //Uploads new photo
                        Log.e("SENDER", "You got here!");

                        Uri imageUri = Uri.parse(path);
                        StorageReference imageRef = mStorageReference.child(imageUri.getLastPathSegment());
                        imageRef.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                                    Flower flower = new Flower(mUsername, flowerName, saveEase, saveInst, downloadUri.toString(), saveKey);
                                    mReference.child(saveKey).setValue(flower);
                                    Toast.makeText(getApplicationContext(), "Changes saved", Toast.LENGTH_LONG).show();
                                    Log.e("SENDER", "uploaded");
                                }
                            });
                    } else {
                        //Deletes old photo uploaded
                        StorageReference photoRef = mStorage.getReferenceFromUrl(photoUrl);
                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("SENDER", "Photo deleted.");
                                }
                            });
                        //Uploads new photo
                        Uri imageUri = Uri.parse(path);
                        StorageReference imageRef = mStorageReference.child(imageUri.getLastPathSegment());
                        imageRef.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                                    Flower flower = new Flower(mUsername, flowerName, saveEase, saveInst, downloadUri.toString(), saveKey);
                                    mReference.child(saveKey).setValue(flower);
                                    Toast.makeText(getApplicationContext(), "Changes saved", Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                } else { // Path is empty, NO photo selected

                    if (delPhoto.equals("delete") && !photoUrl.equals("dummyData")) { // User wants to remove photo previously uploaded
                        StorageReference photoRef = mStorage.getReferenceFromUrl(photoUrl);
                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("SENDER", "Photo deleted.");
                            }
                        });

                        Flower flower = new Flower(mUsername, flowerName, saveEase, saveInst, "dummyData", saveKey);
                        mReference.child(saveKey).setValue(flower, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Toast.makeText(getApplicationContext(), "Changes saved", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else { // User update only affects the text fields

                        Flower flower = new Flower(mUsername, flowerName, saveEase, saveInst, photoUrl, saveKey);
                        mReference.child(saveKey).setValue(flower, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Toast.makeText(getApplicationContext(), "Changes saved", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            } else {
                //Saving new flower

                if (!path.equals("") && !path.isEmpty()) { //Uploading a photo
                    Uri imageUri = Uri.parse(path);
                    StorageReference imageRef = mStorageReference.child(imageUri.getLastPathSegment());
                    imageRef.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                Flower flower = new Flower(mUsername, flowerName, saveEase, saveInst, downloadUri.toString(), "dummyData");
                                mReference.push().setValue(flower, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError error, DatabaseReference reference) {
                                        uniqueKey = reference.getKey();
                                        mReference.child(uniqueKey).child("postKey").setValue(uniqueKey);
                                    }
                                });
                            }
                        });
                } else { // No photo to be uploaded

                    Flower flower = new Flower(mUsername, flowerName, saveEase, saveInst, "dummyData", "dummyData");
                    mReference.push().setValue(flower, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError error, DatabaseReference reference) {
                                uniqueKey = reference.getKey();
                                mReference.child(uniqueKey).child("postKey").setValue(uniqueKey);
                            }
                        });
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle("Select Action");
        menu.add(0, view.getId(), 0, "Edit");
        menu.add(0, view.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Edit"){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Flower flower = (Flower) flowerAdapter.getItem(info.position);

            String nameFlower = flower.getFlowerName();
            String easeFlower = flower.getEase();
            String instFlower = flower.getInstructions();
            String keyFlower = flower.getPostKey();
            String urlFlower = flower.getPhotoUrl();

            Intent intent = new Intent(FlowerMainActivity.this, AddActivity.class);
            intent.putExtra(NAMEFLOWER, nameFlower);
            intent.putExtra(EASEFLOWER, easeFlower);
            intent.putExtra(INSTFLOWER, instFlower);
            intent.putExtra(KEYFLOWER, keyFlower);
            intent.putExtra(URLFLOWER, urlFlower);

            startActivityForResult(intent, ADD);

        } else if (item.getTitle() == "Delete") {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final Flower flower = (Flower) flowerAdapter.getItem(info.position);

            AlertDialog.Builder builder = new AlertDialog.Builder(FlowerMainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Delete");
            builder.setMessage(flower.getFlowerName() + " will be removed from your flower list. Continue?");
            builder.setPositiveButton("Delete",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!flower.getPhotoUrl().equals("dummyData")) {
                                StorageReference photoRef = mStorage.getReferenceFromUrl(flower.getPhotoUrl());
                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("SENDER", "Photo deleted.");
                                    }
                                });
                            }
                            mReference.child(flower.getPostKey()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else { return false; }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
        flowerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthListener);
        detachReadListener();
        flowerAdapter.clear();
    }

    private void onSignInInitilaize(String username) {
        mUsername = username;
        attachReadListener();

    }

    private void  onSignOutCleanup() {
        mUsername = "Anonymous";
        flowerAdapter.clear();
        detachReadListener();
    }

    private void attachReadListener() {
        if (mListener == null) {
            mListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Flower flower = dataSnapshot.getValue(Flower.class);
                    if (!flower.getPostKey().equals("dummyData")) {
                        flowerAdapter.add(flower);
                    } else {
                        mReference.child(flower.getPostKey()).removeValue();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey();
                    int position = 0;

                    Iterator<Flower> iterator = flowerArrayList.iterator();
                    while (iterator.hasNext()) {
                        Flower flower = iterator.next();
                        if (key.equals(flower.getPostKey())) {
                            position = flowerAdapter.getPosition(flower);
                            iterator.remove();
                        }
                    }
                    Flower flowerNew = dataSnapshot.getValue(Flower.class);
                    flowerAdapter.insert(flowerNew, position);
                    flowerAdapter.notifyDataSetChanged();
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();

                    Iterator<Flower> iterator = flowerArrayList.iterator();
                    while (iterator.hasNext()) {
                        Flower flower = iterator.next();
                        if (key.equals(flower.getPostKey())) {
                            iterator.remove();
                            flowerAdapter.notifyDataSetChanged();
                        }
                    }
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mReference.addChildEventListener(mListener);
        }
    }

    private void detachReadListener() {
        if (mListener != null) {
            mReference.removeEventListener(mListener);
            mListener = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
