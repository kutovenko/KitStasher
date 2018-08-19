package com.example.kitstasher.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.other.MyConstants;
import com.yalantis.ucrop.UCrop;

/**
 * Created by Alexey on 21.04.2018.
 * Crops boxart photo with the use of UCrop library.
 *
 * Кадрирование фото боксарта с помощью библиотеки UCrop.
 */

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        String u = "file:///" + getIntent().getStringExtra(MyConstants.FILE_URI);
        Uri uri = Uri.parse(u);
        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle(getResources().getString(R.string.edit_image));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        UCrop.of(uri, uri)
                .withAspectRatio(MyConstants.ASPECTRATIO_X, MyConstants.ASPECTRATIO_Y)
                .withMaxResultSize(MyConstants.SIZE_FULL_WIDTH, MyConstants.SIZE_FULL_HEIGHT)
                .withOptions(options)
                .start(this);
        Intent intent = new Intent();
        intent.putExtra(MyConstants.CROPPED_URI, String.valueOf(uri));
        setResult(RESULT_OK, intent);

        finish();
    }
}
