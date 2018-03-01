package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.AdapterAlertDialog;
import com.example.kitstasher.objects.Item;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.kitstasher.activity.MainActivity.asyncService;

/**
 * Created by Алексей on 03.09.2017. Search without adding to the local database
 */

public class SearchFragment extends Fragment implements AsyncApp42ServiceApi.App42StorageServiceListener, TextWatcher {
    private DecoratedBarcodeView brcView;
    private EditText etCatno;
    private AutoCompleteTextView acBrand;
    private String barcode;
    private ProgressBar pbProgress;
    private Kit kitToShow;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button btnCheck = view.findViewById(R.id.btnCheck);
        if (!isOnline()){
            btnCheck.setClickable(false);
            Toast.makeText(getActivity(), R.string.We_badly_need_internet, Toast.LENGTH_SHORT).show();
        }
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kitToShow.setBrand(acBrand.getText().toString().trim());
                kitToShow.setBrandCatno(etCatno.getText().toString().trim());
                if(checkSearchFields()) {
                    searchKitOnline(kitToShow);
                }
            }
        });

        brcView = view.findViewById(R.id.brcView);
        etCatno = view.findViewById(R.id.etCatno);
        acBrand = view.findViewById(R.id.acBrand);
        pbProgress = view.findViewById(R.id.pbSearchProgress);
        pbProgress.setVisibility(View.GONE);

        DbConnector dbConnector = new DbConnector(getActivity());
        dbConnector.open();

        kitToShow = new Kit.KitBuilder().build();

        ArrayList<String> myBrands = DbConnector.getAllBrands();
        ArrayAdapter<String> acAdapterMybrands = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, myBrands);
        acBrand.addTextChangedListener(this);
        acBrand.setAdapter(acAdapterMybrands);
        if (!isOnline()) {
            btnCheck.setClickable(false);
        }

        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(R.string.searching));

        return view;
    }


    private boolean checkSearchFields() {
        boolean check = true; //Если true, проверка пройдена, можно записывать
        if (TextUtils.isEmpty(acBrand.getText())) {
            check = false;
        }
        if (TextUtils.isEmpty(etCatno.getText())) {
            check = false;
        }
        return check;
    }

    private void clearFields(){
        acBrand.setText(MyConstants.EMPTY);
        etCatno.setText(MyConstants.EMPTY);

        kitToShow.setBrand(MyConstants.EMPTY);
        kitToShow.setBrandCatno(MyConstants.EMPTY);
        kitToShow.setKit_name(MyConstants.EMPTY);
        kitToShow.setScale(0);
        kitToShow.setDescription(MyConstants.EMPTY);
        kitToShow.setKit_noeng_name(MyConstants.EMPTY);
        kitToShow.setBoxart_url(MyConstants.EMPTY);
        kitToShow.setBarcode(MyConstants.EMPTY);
        kitToShow.setScalemates_url(MyConstants.EMPTY);
        kitToShow.setPrototype(MyConstants.EMPTY);
        kitToShow.setYear(MyConstants.EMPTY);
    }

    private void searchKitOnline(Kit kitToSearch) {
        Query q1 = QueryBuilder.build(MyConstants.TAG_BRAND, kitToSearch.getBrand().trim(),
                QueryBuilder.Operator.EQUALS);
        Query q2 = QueryBuilder.build(MyConstants.TAG_BRAND_CATNO, kitToSearch.getBrandCatno().trim(),
                QueryBuilder.Operator.EQUALS);
        Query query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
        asyncService.findDocByQuery(MyConstants.App42DBName, MyConstants.CollectionName, query, this);
    }

    private void initiateScanner(BarcodeCallback callback) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
        scanIntegrator.setBeepEnabled(true);
        scanIntegrator.setOrientationLocked(false);
        brcView.decodeSingle(callback);
    }

    private BarcodeCallback getCallback() {
        return new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null || !result.getText().equals(barcode)) {
                    barcode = result.getResult().toString();
                    if (!isOnline()) {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_connection),
                                Toast.LENGTH_SHORT).show();
                    }else {
                        searchCloud(barcode);
                    }
                }
            }
            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        };
    }

    private void searchCloud(String bc) {
        pbProgress.setVisibility(View.VISIBLE);
        Query query = QueryBuilder.build(MyConstants.TAG_BARCODE,
                bc.substring(0, bc.length() - 1), QueryBuilder.Operator.LIKE);
        asyncService.findDocByQuery(MyConstants.App42DBName, MyConstants.CollectionName, query, this);
    }

    private String setDescription(String description) {
        String desc = MyConstants.EMPTY;
        if (!description.equals(MyConstants.EMPTY)) {
            switch (description) {
                case "0":
                    desc = MyConstants.EMPTY;
                    break;
                case "1":
                    desc = getString(R.string.new_tool);
                    break;
                case "2":
                    desc = getString(R.string.repack);
                    break;
            }
        }
        return desc;
    }

    @Override
    public void onFindDocSuccess(Storage response) {
        final List<Kit> kitsForChoose = new ArrayList<>();
        final List<Item> itemList = new ArrayList<>();
        pbProgress.setVisibility(View.GONE);


        ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        for(int i = 0; i < jsonDocList.size(); i++)
        {
            String kit_noeng_name = MyConstants.EMPTY;
            String kit_name = MyConstants.EMPTY;
            String brand = MyConstants.EMPTY;
            String brand_catno = MyConstants.EMPTY;
            String description = MyConstants.EMPTY;
            String boxart_url = MyConstants.EMPTY;
            String scalemates_page = MyConstants.EMPTY;
            String prototype = MyConstants.EMPTY;
            String year = MyConstants.EMPTY;
            int scale = 0;

            //поля для демонстрации
            String inputDoc = jsonDocList.get(i).getJsonDoc();
            JSONObject object;
            try {
                object = new JSONObject(inputDoc);

                scalemates_page = object.getString(MyConstants.TAG_SCALEMATES_PAGE);
                year = object.getString(MyConstants.TAG_YEAR);
                kit_noeng_name = object.getString(MyConstants.TAG_NOENG_NAME);
                kit_name = object.getString(MyConstants.TAG_KIT_NAME);
                brand = object.getString(MyConstants.TAG_BRAND);
                brand_catno = object.getString(MyConstants.TAG_BRAND_CATNO);
                description = object.getString(MyConstants.TAG_DESCRIPTION);
                boxart_url = object.getString(MyConstants.TAG_BOXART_URL);
                scale = Integer.valueOf(object.getString(MyConstants.TAG_SCALE));
                prototype = object.getString(MyConstants.TAG_PROTOTYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String showKit = kit_name + " " + kit_noeng_name + " " + brand
                    + " " + brand_catno + " "
                    + "1/" + String.valueOf(scale) + " " + setDescription(description) + " " + year;
            Item item = new Item(boxart_url, showKit);
            itemList.add(item);

            Kit kit = new Kit.KitBuilder()
                    .hasBrand(brand)
                    .hasBrand_catno(brand_catno)
                    .hasKit_name(kit_name)
                    .hasScale(scale)
                    .hasDescription(description)
                    .hasPrototype(prototype)//not in use
                    .hasKit_noeng_name(kit_noeng_name)
                    .hasBoxart_url(boxart_url)
                    .hasBarcode(barcode)
                    .hasScalemates_url(scalemates_page)//not in use
                    .hasYear(year)
                    .build();
            kitsForChoose.add(kit);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Found);

        AdapterAlertDialog adapterAlertDialog = new AdapterAlertDialog(getActivity(), itemList);
        builder.setAdapter(adapterAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                kitToShow = kitsForChoose.get(item);
                Bundle bundle = new Bundle();
                bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_SEARCH);

                bundle.putString(MyConstants.LIST_CATEGORY, kitToShow.getCategory());
                bundle.putString(MyConstants.KITNAME, kitToShow.getKit_name());
                bundle.putString(MyConstants.BRAND, kitToShow.getBrand());
                bundle.putString(MyConstants.CATNO, kitToShow.getBrandCatno());
                bundle.putInt(MyConstants.SCALE, kitToShow.getScale());
                bundle.putString(MyConstants.BOXART_URL, kitToShow.getBoxart_url());
                bundle.putString(MyConstants.SCALEMATES, kitToShow.getScalemates_url());
                bundle.putString(MyConstants.CATEGORY, kitToShow.getCategory());
                bundle.putString(MyConstants.YEAR, kitToShow.getYear());
                bundle.putString(MyConstants.DESCRIPTION, kitToShow.getDescription());
                bundle.putString(MyConstants.ORIGINAL_NAME, kitToShow.getKit_noeng_name());
                bundle.putInt(MyConstants.MEDIA, kitToShow.getMedia());
                ItemCardFragment itemCardFragment = new ItemCardFragment();
                itemCardFragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivityContainer, itemCardFragment);
                fragmentTransaction.addToBackStack("search_fragment");
                fragmentTransaction.commit();
                clearFields();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {

    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        pbProgress.setVisibility(View.GONE);
        Toast.makeText(getActivity(), R.string.No_matches_try_manual, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {

    }
    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(brcView != null) {
            if (isVisibleToUser) {
                brcView.resume();
            } else {
                brcView.pauseAndWait();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onPause() {
        super.onPause();
        brcView.pauseAndWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        brcView.resume();
        initiateScanner(getCallback());
        barcode = MyConstants.EMPTY;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
