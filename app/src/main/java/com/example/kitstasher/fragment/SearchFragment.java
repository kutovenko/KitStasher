package com.example.kitstasher.fragment;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.AdapterAlertDialog;
import com.example.kitstasher.objects.Item;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
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
 * Created by Алексей on 03.09.2017.
 */

public class SearchFragment extends Fragment implements AsyncApp42ServiceApi.App42StorageServiceListener, TextWatcher {
    private Button btnCheck;
    private DecoratedBarcodeView brcView;
    private EditText etCatno;
    private AutoCompleteTextView acBrand;
    private DbConnector dbConnector;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 12;
    private String barcode;
    private ProgressDialog progressDialog;
    private Kit kitToShow;
    private ArrayAdapter acAdapterMybrands;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        btnCheck = (Button) view.findViewById(R.id.btnCheck);
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

        brcView = (DecoratedBarcodeView)view.findViewById(R.id.brcView);
        etCatno = (EditText)view.findViewById(R.id.etCatno);

        acBrand = (AutoCompleteTextView)view.findViewById(R.id.acBrand);


        dbConnector = new DbConnector(getActivity());
        dbConnector.open();

//        checkPermissions();
        kitToShow = new Kit.KitBuilder().build();

        ArrayList myBrands = DbConnector.getAllBrands();
        acAdapterMybrands = new ArrayAdapter<String>(getActivity(),
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
            acBrand.setError(getString(R.string.enter_brand));
            check = false;
        }
        if (TextUtils.isEmpty(etCatno.getText())) {
            etCatno.setError(getString(R.string.enter_cat_no));
            check = false;
        }
        return check;
    }

    private void clearFields(){
        acBrand.setText("");
        acBrand.setError("");
        etCatno.setText("");
        etCatno.setError("");

        kitToShow.setBrand("");
        kitToShow.setBrandCatno("");
        kitToShow.setKit_name("");
        kitToShow.setScale(0);
//                    kitToShow.setCategory(category)
        kitToShow.setDescription("");
        kitToShow.setPrototype("");//not in use
        kitToShow.setKit_noeng_name("");
        kitToShow.setBoxart_url("");
//                    kitToShow.setBoxart_uri(");
        kitToShow.setBarcode("");
        kitToShow.setScalemates_url("");//not in use
        kitToShow.setYear("");
        etCatno.setText("");
        acBrand.setText("");
    }

    private void searchKitOnline(Kit kitToSearch) {


        Query q1 = QueryBuilder.build(Constants.TAG_BRAND, kitToSearch.getBrand().trim(),
                QueryBuilder.Operator.EQUALS);
        Query q2 = QueryBuilder.build(Constants.TAG_BRAND_CATNO, kitToSearch.getBrandCatno().trim(),
                QueryBuilder.Operator.EQUALS);
        Query query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
        asyncService.findDocByQuery(Constants.App42DBName, Constants.CollectionName, query, this);
    }

//    private void checkPermissions() { // TODO: 03.09.2017 переместить в Helper
//        //checking for permissions on Marshmallow+
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.CAMERA)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.CAMERA},
//                        MY_PERMISSIONS_REQUEST_CAMERA);
//            }
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, //// TODO: 03.09.2017 Helper
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_CAMERA: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    initiateScanner(getCallback());
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(getActivity(),
//                            R.string.permission_denied_to_use_camera, Toast.LENGTH_SHORT).show();
//                    brcView.setVisibility(View.GONE);
//                }
////                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

    private void initiateScanner(BarcodeCallback callback) { // TODO: 03.09.2017 Helper
        IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
        scanIntegrator.setBeepEnabled(true);
        scanIntegrator.setOrientationLocked(false);
        brcView.decodeContinuous(callback);
    }

    private BarcodeCallback getCallback(){ // TODO: 03.09.2017 Helper
        return new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() == null || result.getText().equals(barcode)) {
                    // Prevent duplicate scans
                    return;
                }else{
                    barcode = result.getResult().toString();
                    //Check for doubles in local DB and then in cloud
                    if (isInLocalBase(barcode)){
                        Toast.makeText(getActivity(), getString(R.string.entry_already_exist),
                                Toast.LENGTH_SHORT).show();
                        getFromLocalBase(barcode);
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

    /*Checks if local database already includes this kit*/
    private boolean isInLocalBase(String barcode) {
        //Проверяем локально
        if (dbConnector.searchAllListsForFoubles(barcode, "", "")
                || dbConnector.searchForDoubles(barcode)) {
            return true;
        }
        return false;
    }
    private void getFromLocalBase(String barcode) {

    }
    private void searchCloud(String bc) {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.searching));
        progressDialog.setCancelable(true);
        Query query = QueryBuilder.build(Constants.TAG_BARCODE,
                bc.substring(0, bc.length() - 1), QueryBuilder.Operator.LIKE);
        asyncService.findDocByQuery(Constants.App42DBName, Constants.CollectionName, query, this);
    }

    private String setDescription(String description) {
        String desc = "";
        if (!description.equals("")) {
            switch (description) {
                case "0":
                    desc = "";
                    break;
                case "1":
                    desc = getString(R.string.new_tool);
                    break;

                case "2":
                    desc = getString(R.string.changed_parts);
                    break;
                case "3":
                    desc = getString(R.string.new_decal);
                    break;
                case "4":
                    desc = getString(R.string.changed_box);
                    break;
                case "5":
                    desc = getString(R.string.repack);
                    break;
                case "6":
                    desc = "";
            }
        }
    return desc;
    }

    @Override
    public void onFindDocSuccess(Storage response) {
        String showKit = "";
        final List<Kit> kitsForChoose = new ArrayList<Kit>();
        final List<Item> itemList = new ArrayList<Item>();

        //Найденный документ
//        progressDialog.dismiss();

        ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        for(int i = 0; i < jsonDocList.size(); i++)
        {
            String kit_noeng_name = "";
            String kit_name = "";
            String brand = "";
            String brand_catno = "";
            String description = "";
            String boxart_url = "";
            String scalemates_page = "";
            String prototype = "";
            String year = "";
            int scale = 0;

            //поля для демонстрации
            String inputDoc = jsonDocList.get(i).getJsonDoc();
            JSONObject object;
            try {
                object = new JSONObject(inputDoc);

                scalemates_page = object.getString(Constants.TAG_SCALEMATES_PAGE);
                year = object.getString(Constants.TAG_YEAR);
                kit_noeng_name = object.getString(Constants.TAG_NOENG_NAME);
                kit_name = object.getString(Constants.TAG_KIT_NAME);
                brand = object.getString(Constants.TAG_BRAND);
                brand_catno = object.getString(Constants.TAG_BRAND_CATNO);
                description = object.getString(Constants.TAG_DESCRIPTION);
//                description = getKitDescription(object.getString(Constants.TAG_DESCRIPTION));
                boxart_url = object.getString(Constants.TAG_BOXART_URL);
//                category = Helper.codeToTag(object.getString(Constants.TAG_CATEGORY));
                scale = Integer.valueOf(object.getString(Constants.TAG_SCALE));
                prototype = object.getString(Constants.TAG_PROTOTYPE);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            showKit = kit_name + " " + kit_noeng_name + " " + brand
                    + " " + brand_catno + " "
                    + "1/" + String.valueOf(scale) + " " + setDescription(description) + " " + year;
//                    + "-" + scalemates_page;
            Item item = new Item(boxart_url, showKit);
            itemList.add(item);

            Kit kit = new Kit.KitBuilder()
                    .hasBrand(brand)
                    .hasBrand_catno(brand_catno)
                    .hasKit_name(kit_name)
                    .hasScale(scale)
//                    .hasCategory(category)
                    .hasDescription(description)
                    .hasPrototype(prototype)//not in use
                    .hasKit_noeng_name(kit_noeng_name)
                    .hasBoxart_url(boxart_url)
//                    .hasBoxart_uri(boxart_uri)
                    .hasBarcode(barcode)
                    .hasScalemates_url(scalemates_page)//not in use
                    .hasYear(year)
//                    .hasOnlineId(onlineId)
                    .build();
            kitsForChoose.add(kit);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Found);

        AdapterAlertDialog adapterAlertDialog = new AdapterAlertDialog(getActivity(), itemList);
        builder.setAdapter(adapterAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
//                if (item != 0){
                    kitToShow = kitsForChoose.get(item);
                    Bundle bundle = new Bundle();
//                    bundle.putParcelable("Kit", kitToShow);
                    bundle.putString("kitname", kitToShow.getKit_name());
                    bundle.putString("brand", kitToShow.getBrand());
                    bundle.putString("catno", kitToShow.getBrandCatno());
                    bundle.putInt("scale", kitToShow.getScale());
                    bundle.putString("url", kitToShow.getBoxart_url());
                    bundle.putString("scalemates", kitToShow.getScalemates_url());

                ItemCardFragment itemCardFragment = new ItemCardFragment();
                itemCardFragment.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivityContainer, itemCardFragment);
                    fragmentTransaction.addToBackStack("search_fragment");
                    fragmentTransaction.commit();
//                }

                    clearFields();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onInsertionFailed(App42Exception ex) {

    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        progressDialog.dismiss();
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
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
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
        barcode = "";
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
