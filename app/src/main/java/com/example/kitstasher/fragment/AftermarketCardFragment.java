package com.example.kitstasher.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.kitstasher.activity.AftermarketActivity;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.io.File;

import static android.view.View.GONE;

/**
 * Created by Алексей on 08.09.2017. Shows Aftermarket card
 * Просмотр карточки афтермаркета
 * Вызывается из
 * SortAllFragment, тогда MODE_AFTERMARKET
 * KitCard - просморт всего афтера кита
 * KitEdit -
 */

public class AftermarketCardFragment extends Fragment {
    private ImageView ivCategory;
    private String category;
    private char mode;

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

//        String listname = getArguments().getString(Constants.LISTNAME);
//        final long kitId = getArguments().getLong(Constants.ID); //для возврата в Кит
        final long afterId = getArguments().getLong(Constants.AFTER_ID); //для демонстрации карточки
        //Модет быть MODE_AFTERMARKET, если из таблицы
        // MODE_KIT, если из просмотра
        // или редактора MODE_AFTER_KIT
        mode = getArguments().getChar(Constants.WORK_MODE);

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

        Button btnEditAftermarket = view.findViewById(R.id.btnEdit);
        btnEditAftermarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AftermarketActivity.counter++;
//                AftermarketEditFragment fragment = new AftermarketEditFragment();
                ItemEditFragment fragment = new ItemEditFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.AFTER_ID, afterId);
                bundle.putLong(Constants.ID, afterId);
                bundle.putChar(Constants.WORK_MODE, mode); //отправляем , еслди
                // пришли из таблицы посмотреть MODE_AFTARMARKET
                //ЕСли из Кита - MODE_KIT
                //Если из КптЕдит - MODE_AFTER_KIT

                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.linlayoutAftermarketContainer, fragment);
                fragmentTransaction.commit();
            }
        });

        setCategoryImage();

        ImageView ivBoxart = view.findViewById(R.id.ivBoxart);
        ivCategory = view.findViewById(R.id.ivCategory);
        category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
        TextView tvKitname = view.findViewById(R.id.tvKitname);
        tvKitname.setText(kitname);
        TextView tvBrand = view.findViewById(R.id.tvBrand);
        tvBrand.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND)));
        TextView tvBrandcatno = view.findViewById(R.id.tvCatno);
        tvBrandcatno.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO)));
        TextView tvScale = view.findViewById(R.id.tvScale);
        tvScale.setText("1/" + cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE)));
        TextView tvAfterNotes = view.findViewById(R.id.tvNotes);
        tvAfterNotes.setText(notes);
        TextView tvAfterTitle = view.findViewById(R.id.tvAftermarketTitle);
        tvAfterTitle.setVisibility(GONE);
        ListView lvAfter = view.findViewById(R.id.lvAftermarket);
        lvAfter.setVisibility(GONE);
        TextView tvScalemates = view.findViewById(R.id.tvScalemates);
        tvScalemates.setVisibility(GONE);
        TextView tvGoogleUrl = view.findViewById(R.id.tvGoogle);
        tvGoogleUrl.setClickable(true);
        tvGoogleUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String googleText = "<a href='https://www.google.com/search?"
                + "q=" + brand + "+" + catno + "+" + kitname + "+" + scale
                + "'> " + getString(R.string.Search_with_Google) + "</a>";
        tvGoogleUrl.setText(Helper.fromHtml(googleText));

        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(new File(Uri.parse(uri).getPath()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivBoxart);
        }
//        else {
//            Glide
//                    .with(context)
//                    .load(Helper.composeUrl(url, context))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(ivBoxart);
//        }

        return view;
    }

    private void setCategoryImage() {
        if (Constants.CODE_SEA.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_ship_black_24dp);
        }
        if (Constants.CODE_AIR.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_air_black_24dp);
        }
        if (Constants.CODE_GROUND.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_afv_black_24dp);
        }
        if (Constants.CODE_SPACE.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_space_black_24dp);
        }
        if (Constants.CODE_OTHER.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }
        if (Constants.CODE_AUTOMOTO.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_directions_car_black_24dp);
        }
        if (Constants.CODE_FIGURES.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_wc_black_24dp);
        }
        if (Constants.CODE_FANTASY.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_android_black_24dp);
        }
    }
}
