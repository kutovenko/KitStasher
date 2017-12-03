package com.example.kitstasher.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterAlertDialog;
import com.example.kitstasher.objects.Item;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.OnFragmentInteractionListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.kitstasher.activity.MainActivity.asyncService;

/**
 * Created by Алексей on 21.04.2017.
 */

public class ScanFragment extends Fragment implements AsyncApp42ServiceApi.App42StorageServiceListener {

    private String barcode, docId, brand, brand_catno, kit_name,
            kit_noeng_name, sendStatus, date, boxart_url, category, description, scalemates_page, boxart_uri,
            prototype, year, showKit, onlineId, listname;
    private int status, media;

    private int scale;
    boolean isReported;
    private ProgressDialog progressDialog;
    private TextView textView;
    DecoratedBarcodeView barcodeView;
    BarcodeCallback callback;
    DbConnector dbConnector;
    private OnFragmentInteractionListener mListener;
    public static String scanTag;
    private char mode;
    private Context mContext;

    private String notes, purchaseDate, currency;
    private int quantity;
    private int price;

    //for permission check
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 11;
//    private final int MY_PERMISSIONS_REQUEST_INTERNET = 12;
//    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 13;

    public ScanFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ScanFragment newInstance() {
        return new ScanFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
//        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        onAttachToParentFragment(getParentFragment());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabbed_scanning, container, false);
        mContext = getActivity();
        barcodeView = (DecoratedBarcodeView)view.findViewById(R.id.barcode_view);
        textView = (TextView)view.findViewById(R.id.textView);
//        constraintLayout = (ConstraintLayout)view.findViewById(R.id.clScanLayout);

        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        checkMode();
        isReported = false;
        scanTag = this.getTag();
        sendStatus = "";
        status = Constants.STATUS_NEW;
        media = Constants.M_CODE_INJECTED;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        date = df.format(c.getTime());
        boxart_uri = "";

        notes = "";
        purchaseDate = "";
        quantity = 1;
        price = 0;
        currency = "";

//        checkPermissions();
//        initiateScanner(getCallback());
        return view;
    }

//    private void checkPermissions() {
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
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_CAMERA: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    initiateScanner(getCallback());
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(getActivity(),
//                            R.string.permission_denied_to_use_camera, Toast.LENGTH_SHORT).show();
//                    barcodeView.setVisibility(View.GONE);
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }


    // Проверяем, откуда обратились к редактору
    private void checkMode() {
        if (getArguments() != null){
            listname = getArguments().getString("listname");
            if (getArguments().getChar("mode") == 'l'){
                mode = 'l';
            }else{
                mode = 'm';
            }
        }else{
            mode = 'm';
        }
    }

    private BarcodeCallback getCallback() {
        BarcodeCallback cb = new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() == null || result.getText().equals(barcode)) {
                    // Prevent duplicate scans
                    return;
                } else {
                    barcode = result.getResult().toString();
                    //Check for doubles in local DB and then in cloud
                    if (isInLocalBase(barcode)) {
                        Toast.makeText(getActivity(), getString(R.string.entry_already_exist),
                                Toast.LENGTH_SHORT).show();
                        initiateScanner(getCallback());
                    } else {
                        searchCloud(barcode);
                    }
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        };
        return cb;
    }

    private void initiateScanner(BarcodeCallback callback) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
        scanIntegrator.setBeepEnabled(true);
        scanIntegrator.setOrientationLocked(false);
        barcodeView.decodeContinuous(callback);
    }

    /*Checks if local database already includes this kit*/
    private boolean isInLocalBase(String barcode) {
        //Проверяем локально
        if (mode == 'l') {
            if (dbConnector.searchListForDoubles(listname, barcode)) {
                return true;
            }
        }else{
            if (dbConnector.searchForDoubles(barcode)) {
                return true;
            }
        }
        return false;
    }


    private void searchCloud(String bc) {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.searching));
        progressDialog.setCancelable(true);
// TODO: 14.07.2017 ошибка в баркоде java.lang.StringIndexOutOfBoundsException: length=8; regionStart=0; regionLength=12
        Query query = QueryBuilder.build(Constants.TAG_BARCODE,
                bc.substring(0, bc.length() - 1), QueryBuilder.Operator.LIKE);
        asyncService.findDocByQuery(Constants.App42DBName, Constants.CollectionName, query, this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(barcodeView != null) {
            if (isVisibleToUser) {
                barcodeView.resume();
            } else {
                barcodeView.pauseAndWait();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pauseAndWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
//        getCallback();
        initiateScanner(getCallback());
        barcode = "";
    }

    @Override
    public void onDocumentInserted(Storage response) {
        // TODO Auto-generated method stub
        progressDialog.dismiss();
        String getJson = response.getJsonDocList().get(0).getJsonDoc();
        try{
            JSONObject json = new JSONObject(getJson);
            createAlertDialog("Document Inserted for key 'Name' with value: "+ json.get("Name"));
            docId = response.getJsonDocList().get(0).getDocId();
        }catch(JSONException ex){

        }

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        // TODO Auto-generated method stub
        progressDialog.dismiss();
        createAlertDialog(getString(R.string.Exception_Occured)+ ex.getMessage());
    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        progressDialog.dismiss();
        openManualAdd();
    }

    private void openManualAdd() {
        if (mode == 'l'){
            ManualAddFragment fragment = new ManualAddFragment();
            Bundle bundle = new Bundle(3);
            bundle.putChar("mode", 'l');
            bundle.putString("listname", listname);
            bundle.putString("barcode", barcode);
            fragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.llListsContainer, fragment);
            fragmentTransaction.commit();
        }else {
            if (mListener != null) {
                mListener.onFragmentInteraction(barcode);
            }

            ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpagerAdd);
            viewPager.setCurrentItem(1);
        }

        initiateScanner(getCallback());
    }

    @Override
    public void onFindDocSuccess(Storage response) {

        showKit = "";
        final List<Kit> itemsForDb = new ArrayList<Kit>();
        final List<Item> itemList = new ArrayList<Item>();

        //НАйденный документ
        // TODO Auto-generated method stub
        progressDialog.dismiss();
        ///////
//        mItems.add("Add another variant"); //Add another version of the kit with scanned barcode
//        mBoxarts.add(""); //Empty boxart url for "Add new variant" line
/////
        Item startItem = new Item("", getString(R.string.add_another_variant));
        itemList.add(startItem);
        //////////////////
        ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        for(int i = 0; i < jsonDocList.size(); i++)
        {
            //поля для демонстрации
            String inputDoc = jsonDocList.get(i).getJsonDoc();
//            onlineId = jsonDocList.get(i).getDocId();

            JSONObject object;
            try {
                object = new JSONObject(inputDoc);
                year = object.getString(Constants.TAG_YEAR);
                kit_noeng_name = object.getString(Constants.TAG_NOENG_NAME);
                kit_name = object.getString(Constants.TAG_KIT_NAME);
                brand = object.getString(Constants.TAG_BRAND);
                brand_catno = object.getString(Constants.TAG_BRAND_CATNO);
                description = object.getString(Constants.TAG_DESCRIPTION);
//                description = getKitDescription(object.getString(Constants.TAG_DESCRIPTION));
                boxart_url = object.getString(Constants.TAG_BOXART_URL);
//                category = Helper.codeToTag(object.getString(Constants.TAG_CATEGORY));
                category = String.valueOf(object.getInt(Constants.TAG_CATEGORY));
//                category = object.getString(Constants.TAG_CATEGORY);

                scalemates_page = object.getString(Constants.TAG_SCALEMATES_PAGE);

                scale = Integer.valueOf(object.getString(Constants.TAG_SCALE)); //// TODO: 10.05.2017 подумать, когда преобразовывать
                prototype = object.getString(Constants.TAG_PROTOTYPE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            showKit = kit_name + " " + kit_noeng_name + " " + brand
                    + " " + brand_catno + " "
                    + "1/" + String.valueOf(scale) + " " + setDescription(description) + " " + year;
            Item item = new Item(boxart_url, showKit);
            itemList.add(item);

            Kit kit = new Kit.KitBuilder()
                    .hasBrand(brand)
                    .hasBrand_catno(brand_catno)
                    .hasKit_name(kit_name)
                    .hasScale(scale)
                    .hasCategory(category)
                    .hasDescription(description)
                    .hasPrototype(prototype)//not in use
                    .hasKit_noeng_name(kit_noeng_name)
                    .hasBoxart_url(boxart_url)
                    .hasBoxart_uri(boxart_uri)
                    .hasBarcode(barcode)
                    .hasScalemates_url(scalemates_page)//not in use
                    .hasYear(year)
//                    .hasOnlineId(onlineId)
                    .build();
            itemsForDb.add(kit);

        }

//        final String[] items = mItems.toArray(new String[mItems.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Found);
//////////////////////////////////////////
        AdapterAlertDialog adapterAlertDialog = new AdapterAlertDialog(getActivity(), itemList);
        builder.setAdapter(adapterAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (item == 0){
                    openManualAdd();
                }else {
                    Kit kitToAdd = itemsForDb.get(item - 1);// TODO: 11.07.2017 добавить год в базу
                    kitToAdd.setDate_added(date);
                    kitToAdd.setNotes(notes);
                    kitToAdd.setDatePurchased(purchaseDate);
                    kitToAdd.setQuantity(quantity);
                    kitToAdd.setPrice(price);
                    kitToAdd.setCurrency(currency);
                    kitToAdd.setSendStatus(sendStatus);
                    kitToAdd.setOnlineId("");
                    kitToAdd.setBoxart_uri("");
                    kitToAdd.setPlacePurchased("");
                    kitToAdd.setScalemates_url(scalemates_page);

                    kitToAdd.setStatus(status);
                    kitToAdd.setMedia(media);

                    if (mode == 'l'){

                        dbConnector.addListItem(kitToAdd, listname);

                        Toast.makeText(mContext, R.string.Kit_added_to_list, Toast.LENGTH_SHORT).show();
                        ListViewFragment listViewFragment = new ListViewFragment();
                        Bundle bundle = new Bundle(1);
                        bundle.putString("listname", listname);
                        listViewFragment.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.llListsContainer, listViewFragment);
                        fragmentTransaction.commit();

                    }else {
                        dbConnector.addKitRec(kitToAdd);
                        Toast.makeText(mContext, R.string.kit_added, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private String setDescription(String description) { // TODO: 03.09.2017 Helper
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
    public void onUpdateDocSuccess(Storage response) {
        // TODO Auto-generated method stub
        progressDialog.dismiss();
        createAlertDialog("Document SuccessFully Updated : "+ response.getJsonDocList().get(0).getJsonDoc());
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {
        // TODO Auto-generated method stub
        progressDialog.dismiss();
        createAlertDialog("Exception Occurred : "+ ex.getMessage());
    }

    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(
                getActivity());
        alertbox.setTitle("Response Message");
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1)
            {
            }
        });
        alertbox.show();
    }

    public String getBarcode(){ //?
        return barcode;
    }

//    private void writeToLocalDatabase() {
////        dbConnector.open();
////        dbConnector.addKitRec(barcode, brand, brand_catno, scale, kitname,
////                kit_noengname, sendStatus, date, boxart_url, category, "");
//    }

    public void onAttachToParentFragment(Fragment fragment)
    {
        try
        {
            mListener = (OnFragmentInteractionListener) fragment;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnPlayerSelectionSetListener");
        }
    }

}
