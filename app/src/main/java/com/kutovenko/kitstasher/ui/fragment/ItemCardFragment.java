package com.kutovenko.kitstasher.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentItemCardBinding;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.ui.EditActivity;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;

import java.io.File;


/**
 * Created by Алексей on 03.09.2017. Main display
 */

public class ItemCardFragment extends Fragment {
    private Context context;
    private FragmentItemCardBinding binding;

    private String category;
    public static final int EDIT_ACTIVITY_CODE = 21;
    private int tabToReturn;
    private StashItem stashItem;

    public ItemCardFragment() {
        super();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_card, container, false);
        context = getActivity();

        final String workMode = getArguments().getString(MyConstants.ITEM_TYPE);

        if ((workMode.equals(MyConstants.MODE_SEARCH))) {
            showSearchCard();
        } else {
            showDbCard(workMode);
        }

        return binding.getRoot();
    }
    private void showDbCard(final String workMode) {

        stashItem = getArguments().getParcelable(MyConstants.KIT);

        tabToReturn = getArguments().getInt(MyConstants.CATEGORY_TAB);

        final String kitname = stashItem.getName();
        final String brand = stashItem.getBrand();
        final String catno = stashItem.getBrandCatno();
        final int scale = stashItem.getScale();
        final String url = stashItem.getBoxartUrl();
        final String uri = stashItem.getBoxartUri();
        final String scalematesUrl = stashItem.getScalematesUrl();
        category = stashItem.getCategory();
        final String year = stashItem.getYear();
        final String description = stashItem.getDescription();
        final String origName = stashItem.getNoengName();
        final String notes = stashItem.getNotes();
        final String media = stashItem.getMedia();
        final int quantity = stashItem.getQuantity();
        final int status = stashItem.getStatus();
        final String shop = stashItem.getPlacePurchased();
        final String purchaseDate = stashItem.getDatePurchased();
        final int price = stashItem.getPrice();
        final String currency = stashItem.getCurrency();

        binding.tvKitname.setText(kitname);
        binding.tvOriginalKitName.setText(origName);
        binding.tvBrand.setText(brand);
        binding.tvCatno.setText(catno);
        String scaleText = "1/" + String.valueOf(scale);
        binding.tvScale.setText(scaleText);
        binding.tvMedia.setText(codeToMedia(media));
        binding.tvDesc.setText(codeToDescription(description));
        binding.tvYear.setText(year);

        binding.tvShop.setText(shop);

        if (notes != null && !notes.equals(MyConstants.EMPTY)) {
            binding.tvNotes.setText(notes);
        }
        binding.tvQuantity.setText(String.valueOf(quantity));
        binding.tvDatePurchased.setText(purchaseDate);
        binding.tvPrice.setText(String.valueOf(price / 100));
        binding.tvCurrency.setText(currency);
        binding.tvScalemates.setClickable(true);
        binding.tvScalemates.setMovementMethod(LinkMovementMethod.getInstance());

        String scalematesText = "<a href='"
                + scalematesUrl
                + "'> " + getString(com.kutovenko.kitstasher.R.string.Look_up_on_Scalemates) + "</a>";
        if (!Helper.isBlank(scalematesUrl)) {
            binding.tvScalemates.setText(Helper.fromHtml(scalematesText));
        } else {
            binding.tvScalemates.setVisibility(View.GONE);
        }

        binding.tvGoogle.setClickable(true);
        binding.tvGoogle.setMovementMethod(LinkMovementMethod.getInstance());
        String googleText = "<a href='https://www.google.com/search?"
                + "q=" + brand + "+" + catno + "+" + kitname + "+" + scale
                + "'> " + getString(com.kutovenko.kitstasher.R.string.Search_with_Google) + "</a>";
        binding.tvGoogle.setText(Helper.fromHtml(googleText));

        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(new File(uri))
                    .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                    .into(binding.ivBoxart);
        } else {
            Glide
                    .with(context)
                    .load(Helper.composeUrl(url, MyConstants.BOXART_URL_LARGE))
                    .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                    .into(binding.ivBoxart);
        }

        setCategoryImage(category);

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // TODO: 04.07.2018 replace with fragment
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(MyConstants.KIT, stashItem);
                intent.putExtra(MyConstants.CATEGORY_TAB, tabToReturn);
                intent.putExtra(MyConstants.ITEM_TYPE, workMode);
                getActivity().startActivityForResult(intent, EDIT_ACTIVITY_CODE);
            }
        });
    }

    private void showSearchCard() { // TODO: 11.04.2018 переписать с stashItem

        String kitname = getArguments().getString(MyConstants.KITNAME);
        binding.tvKitname.setText(kitname);

        String origName = getArguments().getString(MyConstants.ORIGINAL_NAME);
        binding.tvOriginalKitName.setText(origName);

        String brand = getArguments().getString(MyConstants.BRAND);
        binding.tvBrand.setText(brand);

        String catno = getArguments().getString(MyConstants.CATNO);
        binding.tvCatno.setText(catno);

        int scale = getArguments().getInt(MyConstants.SCALE);
        String scaleText = "1/" + String.valueOf(scale);
        binding.tvScale.setText(scaleText);

        binding.tvMedia.setText(codeToMedia(getArguments().getString(MyConstants.MEDIA, MyConstants.EMPTY)));
        binding.tvDesc.setText(codeToDescription(getArguments().getString(MyConstants.DESCRIPTION, MyConstants.EMPTY)));
        binding.tvYear.setText(getArguments().getString(MyConstants.YEAR));
        binding.tvShop.setVisibility(View.GONE);
        binding.tvQuantity.setVisibility(View.GONE);
        binding.tvDatePurchased.setVisibility(View.GONE);
        binding.tvPrice.setVisibility(View.GONE);
        binding.tvCurrency.setVisibility(View.GONE);
        binding.tvPurchaseTitle.setVisibility(View.GONE);
        binding.tableLayoutPurchase.setVisibility(View.GONE);
        binding.tvNotesTitle.setVisibility(View.GONE);
        binding.tvNotes.setVisibility(View.GONE);
        binding.btnEdit.setVisibility(View.GONE);


        binding.tvScalemates.setClickable(true);
        binding.tvScalemates.setMovementMethod(LinkMovementMethod.getInstance());
        String scalemates = getArguments().getString(MyConstants.SCALEMATES);
        String scalematesText = "<a href='"
                + scalemates
                + "'> " + getString(com.kutovenko.kitstasher.R.string.Look_up_on_Scalemates) + "</a>";
        if (scalemates != null) {
            binding.tvScalemates.setText(Helper.fromHtml(scalematesText));
        } else {
            binding.tvScalemates.setText("");
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 16);
        binding.llButton.setLayoutParams(layoutParams);


        binding.tvGoogle.setClickable(true);
        binding.tvGoogle.setMovementMethod(LinkMovementMethod.getInstance());
        String googleText = "<a href='https://www.google.com/search?"
                + "q=" + brand + "+" + catno + "+" + kitname + "+" + scale
                + "'> " + getString(com.kutovenko.kitstasher.R.string.Search_with_Google) + "</a>";
        binding.tvGoogle.setText(Helper.fromHtml(googleText));
        String url = getArguments().getString(MyConstants.BOXART_URL);

        Glide
                .with(context)
                .load(Helper.composeUrl(url, MyConstants.BOXART_URL_LARGE))
                .into(binding.ivBoxart);

        setCategoryImage(category);
    }


    private void setCategoryImage(String category) {
        if (MyConstants.CODE_SEA.equals(category)) {
            binding.ivCategory.setImageResource(com.kutovenko.kitstasher.R.drawable.ic_tag_ship_black_24dp);
        }
        if (MyConstants.CODE_AIR.equals(category)) {
            binding.ivCategory.setImageResource(com.kutovenko.kitstasher.R.drawable.ic_tag_air_black_24dp);
        }
        if (MyConstants.CODE_GROUND.equals(category)) {
            binding.ivCategory.setImageResource(com.kutovenko.kitstasher.R.drawable.ic_tag_afv_black_24dp);
        }
        if (MyConstants.CODE_SPACE.equals(category)) {
            binding.ivCategory.setImageResource(com.kutovenko.kitstasher.R.drawable.ic_tag_space_black_24dp);
        }
        if (MyConstants.CODE_OTHER.equals(category)) {
            binding.ivCategory.setImageResource(com.kutovenko.kitstasher.R.drawable.ic_help_black_24dp);
        }
        if (MyConstants.CODE_AUTOMOTO.equals(category)) {
            binding.ivCategory.setImageResource(com.kutovenko.kitstasher.R.drawable.ic_directions_car_black_24dp);
        }
        if (MyConstants.CODE_FIGURES.equals(category)) {
            binding.ivCategory.setImageResource(com.kutovenko.kitstasher.R.drawable.ic_wc_black_24dp);
        }
        if (MyConstants.CODE_FANTASY.equals(category)) {
            binding.ivCategory.setImageResource(com.kutovenko.kitstasher.R.drawable.ic_android_black_24dp);
        }
    }

    private String codeToDescription(String code) {
        String desc = "";
        switch (code) {
            case MyConstants.NEW_TOOL:
                desc = getResources().getString(com.kutovenko.kitstasher.R.string.newkit);
                break;
            case MyConstants.REBOX:
                desc = getResources().getString(com.kutovenko.kitstasher.R.string.rebox);
                break;
        }
        return desc;
    }

    private String codeToMedia(String mediaCode) {
        String mediaText;
        switch (mediaCode) {
            case MyConstants.M_CODE_UNKNOWN:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.unknown);
                break;
            case MyConstants.M_CODE_INJECTED:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_injected);
                break;
            case MyConstants.M_CODE_SHORTRUN:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_shortrun);
                break;
            case MyConstants.M_CODE_RESIN:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_resin);
                break;
            case MyConstants.M_CODE_VACU:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_vacu);
                break;
            case MyConstants.M_CODE_PAPER:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_paper);
                break;
            case MyConstants.M_CODE_WOOD:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_wood);
                break;
            case MyConstants.M_CODE_METAL:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_metal);
                break;
            case MyConstants.M_CODE_3DPRINT:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_3dprint);
                break;
            case MyConstants.M_CODE_MULTIMEDIA:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_multimedia);
                break;
            case MyConstants.M_CODE_OTHER:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_other);
                break;
            case MyConstants.M_CODE_DECAL:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_decal);
                break;
            case MyConstants.M_CODE_MASK:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.media_mask);
                break;

            default:
                mediaText = getResources().getString(com.kutovenko.kitstasher.R.string.unknown);
                break;
        }
        return mediaText;
    }
}
