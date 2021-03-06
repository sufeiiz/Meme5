package meme5.c4q.nyc.meme_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

public class ChooseMemeStyle extends Activity {

    protected Button nextButton;
    protected boolean vanilla;
    ImageView img, thumbnail;
    String imgFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_meme_style);

        // Get thumbnail of image chosed to be displayed in R.id.thumbnail
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("imgFilePath") != null)
            imgFilePath = bundle.getString("imgFilePath");

        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        Bitmap bmp2 = BitmapFactory.decodeFile(imgFilePath);
        thumbnail.setImageBitmap(bmp2);

        // set up radio buttons, with initial on vanilla
        RadioGroup group = (RadioGroup) findViewById(R.id.styleGroup);
        img = (ImageView) findViewById(R.id.sampleImageHolder);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.chooseVanilla:
                        vanilla = true;
                        img.setImageResource(R.drawable.vanilla_sample);
                        break;

                    case R.id.chooseDemotivational:
                        vanilla = false;
                        img.setImageResource(R.drawable.demotivational_sample);
                        break;
                }
            }
        });
        group.check(R.id.chooseVanilla);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (vanilla) {
                    Intent vanillameme = new Intent(ChooseMemeStyle.this, VanillaMeme.class);
                    vanillameme.putExtra("imgFilePath", imgFilePath);
                    vanillameme.putExtra("top", "");
                    vanillameme.putExtra("bottom", "");
                    startActivity(vanillameme);
                } else {
                    Intent demotivationalMeme = new Intent(ChooseMemeStyle.this, DemotivationalMeme.class);
                    demotivationalMeme.putExtra("imgFilePath", imgFilePath);
                    demotivationalMeme.putExtra("top", "");
                    demotivationalMeme.putExtra("bottom", "");
                    startActivity(demotivationalMeme);
                }
            }
        });
    }
}

