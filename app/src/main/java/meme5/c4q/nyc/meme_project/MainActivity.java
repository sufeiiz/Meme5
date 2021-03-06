package meme5.c4q.nyc.meme_project;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.widget.ImageButton;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends Activity {

    private static final int RESULT_LOAD_IMG = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String imgFilePath;
    private Uri mCapturedImageURI;
    private ImageButton camera, gallery, memeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "Image File name");
                    mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (galleryIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                }
            }
        });

        memeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemeSearch.class);
                startActivity(intent);
            }
        });
    }

    public void initializeViews() {
        camera = (ImageButton) findViewById(R.id.cameraButton);
        gallery = (ImageButton) findViewById(R.id.albumButton);
        memeSearch = (ImageButton) findViewById(R.id.google);
    }

    // launches intent for meme layout choice
    private void launchChooseMeme() {
        Intent chooseMeme = new Intent(this, ChooseMemeStyle.class);
        chooseMeme.putExtra("imgFilePath", imgFilePath);
        startActivity(chooseMeme);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            // choose camera
            if (requestCode == 1 && resultCode == RESULT_OK) {

                Uri selectedImage = mCapturedImageURI;
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgFilePath = cursor.getString(columnIndex);
                cursor.close();
                launchChooseMeme();

            // choose gallery
            } else if (requestCode == 0 && resultCode == RESULT_OK && data != null) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgFilePath = cursor.getString(columnIndex);
                cursor.close();
                launchChooseMeme();
            } else {
                Toast.makeText(this, "You haven't picked an Image yet", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
}

