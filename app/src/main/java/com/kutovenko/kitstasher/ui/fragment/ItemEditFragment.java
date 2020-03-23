package com.kutovenko.kitstasher.ui.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kutovenko.kitstasher.BuildConfig;
import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentItemEditBinding;
import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.ui.CropActivity;
import com.kutovenko.kitstasher.ui.MainActivity;
import com.kutovenko.kitstasher.ui.ViewActivity;
import com.kutovenko.kitstasher.ui.adapter.UiSpinnerAdapter;
import com.kutovenko.kitstasher.ui.adapter.UiSpinnerSupplyAdapter;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.kutovenko.kitstasher.ui.MainActivity.REQUEST_CODE_CROP;
import static java.lang.Integer.parseInt;

/**
 * Created by Алексей on 05.09.2017.
 * Universal item edit fragment for kits and aftermarket card.
 */

public class ItemEditFragment extends Fragment implements View.OnClickListener {
    private FragmentItemEditBinding binding;
    private Context context;
    private DbConnector dbConnector;
    private int position;
    private String purchaseDate;
    private String boxartUri;
    private String boxartUrl;
    private String mCurrentPhotoPath;
    private String workMode;
    private boolean isBoxartTemporary;
    private ArrayAdapter<String> descriptionAdapter, yearsAdapter, currencyAdapter;
    private StashItem stashItem;
    private StashItem editedStashItem;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_edit, container, false);

        context = getActivity();

        dbConnector = new DbConnector(context);
        dbConnector.open();

        initUI();

        stashItem = getArguments().getParcelable(MyConstants.KIT);

        workMode = getArguments().getString(MyConstants.ITEM_TYPE, MyConstants.TYPE_KIT);
        showEditForm(stashItem);


        if (savedInstanceState != null) {
            if (savedInstanceState.getString(MyConstants.BOXART_URI) != null) {
                mCurrentPhotoPath = savedInstanceState.getString("imagePath");
                if (mCurrentPhotoPath != null && !mCurrentPhotoPath.equals(MyConstants.EMPTY)) {
                    Glide
                            .with(context)
                            .load(new File(Uri.parse(mCurrentPhotoPath).getPath()))
                            .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                            .into(binding.ivEditBoxart);
                }
            }
        } else {

            String path = stashItem.getBoxartUri();
            if (path != null) {
                mCurrentPhotoPath = path;
            }
        }
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbConnector == null) dbConnector = new DbConnector(getActivity());
        dbConnector.open();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.kutovenko.kitstasher.R.id.ivEditBoxart:
                chooseImageAction();
                break;

            case com.kutovenko.kitstasher.R.id.btnEditSave:
                if (checkAllFields()) {
                    isBoxartTemporary = false;
                    buildEditedKit();
                    dbConnector.editItem(editedStashItem);
                    Intent intent3 = new Intent(context, ViewActivity.class);
                    intent3.putExtra(MyConstants.POSITION, position);
                    intent3.putExtra(MyConstants.CATEGORY, editedStashItem.getCategory());
                    intent3.putExtra(MyConstants.KIT, editedStashItem);
                    intent3.putExtra(MyConstants.ITEM_TYPE, workMode);
                    getActivity().setResult(RESULT_OK, intent3);
                    getActivity().finish();

//                    KitsFragment kitsFragment = new KitsFragment();
//                    Bundle bundleKit = new Bundle();
//
//                    bundleKit.putInt(MyConstants.POSITION, position);
//                    bundleKit.putString(MyConstants.CATEGORY, editedStashItem.getCategory());
//                    bundleKit.putParcelable(MyConstants.KIT, editedStashItem);
//                    bundleKit.putString(MyConstants.ITEM_TYPE, workMode);
//                    kitsFragment.setArguments(bundleKit);
//                    FragmentTransaction fragmentTransaction =
//                            getFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.frameLayoutEditContainer, kitsFragment);
//                    fragmentTransaction.commit();



                } else {
                    Toast.makeText(context, com.kutovenko.kitstasher.R.string.Please_enter_data, Toast.LENGTH_SHORT).show();
                }
                break;

            case com.kutovenko.kitstasher.R.id.tvEditPurchaseDate:
                DialogFragment newFragment = new SelectDateFragment();
                Bundle bundle = new Bundle(1);
                bundle.putString("caller", "ViewActivity");
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "DatePicker");
                break;

            case com.kutovenko.kitstasher.R.id.btnEditClearDate:
                purchaseDate = "";
                binding.tvEditPurchaseDate.setText(com.kutovenko.kitstasher.R.string.Date_not_set);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mCurrentPhotoPath = sharedPref.getString(MyConstants.FILE_URI, "");
        if (resultCode == RESULT_OK && requestCode == MainActivity.REQUEST_CODE_CAMERA) {
            Intent cropIntent = new Intent(getActivity(), CropActivity.class);
            cropIntent.putExtra(MyConstants.FILE_URI, mCurrentPhotoPath);
            startActivityForResult(cropIntent, REQUEST_CODE_CROP);
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.camera_failure, Toast.LENGTH_LONG).show();
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CROP) {
            File image = new File(mCurrentPhotoPath);
            Glide
                    .with(context)
                    .load(image)
                    .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                    .into(binding.ivEditBoxart);
            boxartUri = mCurrentPhotoPath;

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.crop_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imagePath", mCurrentPhotoPath);
        String outDate = binding.tvEditPurchaseDate.getText().toString();
        outState.putString("outDate", outDate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBoxartTemporary && mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty()) {
            File file = new File(mCurrentPhotoPath);
            file.deleteOnExit();
        }
        dbConnector.close();
    }

    private void showEditForm(StashItem stashItem) {
        boxartUrl = stashItem.getBoxartUrl();
        boxartUri = stashItem.getBoxartUri();
        String brand = stashItem.getBrand();
        binding.acEditBrand.setText(brand);
        String catno = stashItem.getBrandCatno();
        binding.etEditCatno.setText(catno);
        String kitname = stashItem.getName();
        binding.etEditName.setText(kitname);
        int scale = stashItem.getScale();
        binding.etEditScale.setText(String.valueOf(scale));
        String pr = String.valueOf(stashItem.getPrice() / 100);
        binding.etEditPrice.setText(pr);
        String category = stashItem.getCategory();
        String[] categories;
        int[] icons;
        if (workMode.equals(MyConstants.TYPE_KIT)) {
            categories = new String[]{
                    getString(com.kutovenko.kitstasher.R.string.other),
                    getString(com.kutovenko.kitstasher.R.string.Air),
                    getString(com.kutovenko.kitstasher.R.string.Ground),
                    getString(com.kutovenko.kitstasher.R.string.Sea),
                    getString(com.kutovenko.kitstasher.R.string.Space),
                    getString(com.kutovenko.kitstasher.R.string.Auto_moto),
                    getString(com.kutovenko.kitstasher.R.string.Figures),
                    getString(com.kutovenko.kitstasher.R.string.Fantasy)
            };
            icons = new int[]{
                    com.kutovenko.kitstasher.R.drawable.ic_check_box_outline_blank_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_tag_air_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_tag_afv_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_tag_ship_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_tag_space_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_directions_car_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_wc_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_android_black_24dp
            };
            UiSpinnerAdapter uiSpinnerAdapter = new UiSpinnerAdapter(context, icons, categories);
            binding.spEditCategory.setAdapter(uiSpinnerAdapter);
            binding.spEditCategory.setSelection(Integer.parseInt(category));
        } else if (workMode.equals(MyConstants.TYPE_AFTERMARKET)) {
            categories = new String[]{
                    getString(com.kutovenko.kitstasher.R.string.media_addon),
                    getString(com.kutovenko.kitstasher.R.string.media_photoetch),
                    getString(com.kutovenko.kitstasher.R.string.media_decal),
                    getString(com.kutovenko.kitstasher.R.string.media_mask),
                    getString(com.kutovenko.kitstasher.R.string.media_other),
            };
            UiSpinnerSupplyAdapter uiSpinnerAdapter = new UiSpinnerSupplyAdapter(context, categories);
            binding.spEditCategory.setAdapter(uiSpinnerAdapter);
            binding.spEditCategory.setSelection(setAddonCategoryToPosition(category));
        }

        String[] descriptionItems = new String[]{getString(com.kutovenko.kitstasher.R.string.unknown),
                getString(com.kutovenko.kitstasher.R.string.newkit),
                getString(com.kutovenko.kitstasher.R.string.rebox),
        };
        descriptionAdapter = new ArrayAdapter<>(context,
                com.kutovenko.kitstasher.R.layout.simple_spinner_item, descriptionItems);
        binding.spEditDescription.setAdapter(descriptionAdapter);
        binding.spEditDescription.setSelection(2);

        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add(getString(com.kutovenko.kitstasher.R.string.unknown));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<>(context,
                com.kutovenko.kitstasher.R.layout.simple_spinner_item, years);
        binding.spEditYear.setAdapter(yearsAdapter);

        String[] currencies = dbConnector.getCurrencies(DbConnector.TABLE_CURRENCIES,
                DbConnector.CURRENCIES_COLUMN_CURRENCY);

        currencyAdapter = new ArrayAdapter<>(context,
                com.kutovenko.kitstasher.R.layout.simple_spinner_item, currencies);
        binding.spEditCurrency.setAdapter(currencyAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, MyConstants.EMPTY);

        if (stashItem.getCurrency() != null
                && !stashItem.getCurrency().equals(MyConstants.EMPTY)) {
            String cr = stashItem.getCurrency();
            setKitCurrency(cr);
        } else {
            setKitCurrency(defaultCurrency);
        }

        Integer[] quants = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter quantityAdapter = new ArrayAdapter<>(context,
                com.kutovenko.kitstasher.R.layout.simple_spinner_item, quants);
        binding.spEditQuantity.setAdapter(quantityAdapter);

        String[] mediaTypes = new String[]{
                getString(com.kutovenko.kitstasher.R.string.media_other),
                getString(com.kutovenko.kitstasher.R.string.media_injected),
                getString(com.kutovenko.kitstasher.R.string.media_shortrun),
                getString(com.kutovenko.kitstasher.R.string.media_resin),
                getString(com.kutovenko.kitstasher.R.string.media_vacu),
                getString(com.kutovenko.kitstasher.R.string.media_paper),
                getString(com.kutovenko.kitstasher.R.string.media_wood),
                getString(com.kutovenko.kitstasher.R.string.media_metal),
                getString(com.kutovenko.kitstasher.R.string.media_3dprint),
                getString(com.kutovenko.kitstasher.R.string.media_multimedia),
                getString(com.kutovenko.kitstasher.R.string.media_decal),
                getString(com.kutovenko.kitstasher.R.string.media_mask)
        };
        ArrayAdapter mediaAdapter = new ArrayAdapter<>(context, com.kutovenko.kitstasher.R.layout.simple_spinner_item,
                mediaTypes);
        binding.spEditMedia.setAdapter(mediaAdapter);
        binding.spEditMedia.setSelection(Integer.parseInt(stashItem.getMedia()));



        if (workMode.equals(MyConstants.TYPE_KIT)) {
            String orName = stashItem.getNoengName();
            if (orName != null) {
                binding.etEditOrigName.setText(orName);
            }
        }
        int prc = stashItem.getPrice();
        if (prc != 0) {
            binding.etEditPrice.setText(String.valueOf(prc / 100));
        }else{
            binding.etEditPrice.setText(MyConstants.EMPTY);
        }
        String notes = stashItem.getNotes();
        binding.etEditNotes.setText(notes);

        String pd = stashItem.getDatePurchased();
        if (pd != null && !pd.equals("")) {
            binding.tvEditPurchaseDate.setText(pd);
        }else{
            binding.tvEditPurchaseDate.setText(com.kutovenko.kitstasher.R.string.Date_not_set);
        }

        String pPlace = stashItem.getPlacePurchased();
        if (pPlace != null) {
            binding.acEditShop.setText(pPlace);
        }

        String year = stashItem.getYear();
        if (year != null) {
            setKitYear(year);
        }

        String description = stashItem.getDescription();
        if (description != null) {
            setKitDescription(description);
        }
        setBoxartImage();
    }

    private int setAddonCategoryToPosition(String category) {
        switch (category){
            case MyConstants.M_CODE_ADDON:
                return 0;
            case MyConstants.M_CODE_PHOTOETCH:
                return 1;
            case MyConstants.M_CODE_DECAL:
                return 2;
            case MyConstants.M_CODE_MASK:
                return 3;
            case MyConstants.M_CODE_OTHER:
                return 4;
            default:
                return 4;
        }
    }

    private String getAddonCategoryFromPosition(int position) {
        switch (position) {
            case 0:
                return MyConstants.M_CODE_ADDON;
            case 1:
                return MyConstants.M_CODE_PHOTOETCH;
            case 2:
                return MyConstants.M_CODE_DECAL;
            case 3:
                return MyConstants.M_CODE_MASK;
            case 4:
                return MyConstants.M_CODE_OTHER;
            default:
                return MyConstants.M_CODE_OTHER;
        }
    }

    private void setKitDescription(String description) {
        if (!description.equals("")) {
            String desc = "0";
            if (!description.equals("")) {
                switch (description) {
                    case "0":
                        desc = "";
                        break;
                    case "1":
                        desc = getString(com.kutovenko.kitstasher.R.string.newkit);
                        break;
                    case "2":
                        desc = getString(com.kutovenko.kitstasher.R.string.rebox);
                        break;
                    case "3":
                        desc = getString(com.kutovenko.kitstasher.R.string.rebox);
                        break;
                    case "4":
                        desc = getString(com.kutovenko.kitstasher.R.string.rebox);
                        break;
                    case "5":
                        desc = getString(com.kutovenko.kitstasher.R.string.rebox);
                        break;
                    case "6":
                        desc = "";
                }
            }else{
                desc = getString(com.kutovenko.kitstasher.R.string.kittype);
            }
            int spDescPosition = descriptionAdapter.getPosition(desc);
            binding.spEditDescription.setSelection(spDescPosition);
        }else{
            binding.spEditDescription.setSelection(0);
        }
    }

    private void setKitYear(String year) {
        if (year.length() == 4 && !year.contains("-")) {
            int spYearPosition = yearsAdapter.getPosition(year);
            binding.spEditYear.setSelection(spYearPosition);
        }else{
            binding.spEditYear.setSelection(0); //оставляем на первой
        }
    }

    private void setKitCurrency(String currency) {
        int spCurrencyPosition = currencyAdapter.getPosition(currency);
        binding.spEditCurrency.setSelection(spCurrencyPosition);
    }

    private void setBoxartImage() {
        if (!Helper.isBlank(boxartUri)) {
            Glide
                    .with(context)
                    .load(boxartUri)//todo ури не приводится к абсолютному пути
                    .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                    .into(binding.ivEditBoxart);
        } else {
            Glide
                    .with(context)
                    .load(Helper.composeUrl(boxartUrl, MyConstants.BOXART_URL_LARGE))
                    .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                    .into(binding.ivEditBoxart);
        }
    }

    private void chooseImageAction() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(com.kutovenko.kitstasher.R.layout.alertdialog_imagemode, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(com.kutovenko.kitstasher.R.string.change_boxart);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final Button btnTakePicture = dialogView.findViewById(com.kutovenko.kitstasher.R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                alertDialog.dismiss();
            }
        });

        final Button btnClearPicture = dialogView.findViewById(com.kutovenko.kitstasher.R.id.btnClearPicture);
        btnClearPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Helper.isBlank(boxartUri)) {
                    File file = new File(boxartUri);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                boxartUri = MyConstants.EMPTY;
                mCurrentPhotoPath = MyConstants.EMPTY;
                ContentValues cvUri = new ContentValues(1);
                cvUri.put(DbConnector.COLUMN_BOXART_URI, MyConstants.EMPTY);
                dbConnector.editItemById(stashItem.getLocalId(), cvUri);

                setBoxartImage();

                alertDialog.dismiss();
            }
        });
    }

    private void buildEditedKit(){
        editedStashItem = stashItem;
        editedStashItem.setBrand(binding.acEditBrand.getText().toString().trim());
        editedStashItem.setName(binding.etEditName.getText().toString().trim());
        editedStashItem.setBrandCatno(binding.etEditCatno.getText().toString().trim());
        editedStashItem.setScale(parseInt(binding.etEditScale.getText().toString().trim()));
        editedStashItem.setPlacePurchased(binding.acEditShop.getText().toString().trim());
        String date = binding.tvEditPurchaseDate.getText().toString();
        if (!date.equals(getResources().getString(com.kutovenko.kitstasher.R.string.Date_not_set))) {
            purchaseDate = binding.tvEditPurchaseDate.getText().toString();
            editedStashItem.setDatePurchased(purchaseDate);
        }else{
            editedStashItem.setDatePurchased(MyConstants.EMPTY);
        }

        if (binding.etEditOrigName.getText() != null) {
            editedStashItem.setNoengName(binding.etEditOrigName.getText().toString().trim());
        }
        if (binding.etEditPrice.getText().toString().trim().equals("")){
            editedStashItem.setPrice(0);
        }else{
            int pr = Integer.parseInt(binding.etEditPrice.getText().toString().trim()) * 100;
            editedStashItem.setPrice(pr);
        }

        editedStashItem.setNotes(binding.etEditNotes.getText().toString().trim());

        if (mCurrentPhotoPath != null && !Helper.isBlank(mCurrentPhotoPath)) {
            editedStashItem.setBoxartUri(mCurrentPhotoPath);
        } else {
            editedStashItem.setBoxartUri(MyConstants.EMPTY);
        }

        if (workMode.equals(MyConstants.TYPE_KIT)){
            String cat = String.valueOf(binding.spEditCategory.getSelectedItemPosition());
            editedStashItem.setCategory(cat);
        }else{
            int position = binding.spEditCategory.getSelectedItemPosition();
            String cat = getAddonCategoryFromPosition(position);
            editedStashItem.setCategory(cat);
        }

        String y = binding.spEditYear.getSelectedItem().toString();
        if (!(y).equals(getResources().getString(com.kutovenko.kitstasher.R.string.unknown))) {
            editedStashItem.setYear(y);
        } else {
            editedStashItem.setYear(MyConstants.EMPTY);
        }

        String d = binding.spEditDescription.getSelectedItem().toString();
        editedStashItem.setDescription(descToCode(d));

        int quantity = binding.spEditCurrency.getSelectedItemPosition() + 1;
        editedStashItem.setQuantity(quantity);

        String curr = binding.spEditCurrency.getSelectedItem().toString();
        editedStashItem.setCurrency(curr);

        String purchasedFrom = binding.acEditShop.getText().toString().trim();
        editedStashItem.setPlacePurchased(purchasedFrom);
        if (!Helper.isBlank(purchasedFrom)){
            dbConnector.addShop(purchasedFrom);
        }

        String media = String.valueOf(binding.spEditMedia.getSelectedItemPosition());
        editedStashItem.setMedia(media);

        editedStashItem.setBoxartUrl(stashItem.getBoxartUrl());
    }

    /**
     *
     * @param d
     * @return
     */

    public String descToCode(String d) {
        String desc = "";
        if (d.equals(getString(com.kutovenko.kitstasher.R.string.unknown))) {
            desc = "0";
        }else if (d.equals(getString(com.kutovenko.kitstasher.R.string.newkit))){
            desc = "1";
        }else if (d.equals(getString(com.kutovenko.kitstasher.R.string.rebox))){
            desc = "2";
        }
        return desc;
    }

    private void dispatchTakePictureIntent() {
        Uri photoPath;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            if (Helper.getExternalStorageState() == Helper.StorageState.WRITEABLE) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        photoPath = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);
                    } else {
                        photoPath = Uri.fromFile(photoFile);
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
                    startActivityForResult(takePictureIntent, MainActivity.REQUEST_CODE_CAMERA);
                }
            }
        }
    }


    private File createImageFile() {
        isBoxartTemporary = true;
        String imageFileName = getTimestamp();
        File storageDir = getActivity().getExternalFilesDir(MyConstants.BOXART_DIRECTORY_NAME);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    MyConstants.JPG,/* suffix */
                    storageDir      /* directory */
            );

        } catch (IOException e) {
            Toast.makeText(context, com.kutovenko.kitstasher.R.string.cannot_create_file, Toast.LENGTH_SHORT).show();
        }
        if (image != null) {
            mCurrentPhotoPath = image.getAbsolutePath();
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MyConstants.FILE_URI, mCurrentPhotoPath);
            editor.apply();
        } else {
            Toast.makeText(context, com.kutovenko.kitstasher.R.string.cannot_create_file, Toast.LENGTH_LONG).show();
        }
        return image;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    private void initUI(){
        binding.ivEditBoxart.setOnClickListener(this);
        binding.btnEditSave.setOnClickListener(this);
        binding.btnEditClearDate.setOnClickListener(this);
        binding.tvEditPurchaseDate.setOnClickListener(this);
    }

    private boolean checkAllFields() {
        boolean check = true;
        if (TextUtils.isEmpty(binding.acEditBrand.getText())) {
            check = false;
        }
        if (TextUtils.isEmpty(binding.etEditCatno.getText())) {
            check = false;
        }
        if (TextUtils.isEmpty(binding.etEditScale.getText())
                || binding.etEditScale.getText().toString().equals("0")) {
            check = false;
        }
        if (TextUtils.isEmpty(binding.etEditName.getText())) {
            check = false;
        }
        return check;
    }
}