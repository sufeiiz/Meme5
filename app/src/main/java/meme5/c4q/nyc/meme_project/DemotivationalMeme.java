package meme5.c4q.nyc.meme_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by c4q-anthonyf on 6/5/15
 * Edited by c4q-sufeiz on 6/8/15
 */
public class DemotivationalMeme extends Activity {

    Bitmap image, memeImage;
    ImageView preview;
    EditText topET, bottomET;
    Button switch_btn, save, share;
    String imgFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demotivational_meme);

        initializeViews();

        // get image and text fromt bundle, populate views
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("imgFilePath") != null) {
            imgFilePath = bundle.getString("imgFilePath");
            image = Decode.decodeFile(image, imgFilePath, true);
            topET.setText(bundle.getString("top"));
            bottomET.setText(bundle.getString("bottom"));

            if (!topET.getText().toString().isEmpty() || !bottomET.getText().toString().isEmpty()) {
                memeImage = drawTextToBitmap(image.copy(image.getConfig(), true), topET.getText().toString().toUpperCase(), true);
                memeImage = drawTextToBitmap(memeImage, bottomET.getText().toString().toUpperCase(), false);
                preview.setImageBitmap(memeImage);
            } else
                preview.setImageBitmap(image);
        }

        // custom textwatch draws text onto image as editText changes
        TextWatch tw = new TextWatch(topET, bottomET);
        topET.addTextChangedListener(tw);
        bottomET.addTextChangedListener(tw);

        switch_btn.setOnClickListener(new SwitchListener());
        save.setOnClickListener(new SaveListener());
        share.setOnClickListener(new ShareListener());
    }

    public void initializeViews() {
        preview = (ImageView) findViewById(R.id.preview);
        topET = (EditText) findViewById(R.id.large);
        bottomET = (EditText) findViewById(R.id.small);
        switch_btn = (Button) findViewById(R.id.switch_btn);
        save = (Button) findViewById(R.id.save);
        share = (Button) findViewById(R.id.share);
    }

    public class TextWatch implements TextWatcher {

        private EditText large, small;

        public TextWatch(EditText large, EditText small) {
            this.large = large;
            this.small = small;
        }

        // as text changes, update image for preview of meme
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            memeImage = drawTextToBitmap(image.copy(image.getConfig(), true), large.getText().toString().toUpperCase(), true);
            memeImage = drawTextToBitmap(memeImage, small.getText().toString(), false);
            preview.setImageBitmap(memeImage);
        }
        @Override
        public void afterTextChanged(Editable s) {}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    }

    public Bitmap drawTextToBitmap(Bitmap bitmap, String mText1, boolean largeText) {
        try {
            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            int textSize, heightOffset;

            // set default bitmap config if none
            if (bitmapConfig == null)
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;

            // resource bitmaps are imutable, so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);

            if (largeText) {
                textSize = 100;
                heightOffset = 340;
                mText1 = mText1.toUpperCase();
            } else {
                textSize = 60;
                heightOffset = 220;
            }

            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(10f, 10f, 10f, Color.BLACK);
            paint.setTextAlign(Paint.Align.CENTER);

            Rect rectText = new Rect();
            paint.getTextBounds(mText1, 0, mText1.length(), rectText);

            StaticLayout mTextLayout = new StaticLayout(mText1, paint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 0.8f, 0.0f, false);
            int xPos = (bitmap.getWidth() / 2) - 2;
            int yPos = canvas.getHeight() + rectText.height() - heightOffset;
            canvas.translate(xPos, yPos);

            mTextLayout.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public class SwitchListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent vanillaMeme = new Intent(getApplicationContext(), VanillaMeme.class);
            vanillaMeme.putExtra("imgFilePath", imgFilePath);
            vanillaMeme.putExtra("top", topET.getText().toString());
            vanillaMeme.putExtra("bottom", bottomET.getText().toString());
            startActivity(vanillaMeme);
        }
    }

    public class SaveListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String timeStamp = "meme_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
            MediaStore.Images.Media.insertImage(getContentResolver(), memeImage, timeStamp, "Created with Meme5");
            Toast.makeText(getApplicationContext(), "Meme has been saved!", Toast.LENGTH_LONG).show();
        }
    }

    public class ShareListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            memeImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
            startActivity(Intent.createChooser(share, "Share Image"));
        }
    }
}
