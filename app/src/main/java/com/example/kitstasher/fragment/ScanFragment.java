package com.example.kitstasher.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.UiAlertDialogAdapter;
import com.example.kitstasher.objects.Item;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.kitstasher.activity.MainActivity.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.example.kitstasher.activity.MainActivity.asyncService;

/**
 * Created by Алексей on 21.04.2017. Adding new items by Scanning
 */

public class ScanFragment extends Fragment implements AsyncApp42ServiceApi.App42StorageServiceListener {

    private String currentBarcode,
    //                docId,
//            brand,
//            brand_catno,
//            kit_name,
//            kit_noeng_name,
//            sendStatus, date,
//            boxart_url,
//            category,
//            description,
//            scalemates_page,
//            boxart_uri,
//            prototype,
//            year,
    onlineId;
    //            listname,
//            notes,
//            purchaseDate,
//            currency;
    private int status;
    //            scale,
//            media,
//            quantity,
//            price;
    private long currentId;
    private boolean isReported,
            cloudModeOn;
    private DecoratedBarcodeView barcodeView;
    private ProgressBar progressBar;
    private DbConnector dbConnector;
    private OnFragmentInteractionListener mListener;
    public static String scanTag;
    private String workMode;
    private Context mContext;

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
    public void onPause() {
        super.onPause();
        barcodeView.pauseAndWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
        initiateScanner(getCallback());
        currentBarcode = MyConstants.EMPTY;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        onAttachToParentFragment(getParentFragment());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_tabbed_scanning, container,
                false);

        checkCameraPermissions();

        mContext = getActivity();
        barcodeView = view.findViewById(R.id.barcode_view);
        progressBar = view.findViewById(R.id.pbScan);
        progressBar.setVisibility(View.GONE);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        currentBarcode = MyConstants.EMPTY;
        checkMode();
        isReported = false;
        scanTag = this.getTag();
//        sendStatus = MyConstants.EMPTY;
//        status = MyConstants.STATUS_NEW;
//        media = MyConstants.M_CODE_INJECTED;
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
//        date = df.format(c.getTime());
//        boxart_uri = MyConstants.EMPTY;
//        notes = MyConstants.EMPTY;
//        purchaseDate = MyConstants.EMPTY;
//        quantity = 1;
//        price = 0;
//        currency = MyConstants.EMPTY;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cloudModeOn = sharedPreferences.getBoolean(MyConstants.CLOUD_MODE, true);

        return view;
    }


    public void onAttachToParentFragment(Fragment fragment) {
        try {
            mListener = (OnFragmentInteractionListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnPlayerSelectionSetListener");
        }
    }

    private void checkMode() {
        if (getArguments() != null) {
            workMode = getArguments().getString(MyConstants.WORK_MODE);
        } else {
            workMode = MyConstants.TYPE_KIT;
        }
    }

    private void openManualAdd() {

        mListener.onFragmentInteraction(currentBarcode, workMode);
        ViewPager viewPager = getActivity().findViewById(R.id.viewpagerAdd);
        viewPager.setCurrentItem(1);
        initiateScanner(getCallback());
    }

    public String getBarcode() {
        return currentBarcode;
    }

    public String getWorkMode() {
        return workMode;
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
                default:
                    desc = getString(R.string.rebox);
                    break;
            }
        }
        return desc;
    }

    private boolean isInLocalBase(String barcode) {
        String bc = barcode.substring(0, barcode.length() - 1);
//        if (workMode == MyConstants.MODE_LIST) {
//            if (dbConnector.searchListForDoubles(listname, bc)) {
//                return true;
//            }
//        } else {
        return dbConnector.isItemDuplicate(bc);
//        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /* SCANNER */
    private void initiateScanner(BarcodeCallback callback) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
        scanIntegrator.setBeepEnabled(true);
        scanIntegrator.setOrientationLocked(false);
        barcodeView.decodeContinuous(callback);
    }

    private BarcodeCallback getCallback() {
        return new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null)
                    if (!result.getText().equals(currentBarcode)) {
                        currentBarcode = result.getResult().toString();
                        if (isInLocalBase(currentBarcode)) {
                            Toast.makeText(getActivity(), getString(R.string.entry_already_exist),
                                    Toast.LENGTH_SHORT).show();
                            initiateScanner(getCallback());
                        } else {
                            searchCloud(currentBarcode);
                        }

                    } else {
                        initiateScanner(getCallback());
                    }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        };
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

    /* CLOUD */
    private void searchCloud(String bc) {
        progressBar.setVisibility(View.VISIBLE);
        Query query = QueryBuilder.build(MyConstants.TAG_BARCODE,
                bc.substring(0, bc.length() - 1), QueryBuilder.Operator.LIKE);
        asyncService.findDocByQuery(MyConstants.App42DBName, MyConstants.CollectionName, query,
                this);
    }


    @Override
    public void onFindDocSuccess(Storage response) {


        final List<Kit> itemsForDb = new ArrayList<>();
        final List<Item> itemList = new ArrayList<>();

        progressBar.setVisibility(View.GONE);
        Item startItem = new Item(MyConstants.EMPTY, getString(R.string.add_another_variant));
        itemList.add(startItem);

        final ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        for(int i = 0; i < jsonDocList.size(); i++)
        {
            String inputDoc = jsonDocList.get(i).getJsonDoc();
            try {
                JSONObject object = new JSONObject(inputDoc);
                String boxartUrl = object.getString(MyConstants.TAG_BOXART_URL);
                String year = object.getString(MyConstants.TAG_YEAR);
                String kitNoengName = object.getString(MyConstants.TAG_NOENG_NAME);
                String kitName = object.getString(MyConstants.TAG_KIT_NAME);
                String brand = object.getString(MyConstants.TAG_BRAND);
                String brandCatno = object.getString(MyConstants.TAG_BRAND_CATNO);
                String description = object.getString(MyConstants.TAG_DESCRIPTION);
                String scale = object.getString(MyConstants.TAG_SCALE);

                String showKit = kitName + " " + kitNoengName + " " + brand
                        + " " + brandCatno + " "
                        + "1/" + scale + " " + setDescription(description) + " " + year;
                Item item = new Item(boxartUrl, showKit);
                itemList.add(item);

            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error in online record", Toast.LENGTH_SHORT).show();
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Found);
        UiAlertDialogAdapter uiAlertDialogAdapter = new UiAlertDialogAdapter(getActivity(), itemList);
        builder.setAdapter(uiAlertDialogAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (item == 0){
                    openManualAdd();
                }else {
                    try {
                        JSONObject outputJson = new JSONObject(jsonDocList.get(item - 1).getJsonDoc());
                        Kit kitToAdd = new Kit.KitBuilder()
                                .hasBrand(outputJson.getString(MyConstants.TAG_BRAND))
                                .hasBrand_catno(outputJson.getString(MyConstants.TAG_BRAND_CATNO))
                                .hasKitName(outputJson.getString(MyConstants.TAG_KIT_NAME))
                                .hasCategory(outputJson.getString(MyConstants.TAG_CATEGORY))
                                .hasDescription(outputJson.getString(MyConstants.TAG_DESCRIPTION))
                                .hasPrototype(outputJson.getString(MyConstants.TAG_PROTOTYPE))//not in use
                                .hasKitNoengName(outputJson.getString(MyConstants.TAG_NOENG_NAME))
                                .hasBoxartUrl(outputJson.getString(MyConstants.TAG_BOXART_URL))
                                .hasBarcode(outputJson.getString(MyConstants.TAG_BARCODE))
                                .hasScalematesUrl(outputJson.getString(MyConstants.TAG_SCALEMATES_PAGE))
                                .hasYear(outputJson.getString(MyConstants.TAG_YEAR))
                                .hasScale(
                                        outputJson.getInt(MyConstants.TAG_SCALE))
                                .hasDateAdded(Helper.getTodaysDate())
                                .hasNotes(MyConstants.EMPTY)
                                .hasDatePurchased(MyConstants.EMPTY)
                                .hasQuantity(1)
                                .hasPrice(0)
                                .hasCurrency(MyConstants.EMPTY)
                                .hasSendStatus(MyConstants.EMPTY)
                                .hasOnlineId(MyConstants.EMPTY)
                                .hasBoxartUri(MyConstants.EMPTY)
                                .hasPlacePurchased(MyConstants.EMPTY)

                                .hasMedia(
                                        outputJson.getInt(MyConstants.TAG_YEAR))
                                .hasItemType(MyConstants.TYPE_KIT)
                                .build();

//                        if (!kitToAdd.saveToLocalDb(getActivity())){
//                            Toast.makeText(mContext, R.string.cant_write_to_local_db, Toast.LENGTH_SHORT).show();
//                        }
                        currentId = dbConnector.addItem(kitToAdd, DbConnector.TABLE_KITS);
                        kitToAdd.setLocalId(currentId);
                        if (cloudModeOn && isOnline()) {
                            kitToAdd.setOnlineId(kitToAdd.saveToOnlineStash(getActivity()));

                        } else {
                            Toast.makeText(mContext, R.string.online_backup_is_off, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(mContext, R.string.kit_added, Toast.LENGTH_SHORT).show();
                    }catch (JSONException ex) {
                        Toast.makeText(getActivity(), "Error in online record", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        progressBar.setVisibility(View.GONE);

        openManualAdd();
    }

    @Override
    public void onDocumentInserted(Storage response) {
        progressBar.setVisibility(View.GONE);
        String getJson = response.getJsonDocList().get(0).getJsonDoc();
        try {
            JSONObject json = new JSONObject(getJson);
            createAlertDialog("Document Inserted with value: " + json.get("Name"));
            String docId = response.getJsonDocList().get(0).getDocId();
        } catch (JSONException ex) {
        }

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        progressBar.setVisibility(View.GONE);
        createAlertDialog(getString(R.string.Exception_Occured) + ex.getMessage());
    }

    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(
                mContext);
        alertbox.setTitle("Response Message");
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1)
            {
            }
        });
        alertbox.show();
    }

    @Override
    public void onUpdateDocSuccess(Storage response) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {
        progressBar.setVisibility(View.GONE);
        createAlertDialog("Exception Occurred : " + ex.getMessage());
    }


    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                Toast.makeText(getActivity(), R.string.we_cant_read_barcodes,
                        Toast.LENGTH_LONG).show();
                android.support.v4.app.Fragment fragment = NoPermissionFragment.newInstance(Manifest.permission.CAMERA, MyConstants.TYPE_PAINT);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }
}
