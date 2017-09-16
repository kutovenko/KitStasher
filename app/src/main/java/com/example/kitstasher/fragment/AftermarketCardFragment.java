package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.KitActivity;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.io.File;

import static android.R.drawable.ic_menu_camera;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Алексей on 08.09.2017.
 */

public class AftermarketCardFragment extends Fragment {
    private Context context;
    private ImageView ivBoxart;
    private TextView tvKitname, tvBrand, tvBrandcatno, tvScale, tvScalematesUrl, tvGoogleUrl, tvAfterNotes;
    private Button btnBack, btnEditAftermarket;

    DbConnector dbConnector;

    public AftermarketCardFragment() {
        super();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_after_card, container, false);
        context = getActivity();



        String listname = getArguments().getString("listname");

        final long kitId = getArguments().getLong("id"); //для возврата в Кит
        final long afterId = getArguments().getLong("after_id"); //для демонстрации карточки

        dbConnector = new DbConnector(context);
        dbConnector.open();

        Cursor cursor = dbConnector.getAftermarketByID(afterId);
        cursor.moveToFirst();
        String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));

        String kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_AFTERMARKET_NAME));
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        String catno = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
        String scale = "1/" + String.valueOf(getArguments().getInt("scale"));
        String url = getArguments().getString("url");
        String scalematesUrl = getArguments().getString("scalemates");
        String notes = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES));

        String pictureName = uri;
//        String pictureName = brand
//                + catno
////                + Constants.SIZE_SMALL
//                + ".jpg";





        btnBack = (Button)view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, KitActivity.class);
                intent.putExtra("id", kitId);
                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();

            }
        });
        btnEditAftermarket = (Button)view.findViewById(R.id.btnEditAftermarket);
        btnEditAftermarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AftermarketEditFragment fragment = new AftermarketEditFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("after_id", afterId);
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.linlayoutAftermarketContainer, fragment);
                fragmentTransaction.commit();
            }
        });

        ivBoxart = (ImageView)view.findViewById(R.id.ivBoxart);
        tvKitname = (TextView)view.findViewById(R.id.tvKitname);
        tvKitname.setText(kitname);
        tvBrand = (TextView)view.findViewById(R.id.tvBrand);
        tvBrand.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND)));
        tvBrandcatno = (TextView)view.findViewById(R.id.tvCatno);
        tvBrandcatno.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO)));
        tvScale = (TextView)view.findViewById(R.id.tvScale);
        tvScale.setText("1/" + cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE)));

        tvAfterNotes = (TextView)view.findViewById(R.id.tvNotes);
        tvAfterNotes.setText(notes);


        tvGoogleUrl = (TextView)view.findViewById(R.id.tvGoogle);
        tvGoogleUrl.setClickable(true);
        tvGoogleUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String googleText = "<a href='https://www.google.com/search?"
                + "q=" + brand + "+" + catno + "+" + kitname + "+" + scale
                + "'> " + getString(R.string.Search_with_Google) + "</a>";
        tvGoogleUrl.setText(Helper.fromHtml(googleText));


        Glide
                .with(context)
                .load(new File(Uri.parse(Environment.getExternalStorageDirectory()
                        + Constants.APP_FOLDER + pictureName).getPath()))
                .placeholder(ic_menu_camera)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.crossFade()
                .into(ivBoxart);


        return view;
    }

    private String composeUrl(String url){//// TODO: 04.09.2017 Helper
        if (!Helper.isBlank(url)) {
            return Constants.BOXART_URL_PREFIX
                    + url
                    + "pristine"
                    + Constants.BOXART_URL_POSTFIX;
        }else{
            return "";
        }
    }

    private String getSuffix(){
        String suffix = Constants.BOXART_URL_SMALL;
        SharedPreferences preferences = context.getSharedPreferences(Constants.BOXART_SIZE,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            String temp = preferences.getString("boxart_size","");
            switch (temp){
                case Constants.BOXART_URL_COMPANY_SUFFIX:
                    suffix = "";
                    break;
                case Constants.BOXART_URL_SMALL:
                    suffix = Constants.BOXART_URL_SMALL;
                    break;
                case Constants.BOXART_URL_MEDIUM:
                    suffix = Constants.BOXART_URL_MEDIUM;
                    break;
                case Constants.BOXART_URL_LARGE:
                    suffix = Constants.BOXART_URL_LARGE;
                    break;
                default:
                    break;
            }
        }

        return suffix;
    }

}
