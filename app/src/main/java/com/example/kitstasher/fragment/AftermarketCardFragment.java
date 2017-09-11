package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.io.File;

import static android.R.drawable.ic_menu_camera;

/**
 * Created by Алексей on 08.09.2017.
 */

public class AftermarketCardFragment extends Fragment {
    private Context context;
    private ImageView ivBoxart;
    private TextView tvKitname, tvBrand, tvBrandcatno, tvScale, tvScalematesUrl, tvGoogleUrl;
    private Button btnBack;

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
        View view = inflater.inflate(R.layout.fragment_kit_card, container, false);
        context = getActivity();


//        Character mode = getArguments().getChar("mode");
        String listname = getArguments().getString("listname");

//        int scale = getArguments().getInt("scale");
//                bundle.putLong("kitid", cursor.getLong(cursor.getColumnIndex(DbConnector.COLUMN_ID)));
//        int position = getArguments().getInt("position");
//        int categoryToreturn = getArguments().getInt("category");
        long after_id = getArguments().getLong("id");

        dbConnector = new DbConnector(context);
        dbConnector.open();

        Cursor cursor = dbConnector.getAftermarketByID(after_id);
        cursor.moveToFirst();
        String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));

        String kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_AFTERMARKET_NAME));
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        String catno = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
        String scale = "1/" + String.valueOf(getArguments().getInt("scale"));
        String url = getArguments().getString("url");
        String scalematesUrl = getArguments().getString("scalemates");

        String pictureName = brand
                + catno
                + Constants.SIZE_SMALL
                + ".jpg";





        btnBack = (Button)view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo нужно вернуться на карточку кита, т.е во фрагмент другой активити
//                SearchFragment searchFragment = new SearchFragment();
//                android.support.v4.app.FragmentTransaction fragmentTransaction =
//                        getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.mainactivityContainer, searchFragment);
//                fragmentTransaction.addToBackStack("card_fragment");
//                fragmentTransaction.commit();
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
        tvScale.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE)));

        tvScalematesUrl = (TextView)view.findViewById(R.id.tvScalemates);
        tvScalematesUrl.setClickable(true);
        tvScalematesUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String scalematesText = "<a href='https://www.scalemates.com/"
                + scalematesUrl
                + "'> " + getString(R.string.Look_up_on_Scalemates) + "</a>";
        tvScalematesUrl.setText(Helper.fromHtml(scalematesText));

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
