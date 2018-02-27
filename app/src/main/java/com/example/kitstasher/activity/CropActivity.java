package com.example.kitstasher.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.other.MyConstants;
import com.yalantis.ucrop.UCrop;


public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Intent intent = getIntent();

        String u = "file:///" + intent.getExtras().getString(MyConstants.FILE_URI);
        Uri uri = Uri.parse(u);
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
        intent1.putExtra(MyConstants.CROPPED_URI, String.valueOf(uri));
        setResult(RESULT_OK, intent1);

        finish();
    }
}
