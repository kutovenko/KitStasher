package com.kutovenko.kitstasher.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.kutovenko.kitstasher.util.MyConstants;
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
        setContentView(com.kutovenko.kitstasher.R.layout.activity_crop);
        String u = "";
        if (getIntent().getExtras().getString("filepath") != null
                && !getIntent().getExtras().getString("filepath").equals(null)) {
            u = getIntent().getExtras().getString("filepath");
        } else if (getIntent().getStringExtra(MyConstants.FILE_URI) != null){
            u = "file:///" + getIntent().getStringExtra(MyConstants.FILE_URI);
        }
        Uri uri = Uri.parse(u);
        Uri destinationUri;
        if (getIntent().getExtras().getString(MyConstants.NEW_FILE_URI) != null){
            destinationUri = Uri.parse(getIntent().getExtras().getString(MyConstants.NEW_FILE_URI));

        } else {
            destinationUri = uri;
        }
        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle(getResources().getString(com.kutovenko.kitstasher.R.string.edit_image));
        options.setToolbarColor(getResources().getColor(com.kutovenko.kitstasher.R.color.colorPrimary));
        options.setActiveWidgetColor(getResources().getColor(com.kutovenko.kitstasher.R.color.colorPrimary));
        UCrop.of(uri, destinationUri)
                .useSourceImageAspectRatio()
//                .withAspectRatio(MyConstants.ASPECTRATIO_X, MyConstants.ASPECTRATIO_Y)
                .withMaxResultSize(MyConstants.SIZE_FULL_WIDTH, MyConstants.SIZE_FULL_HEIGHT)
                .withOptions(options)
                .start(this);
        Intent intent = new Intent();
        intent.putExtra(MyConstants.CROPPED_URI, String.valueOf(uri));
        setResult(RESULT_OK, intent);

        finish();
    }
}
