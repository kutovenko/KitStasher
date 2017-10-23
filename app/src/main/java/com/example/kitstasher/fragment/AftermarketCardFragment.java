package com.example.kitstasher.fragment;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.io.File;

import static android.R.drawable.ic_menu_camera;
import static android.view.View.GONE;

/**
 * Created by Алексей on 08.09.2017.
 */

public class AftermarketCardFragment extends Fragment {

    public AftermarketCardFragment() {
        super();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_card, container, false);
        Context context = getActivity();
        String listname = getArguments().getString(Constants.LISTNAME);
        final long kitId = getArguments().getLong(Constants.ID); //для возврата в Кит
        final long afterId = getArguments().getLong(Constants.AFTER_ID); //для демонстрации карточки
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();
        Cursor cursor = dbConnector.getAftermarketByID(afterId);
        cursor.moveToFirst();

        String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
        String kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_AFTERMARKET_NAME));
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        String catno = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
        String scale = "1/" + String.valueOf(getArguments().getInt(Constants.SCALE));
//        String url = getArguments().getString(Constants.URL);
//        String scalematesUrl = getArguments().getString(Constants.SCALEMATES);
        String notes = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES));

        Button btnEditAftermarket = (Button) view.findViewById(R.id.btnEdit);
        btnEditAftermarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AftermarketEditFragment fragment = new AftermarketEditFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.AFTER_ID, afterId);
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.linlayoutAftermarketContainer, fragment);
                fragmentTransaction.commit();
            }
        });

        ImageView ivBoxart = (ImageView) view.findViewById(R.id.ivBoxart);
        TextView tvKitname = (TextView) view.findViewById(R.id.tvKitname);
        tvKitname.setText(kitname);
        TextView tvBrand = (TextView) view.findViewById(R.id.tvBrand);
        tvBrand.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND)));
        TextView tvBrandcatno = (TextView) view.findViewById(R.id.tvCatno);
        tvBrandcatno.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO)));
        TextView tvScale = (TextView) view.findViewById(R.id.tvScale);
        tvScale.setText("1/" + cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE)));
        TextView tvAfterNotes = (TextView) view.findViewById(R.id.tvNotes);
        tvAfterNotes.setText(notes);
        TextView tvAfterTitle = (TextView) view.findViewById(R.id.tvAftermarketTitle);
        tvAfterTitle.setVisibility(GONE);
        ListView lvAfter = (ListView) view.findViewById(R.id.lvAftermarket);
        lvAfter.setVisibility(GONE);
        TextView tvScalemates = (TextView) view.findViewById(R.id.tvScalemates);
        tvScalemates.setVisibility(GONE);
        TextView tvGoogleUrl = (TextView) view.findViewById(R.id.tvGoogle);
        tvGoogleUrl.setClickable(true);
        tvGoogleUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String googleText = "<a href='https://www.google.com/search?"
                + "q=" + brand + "+" + catno + "+" + kitname + "+" + scale
                + "'> " + getString(R.string.Search_with_Google) + "</a>";
        tvGoogleUrl.setText(Helper.fromHtml(googleText));

        Glide
                .with(context)
                .load(new File(Uri.parse(Environment.getExternalStorageDirectory()
                        + Constants.APP_FOLDER + uri).getPath()))
                .placeholder(ic_menu_camera)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.crossFade()
                .into(ivBoxart);

        return view;
    }
}
