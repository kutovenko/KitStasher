package com.example.kitstasher.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.other.Constants;
import com.yalantis.ucrop.UCrop;


public class CropActivity extends AppCompatActivity {
    String brand, brandCatno, description, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Intent intent = getIntent();

        String u = "file:///" + intent.getExtras().getString(Constants.FILE_URI);
        Uri uri = Uri.parse(u);

//        brand = intent.getExtras().getString(Constants.BRAND);
//        brandCatno = intent.getExtras().getString(Constants.CATNO);
//        description = intent.getExtras().getString(Constants.DESCRIPTION);
//        year = intent.getExtras().getString(Constants.YEAR);

        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle(getResources().getString(R.string.edit_image));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        UCrop.of(uri, uri)
                .withAspectRatio(16, 10)
                .withMaxResultSize(720, 450)
                .withOptions(options)
                .start(this);
        Intent intent1 = new Intent();
        intent1.putExtra(Constants.CROPPED_URI, String.valueOf(uri));
        setResult(RESULT_OK, intent1);

        finish();
    }
}
