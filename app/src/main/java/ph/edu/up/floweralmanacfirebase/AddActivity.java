package ph.edu.up.floweralmanacfirebase;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by victo on 7/11/2017.
 */

public class AddActivity extends AppCompatActivity {

    public final static String NAME = "ph.edu.up.addactivity.NAME";
    public final static String EASE = "ph.edu.up.addactivity.EASE";
    public final static String INST = "ph.edu.up.addactivity.INST";
    public final static String KEY = "ph.edu.up.addactivity.KEY";
    public final static String URL = "ph.edu.up.addactivity.URL";
    public final static String PATH = "ph.edu.up.addactivity.PATH";
    public final static String DEL = "ph.edu.up.addactivity.DEL";

    public static String userChoice = "";
    public static Uri path = null;

    private static final int REQUEST_CAMERA = 5;
    private static final int SELECT_FILE = 6;
    public static final int INST_LENGTH_LIMIT = 325;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private TextView errorMessage;
    private EditText editText1;
    private ValueEventListener mListener;
    private TextWatcher textWatcher;

    public boolean exists;

    private Button button1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.findItem(R.id.save).setEnabled(!exists);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save:
                saveChanges();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        editText1.getBackground().clearColorFilter();
        errorMessage.setVisibility(View.INVISIBLE);
        if (mListener != null) {
            mReference.removeEventListener(mListener);
        }
        if (textWatcher != null) {
            editText1.removeTextChangedListener(textWatcher);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_flower);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.addPhoto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        //Makes sure that only on button click
        TextView deleteField = (TextView) findViewById(R.id.deletePhoto);
        deleteField.setText("");

        button1 = (Button) findViewById(R.id.clear_button);
        button1.setVisibility(View.INVISIBLE);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePhoto();
            }
        });

        path = null;

        exists = false;

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child("flowers");

        editText1 = (EditText) findViewById(R.id.editName);
        errorMessage = (TextView) findViewById(R.id.errorMessage);
        errorMessage.setText("");

        EditText editText2 = (EditText) findViewById(R.id.editInst);
        editText2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(INST_LENGTH_LIMIT)});

        Intent intent1 = getIntent();

        final String nameFlower = intent1.getStringExtra(FlowerMainActivity.NAMEFLOWER);
        String easeFlower = intent1.getStringExtra(FlowerMainActivity.EASEFLOWER);
        String instFlower = intent1.getStringExtra(FlowerMainActivity.INSTFLOWER);
        String keyFlower = intent1.getStringExtra(FlowerMainActivity.KEYFLOWER);
        String urlFlower = intent1.getStringExtra(FlowerMainActivity.URLFLOWER);

        if (nameFlower != null && !nameFlower.isEmpty()) {

            editText1.setText(nameFlower);
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    final String flowerName = charSequence.toString().trim();
                    mListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                String name = data.child("flowerName").getValue().toString().toLowerCase();
                                if (name.equals(flowerName.toLowerCase()) && !name.equals(nameFlower.toLowerCase())) {
                                    editText1.getBackground().setColorFilter(Color.parseColor("#FF1744"), PorterDuff.Mode.SRC_IN);
                                    errorMessage.setText(flowerName + " already exists");
                                    exists = true;
                                    break;
                                } else {
                                    editText1.getBackground().clearColorFilter();
                                    errorMessage.setText("");
                                    exists = false;
                                }
                            }
                            invalidateOptionsMenu();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    };
                    mReference.addValueEventListener(mListener);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            };
            editText1.addTextChangedListener(textWatcher);

            Spinner spinner = (Spinner) findViewById(R.id.editEase);

            if (easeFlower.equals("Easy")) {
                spinner.setSelection(0);
            } else if (easeFlower.equals("Medium")) {
                spinner.setSelection(1);
            } else if (easeFlower.equals("Difficult")) {
                spinner.setSelection(2);
            }

            editText2.setText(instFlower);

            TextView textView1 = (TextView) findViewById(R.id.keyField);
            textView1.setText(keyFlower);

            TextView textView2 = (TextView) findViewById(R.id.urlField);
            textView2.setText(urlFlower);

            ImageView imageView = (ImageView) findViewById(R.id.photoView);

            if (!urlFlower.equals("dummyData")) {
                button1.setVisibility(View.VISIBLE);
                imageView.setDrawingCacheEnabled(true);
                Glide.with(imageView.getContext())
                        .load(urlFlower)
                        .into(imageView);
            }
        } else {
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    final String flowerName = editable.toString().trim();
                    mListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                String name = data.child("flowerName").getValue().toString().toLowerCase();
                                if (name.equals(flowerName.toLowerCase())) {
                                    editText1.getBackground().setColorFilter(Color.parseColor("#FF1744"), PorterDuff.Mode.SRC_IN);
                                    errorMessage.setText(flowerName + " already exists");
                                    exists = true;
                                    break;
                                } else {
                                    editText1.getBackground().clearColorFilter();
                                    errorMessage.setText("");
                                    exists = false;
                                }
                            }
                            invalidateOptionsMenu();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    };
                    mReference.addValueEventListener(mListener);
                }
            };
            editText1.addTextChangedListener(textWatcher);
        }
    }

    public void saveChanges() {

        EditText textView = (EditText) findViewById(R.id.editName);
        String fname = textView.getText().toString();

        Spinner textView1 = (Spinner) findViewById(R.id.editEase);
        String ease = textView1.getSelectedItem().toString();

        EditText textView2 = (EditText) findViewById(R.id.editInst);
        String inst = textView2.getText().toString();

        TextView textView3 = (TextView) findViewById(R.id.keyField);
        String key = textView3.getText().toString();

        TextView textView4 = (TextView) findViewById(R.id.urlField);
        String url = textView4.getText().toString();

        TextView textView5 = (TextView) findViewById(R.id.deletePhoto);
        String del = textView5.getText().toString();

        Intent intent = new Intent();

        if (fname.isEmpty() || fname.equals("")) {
            setResult(Activity.RESULT_CANCELED, intent);
            Toast.makeText(getApplicationContext(), "Blank item not saved", Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (!key.equals("") || !key.isEmpty()) { // Update only, already has assigned key

                try { // Photo for upload
                    intent.putExtra(KEY, key);
                    intent.putExtra(NAME, fname);
                    intent.putExtra(EASE, ease);
                    intent.putExtra(INST, inst);
                    intent.putExtra(PATH, path.toString());
                    intent.putExtra(URL, url);
                    intent.putExtra(DEL, del);

                    setResult(Activity.RESULT_OK, intent);
                } catch (NullPointerException ne) { // No photo
                    intent.putExtra(KEY, key);
                    intent.putExtra(NAME, fname);
                    intent.putExtra(EASE, ease);
                    intent.putExtra(INST, inst);
                    intent.putExtra(PATH, "");
                    intent.putExtra(URL, url);
                    intent.putExtra(DEL, del);

                    setResult(Activity.RESULT_OK, intent);
                }
                path = null;
                finish();
            } else { // Saving new flower item (NO key yet)
                try { // Photo for upload

                    intent.putExtra(KEY, "");
                    intent.putExtra(NAME, fname);
                    intent.putExtra(EASE, ease);
                    intent.putExtra(INST, inst);
                    intent.putExtra(PATH, path.toString());
                    intent.putExtra(URL, url);

                    setResult(Activity.RESULT_OK, intent);
                } catch (NullPointerException ne) { // No photo
                    intent.putExtra(KEY, "");
                    intent.putExtra(NAME, fname);
                    intent.putExtra(EASE, ease);
                    intent.putExtra(INST, inst);
                    intent.putExtra(PATH, "");
                    intent.putExtra(URL, url);

                    setResult(Activity.RESULT_OK, intent);
                }
                path = null;
                finish();
            }
        }
    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Upload from Library"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = Utility.checkPermission(AddActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoice = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Upload from Library")) {
                    userChoice = "Upload from Library";
                    if (result)
                        galleryIntent();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoice.equals("Take Photo")) {
                        cameraIntent();
                    } else if (userChoice.equals("Upload from Library")) {
                        galleryIntent();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Permission required", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;

        if (data != null) {
            String wholeID = DocumentsContract.getDocumentId(data.getData());
            String id = wholeID.split(":")[1];
            Long origId = Long.parseLong(id);

            bitmap = MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(),
                    origId, MediaStore.Images.Thumbnails.MINI_KIND, null);

            String imagePath = getThumbPath(origId);
            path = Uri.fromFile(new File(imagePath));
        }
        ImageView imageView = (ImageView) findViewById(R.id.photoView);
        imageView.setImageBitmap(bitmap);
        button1.setVisibility(View.VISIBLE);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        path = Uri.fromFile(destination);

        FileOutputStream fileOutputStream;
        try {
            destination.createNewFile();
            fileOutputStream = new FileOutputStream(destination);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ImageView imageView = (ImageView) findViewById(R.id.photoView);
        imageView.setImageBitmap(thumbnail);
        button1.setVisibility(View.VISIBLE);
    }

    public void removePhoto() {
        ImageView imageView = (ImageView) findViewById(R.id.photoView);
        TextView textView = (TextView) findViewById(R.id.deletePhoto);
        try {
            Bitmap bitmap = imageView.getDrawingCache();
            if (bitmap != null) {
                imageView.setImageBitmap(null);
                imageView.setImageResource(R.drawable.flower);
                textView.setText("delete");
                path = null;
                button1.setVisibility(View.INVISIBLE);
            } else {
                Bitmap bit2 = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                if (bit2 != null) {
                    imageView.setImageBitmap(null);
                    imageView.setImageResource(R.drawable.flower);
                    textView.setText("delete");
                    path = null;
                    button1.setVisibility(View.INVISIBLE);
                }
            }
        } catch (NullPointerException ne) {}
    }

    String getThumbPath(long longId) {
        Cursor thumbCursor = null;
        try {
            thumbCursor = getApplicationContext().getContentResolver().
                    query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, null,
                             MediaStore.Images.Thumbnails.IMAGE_ID + " = " + longId+ " AND "
                                    + MediaStore.Images.Thumbnails.KIND + " = "
                                    + MediaStore.Images.Thumbnails.MINI_KIND , null, null);
            if(thumbCursor.moveToFirst()) {
                int dataIndex = thumbCursor.getColumnIndexOrThrow( MediaStore.MediaColumns.DATA );
                return thumbCursor.getString(dataIndex);
            }
        } finally {
            if(thumbCursor != null){
                thumbCursor.close();
            }
        }
        return null;
    }
}
