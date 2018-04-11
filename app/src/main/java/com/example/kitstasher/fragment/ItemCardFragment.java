package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.EditActivity;
import com.example.kitstasher.adapters.NewMyListAdapter;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Алексей on 03.09.2017. Main display
 */

public class ItemCardFragment extends Fragment {
    private Context context;
    private View view;
    private ImageView ivBoxart,
            ivCategory;
    private TextView tvKitname,
            tvOriginalKitName,
            tvBrand,
            tvBrandcatno,
            tvScale,
            tvStatus,
            tvMedia,
            tvDesc,
            tvYear,
            tvShop,
            tvNotes,
            tvQuantity,
            tvDatePurchased,
            tvPrice,
            tvCurrency,
            tvScalematesUrl,
            tvGoogleUrl,
            tvPurchaseTitle,
            tvAftermarketTitle,
            tvNotesTitle;
    private Button btnEdit;
    private TableLayout tableLayoutPurchase;
    private RecyclerView rvAftermarket;
    private String category,
            tableName;
    private final int EDIT_ACTIVITY_CODE = 21;
    private int tabToReturn;

    private Kit kit;


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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_card, container, false);
        context = getActivity();


        initUi();
        final char workMode = getArguments().getChar(MyConstants.WORK_MODE);

        if ((workMode == MyConstants.MODE_SEARCH)) {
            showSearchCard();
        } else {
            showDbCard(workMode);
        }

        return view;
    }

    private void initUi() {
        ivBoxart = view.findViewById(R.id.ivBoxart);
        tvKitname = view.findViewById(R.id.tvKitname);
        tvOriginalKitName = view.findViewById(R.id.tvOriginalKitName);
        tvBrand = view.findViewById(R.id.tvBrand);
        tvBrandcatno = view.findViewById(R.id.tvCatno);
        tvScale = view.findViewById(R.id.tvScale);
        ivCategory = view.findViewById(R.id.ivCategory);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvMedia = view.findViewById(R.id.tvMedia);
        tvDesc = view.findViewById(R.id.tvDesc);
        tvYear = view.findViewById(R.id.tvYear);
        tvNotes = view.findViewById(R.id.tvNotes);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        tvShop = view.findViewById(R.id.tvShop);
        tvDatePurchased = view.findViewById(R.id.tvDatePurchased);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvCurrency = view.findViewById(R.id.tvCurrency);
        rvAftermarket = view.findViewById(R.id.lvAftermarket);
        RecyclerView.LayoutManager afterManager = new LinearLayoutManager(context);
        rvAftermarket.setHasFixedSize(true);
        rvAftermarket.setLayoutManager(afterManager);
        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        rvAftermarket.setItemAnimator(animator);

        tvScalematesUrl = view.findViewById(R.id.tvScalemates);
        tvGoogleUrl = view.findViewById(R.id.tvGoogle);
        btnEdit = view.findViewById(R.id.btnEdit);

        tvPurchaseTitle = view.findViewById(R.id.tvPurchaseTitle);
        tvAftermarketTitle = view.findViewById(R.id.tvAftermarketTitle);
        tableLayoutPurchase = view.findViewById(R.id.tableLayoutPurchase);
        tvNotesTitle = view.findViewById(R.id.tvNotesTitle);

    }

    private void showDbCard(final char workMode) {
        DbConnector dbConnector = new DbConnector(context);

        kit = getArguments().getParcelable("kit");

        tabToReturn = getArguments().getInt(MyConstants.CATEGORY_TAB);

        final long id = kit.getLocalId();
        final String kitname = kit.getKit_name();
        final String brand = kit.getBrand();
        final String catno = kit.getBrandCatno();
        final int scale = kit.getScale();
        final String url = kit.getBoxart_url();
        final String uri = kit.getBoxart_uri();
        final String scalematesUrl = kit.getScalemates_url();
        category = kit.getCategory();
        final String year = kit.getYear();
        final String description = kit.getDescription();
        final String origName = kit.getKit_noeng_name();
        final String notes = kit.getNotes();
        final int media = kit.getMedia();
        final int quantity = kit.getQuantity();
        final int status = kit.getStatus();
        final String shop = kit.getPlacePurchased();
        final String purchaseDate = kit.getDatePurchased();
        final int price = kit.getPrice();
        final String currency = kit.getCurrency();

        tvKitname = view.findViewById(R.id.tvKitname);
        tvKitname.setText(kitname);
        tvOriginalKitName.setText(origName);
        tvBrand.setText(brand);
        tvBrandcatno.setText(catno);
        String scaleText = "1/" + String.valueOf(scale);
        tvScale.setText(scaleText);
        ivCategory = view.findViewById(R.id.ivCategory);

        tvStatus.setText(codeToStatus(status));
        tvMedia.setText(codeToMedia(media));
        tvDesc.setText(codeToDescription(description));
        tvYear.setText(year);

        tvShop.setText(shop);

        if (notes != null && !notes.equals(MyConstants.EMPTY)) {
            tvNotes.setText(notes);
        }
        tvQuantity.setText(String.valueOf(quantity));
        tvDatePurchased.setText(purchaseDate);
        tvPrice.setText(String.valueOf(price / 100));
        tvCurrency.setText(currency);
        tvScalematesUrl.setClickable(true);
        tvScalematesUrl.setMovementMethod(LinkMovementMethod.getInstance());

        String scalematesText = "<a href='"
                + scalematesUrl
                + "'> " + getString(R.string.Look_up_on_Scalemates) + "</a>";
        if (scalematesUrl != null) {
            tvScalematesUrl.setText(Helper.fromHtml(scalematesText));
        } else {
            tvScalematesUrl.setText("");
        }

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
        } else {
            Glide
                    .with(context)
                    .load(Helper.composeUrl(url))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivBoxart);
        }

        setCategoryImage(category);

        ArrayList<Kit> aCursor = dbConnector.getAftermarketForKit(id, "");
//        final Cursor aCursor = dbConnector.getAftermarketForKit(id, listname);
        NewMyListAdapter afterAdapter = new NewMyListAdapter(aCursor, context, MyConstants.MODE_A_KIT);
        rvAftermarket.setAdapter(afterAdapter);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra("kit", kit);
                intent.putExtra(MyConstants.CATEGORY_TAB, tabToReturn);
                getActivity().startActivityForResult(intent, EDIT_ACTIVITY_CODE);
            }
        });
    }

    private void showSearchCard() { // TODO: 11.04.2018 переписать с kit

        String kitname = getArguments().getString(MyConstants.KITNAME);
        tvKitname.setText(kitname);

        String origName = getArguments().getString(MyConstants.ORIGINAL_NAME);
        tvOriginalKitName.setText(origName);

        String brand = getArguments().getString(MyConstants.BRAND);
        tvBrand.setText(brand);

        String catno = getArguments().getString(MyConstants.CATNO);
        tvBrandcatno.setText(catno);

        int scale = getArguments().getInt(MyConstants.SCALE);
        String scaleText = "1/" + String.valueOf(scale);
        tvScale.setText(scaleText);

        tvStatus.setVisibility(View.GONE);
        tvMedia.setText(codeToMedia(getArguments().getInt(MyConstants.MEDIA)));
        tvDesc.setText(codeToDescription(getArguments().getString(MyConstants.DESCRIPTION)));
        tvYear.setText(getArguments().getString(MyConstants.YEAR));
        tvShop.setVisibility(View.GONE);
        tvQuantity.setVisibility(View.GONE);
        tvDatePurchased.setVisibility(View.GONE);
        tvPrice.setVisibility(View.GONE);
        tvCurrency.setVisibility(View.GONE);
        tvAftermarketTitle.setVisibility(View.GONE);
        rvAftermarket.setVisibility(View.GONE);
        tvPurchaseTitle.setVisibility(View.GONE);
        tableLayoutPurchase.setVisibility(View.GONE);
        tvNotesTitle.setVisibility(View.GONE);
        tvNotes.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);


        tvScalematesUrl.setClickable(true);
        tvScalematesUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String scalemates = getArguments().getString(MyConstants.SCALEMATES);
        String scalematesText = "<a href='"
                + scalemates
                + "'> " + getString(R.string.Look_up_on_Scalemates) + "</a>";
        if (scalemates != null) {
            tvScalematesUrl.setText(Helper.fromHtml(scalematesText));
        } else {
            tvScalematesUrl.setText("");
        }
        LinearLayout llButton = view.findViewById(R.id.llButton);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 16);
        llButton.setLayoutParams(layoutParams);


        tvGoogleUrl.setClickable(true);
        tvGoogleUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String googleText = "<a href='https://www.google.com/search?"
                + "q=" + brand + "+" + catno + "+" + kitname + "+" + scale
                + "'> " + getString(R.string.Search_with_Google) + "</a>";
        tvGoogleUrl.setText(Helper.fromHtml(googleText));
        String url = getArguments().getString(MyConstants.BOXART_URL);

        Glide
                .with(context)
                .load(Helper.composeUrl(url))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivBoxart);

        setCategoryImage(category);
    }


    private void setCategoryImage(String category) {
        if (MyConstants.CODE_SEA.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_ship_black_24dp);
        }
        if (MyConstants.CODE_AIR.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_air_black_24dp);
        }
        if (MyConstants.CODE_GROUND.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_afv_black_24dp);
        }
        if (MyConstants.CODE_SPACE.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_space_black_24dp);
        }
        if (MyConstants.CODE_OTHER.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }
        if (MyConstants.CODE_AUTOMOTO.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_directions_car_black_24dp);
        }
        if (MyConstants.CODE_FIGURES.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_wc_black_24dp);
        }
        if (MyConstants.CODE_FANTASY.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_android_black_24dp);
        }
    }



    private String codeToDescription(String code) {
        String desc = "";
        switch (code) {
            case MyConstants.NEW_TOOL:
                desc = getResources().getString(R.string.new_tool);
                break;
            case MyConstants.REBOX:
                desc = getResources().getString(R.string.rebox);
                break;
        }
        return desc;
    }

    private String codeToMedia(int mediaCode) {
        String media;
        switch (mediaCode) {
            case MyConstants.M_CODE_UNKNOWN:
                media = getResources().getString(R.string.unknown);
                break;
            case MyConstants.M_CODE_INJECTED:
                media = getResources().getString(R.string.media_injected);
                break;
            case MyConstants.M_CODE_SHORTRUN:
                media = getResources().getString(R.string.media_shortrun);
                break;
            case MyConstants.M_CODE_RESIN:
                media = getResources().getString(R.string.media_resin);
                break;
            case MyConstants.M_CODE_VACU:
                media = getResources().getString(R.string.media_vacu);
                break;
            case MyConstants.M_CODE_PAPER:
                media = getResources().getString(R.string.media_paper);
                break;
            case MyConstants.M_CODE_WOOD:
                media = getResources().getString(R.string.media_wood);
                break;
            case MyConstants.M_CODE_METAL:
                media = getResources().getString(R.string.media_metal);
                break;
            case MyConstants.M_CODE_3DPRINT:
                media = getResources().getString(R.string.media_3dprint);
                break;
            case MyConstants.M_CODE_MULTIMEDIA:
                media = getResources().getString(R.string.media_multimedia);
                break;
            case MyConstants.M_CODE_OTHER:
                media = getResources().getString(R.string.media_other);
                break;
            case MyConstants.M_CODE_DECAL:
                media = getResources().getString(R.string.media_decal);
                break;
            case MyConstants.M_CODE_MASK:
                media = getResources().getString(R.string.media_mask);
                break;

            default:
                media = getResources().getString(R.string.unknown);
                break;
        }
        return media;
    }

    private String codeToStatus(int code) {
        String status;
        switch (code) {
            case MyConstants.STATUS_NEW:
                status = getResources().getString(R.string.status_new);
                break;
            case MyConstants.STATUS_OPENED:
                status = getResources().getString(R.string.status_opened);
                break;
            case MyConstants.STATUS_STARTED:
                status = getResources().getString(R.string.status_started);
                break;
            case MyConstants.STATUS_INPROGRESS:
                status = getResources().getString(R.string.status_inprogress);
                break;
            case MyConstants.STATUS_FINISHED:
                status = getResources().getString(R.string.status_finished);
                break;
            case MyConstants.STATUS_LOST:
                status = getResources().getString(R.string.status_lost_sold);
                break;
            default:
                status = getResources().getString(R.string.status_new);
                break;
        }
        return status;
    }
}
