package com.example.kitstasher.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import com.example.kitstasher.other.MyConstants;
import com.example.kitstasher.other.OnFragmentInteractionListener;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
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
import java.util.Locale;

import static com.example.kitstasher.activity.MainActivity.asyncService;

/**
 * Created by Алексей on 21.04.2017. Adding new items by Scanning
 */

public class ScanFragment extends Fragment implements AsyncApp42ServiceApi.App42StorageServiceListener {

    private String barcode,
            docId,
            brand,
            brand_catno,
            kit_name,
            kit_noeng_name,
            sendStatus, date,
            boxart_url,
            category,
            description,
            scalemates_page,
            boxart_uri,
            prototype,
            year,
            onlineId,
            listname,
            notes,
            purchaseDate,
            currency;
    private int status,
            scale,
            media,
            quantity,
            price;
    private long currentId;
    private boolean isReported,
            cloudModeOn;
    private DecoratedBarcodeView barcodeView;
    private ProgressBar progressBar;
    private DbConnector dbConnector;
    private OnFragmentInteractionListener mListener;
    public static String scanTag;
    private char workMode;
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
        barcode = MyConstants.EMPTY;
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
        mContext = getActivity();
        barcodeView = view.findViewById(R.id.barcode_view);
        progressBar = view.findViewById(R.id.pbScan);
        progressBar.setVisibility(View.GONE);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        barcode = MyConstants.EMPTY;
        checkMode();
        isReported = false;
        scanTag = this.getTag();
        sendStatus = MyConstants.EMPTY;
        status = MyConstants.STATUS_NEW;
        media = MyConstants.M_CODE_INJECTED;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        date = df.format(c.getTime());
        boxart_uri = MyConstants.EMPTY;
        notes = MyConstants.EMPTY;
        purchaseDate = MyConstants.EMPTY;
        quantity = 1;
        price = 0;
        currency = MyConstants.EMPTY;
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
        if (workMode == '\u0000') {
            workMode = MyConstants.MODE_KIT;
        } else {
            if (getArguments() != null) {
                workMode = getArguments().getChar(MyConstants.WORK_MODE);
                listname = getArguments().getString(MyConstants.LISTNAME);
            } else {
                workMode = MyConstants.MODE_KIT;
                listname = MyConstants.EMPTY;
            }
        }
    }

    private void openManualAdd() {
        if (workMode == MyConstants.MODE_LIST) {
            ManualAddFragment fragment = new ManualAddFragment();
            Bundle bundle = new Bundle(3);
            bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_LIST);
            bundle.putString(MyConstants.LISTNAME, listname);
            bundle.putString(MyConstants.BARCODE, barcode);
            fragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.llListsContainer, fragment);
            fragmentTransaction.commit();
        } else {
            mListener.onFragmentInteraction(barcode, workMode);
            ViewPager viewPager = getActivity().findViewById(R.id.viewpagerAdd);
            viewPager.setCurrentItem(1);
        }
        initiateScanner(getCallback());
    }

    public String getBarcode() {
        return barcode;
    }

    public char getWorkMode() {
        return workMode;
    }

    private String setDescription(String description) { // TODO: 22.02.2018 new kit & rebox
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
                    desc = MyConstants.EMPTY;
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
        return dbConnector.searchForDoubles(bc);
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
                    if (!result.getText().equals(barcode)) {
                        barcode = result.getResult().toString();
                        if (isInLocalBase(barcode)) {
                            Toast.makeText(getActivity(), getString(R.string.entry_already_exist),
                                    Toast.LENGTH_SHORT).show();
                            initiateScanner(getCallback());
                        } else {
                            searchCloud(barcode);
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

    private void saveToOnlineStash(Kit kitSave) {
        final ParseObject kitTowrite = new ParseObject(MyConstants.PARSE_C_STASH);
        kitTowrite.put(MyConstants.PARSE_BARCODE, kitSave.getBarcode());
        kitTowrite.put(MyConstants.PARSE_BRAND, kitSave.getBrand());
        kitTowrite.put(MyConstants.PARSE_BRAND_CATNO, kitSave.getBrandCatno());
        kitTowrite.put(MyConstants.PARSE_SCALE, kitSave.getScale());
        kitTowrite.put(MyConstants.PARSE_KITNAME, kitSave.getKit_name());
        kitTowrite.put(MyConstants.PARSE_NOENGNAME, kitSave.getKit_noeng_name());
        kitTowrite.put(MyConstants.PARSE_SCALEMATES, kitSave.getScalemates_url());

        kitTowrite.put(MyConstants.CATEGORY, kitSave.getCategory());
        if (!TextUtils.isEmpty(kitSave.getBoxart_url())) {
            kitTowrite.put(MyConstants.BOXART_URL, kitSave.getBoxart_url());
        }
        kitTowrite.put(MyConstants.PARSE_DESCRIPTION, kitSave.getDescription());
        kitTowrite.put(MyConstants.PARSE_ITEMTYPE, kitSave.getItemType());
        // TODO: 28.02.2018 проверить запись и поля
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        kitTowrite.put(MyConstants.PARSE_OWNERID, sharedPreferences.getString(MyConstants.USER_ID_PARSE, MyConstants.EMPTY));
        kitTowrite.put(MyConstants.YEAR, kitSave.getYear());
        kitTowrite.put(MyConstants.PARSE_LOCALID, kitSave.getLocalId());
        kitTowrite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                onlineId = kitTowrite.getObjectId();
                ContentValues cv = new ContentValues(1);
                cv.put(DbConnector.COLUMN_ID_ONLINE, onlineId);
                dbConnector.editItemById(DbConnector.TABLE_KITS, currentId, cv);
            }
        });
    }

    @Override
    public void onFindDocSuccess(Storage response) {
        final List<Kit> itemsForDb = new ArrayList<>();
        final List<Item> itemList = new ArrayList<>();
        progressBar.setVisibility(View.GONE);
        Item startItem = new Item(MyConstants.EMPTY, getString(R.string.add_another_variant));
        itemList.add(startItem);

        ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        for(int i = 0; i < jsonDocList.size(); i++)
        {
            String inputDoc = jsonDocList.get(i).getJsonDoc();
            JSONObject object;
            try {
                object = new JSONObject(inputDoc);
                year = object.getString(MyConstants.TAG_YEAR);
                kit_noeng_name = object.getString(MyConstants.TAG_NOENG_NAME);
                kit_name = object.getString(MyConstants.TAG_KIT_NAME);
                brand = object.getString(MyConstants.TAG_BRAND);
                brand_catno = object.getString(MyConstants.TAG_BRAND_CATNO);
                description = object.getString(MyConstants.TAG_DESCRIPTION);
                boxart_url = object.getString(MyConstants.TAG_BOXART_URL);
                category = String.valueOf(object.getInt(MyConstants.TAG_CATEGORY));
                scalemates_page = object.getString(MyConstants.TAG_SCALEMATES_PAGE);
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
                    .hasCategory(category)
                    .hasDescription(description)
                    .hasPrototype(prototype)//not in use
                    .hasKit_noeng_name(kit_noeng_name)
                    .hasBoxart_url(boxart_url)
                    .hasBoxart_uri(boxart_uri)
                    .hasBarcode(barcode)
                    .hasScalemates_url(scalemates_page)
                    .hasYear(year)
                    .hasOnlineId(onlineId)
                    .build();
            itemsForDb.add(kit);
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
                    Kit kitToAdd = itemsForDb.get(item - 1);
                    kitToAdd.setDate_added(date);
                    kitToAdd.setNotes(notes);
                    kitToAdd.setDatePurchased(purchaseDate);
                    kitToAdd.setQuantity(quantity);
                    kitToAdd.setPrice(price);
                    kitToAdd.setCurrency(currency);
                    kitToAdd.setSendStatus(sendStatus);
                    kitToAdd.setOnlineId(MyConstants.EMPTY);
                    kitToAdd.setBoxart_uri(MyConstants.EMPTY);
                    kitToAdd.setPlacePurchased(MyConstants.EMPTY);
                    kitToAdd.setScalemates_url(scalemates_page);
                    kitToAdd.setBarcode(barcode);
//                    kitToAdd.setBoxart_url(boxart_url);

                    kitToAdd.setStatus(status);
                    kitToAdd.setMedia(media);

//                    if (workMode == MyConstants.MODE_LIST) {
//                        dbConnector.addListItem(kitToAdd, listname);
//                        Toast.makeText(mContext, R.string.Kit_added_to_list, Toast.LENGTH_SHORT)
//                                .show();
//                        ListViewFragment listViewFragment = new ListViewFragment();
//                        Bundle bundle = new Bundle(1);
//                        bundle.putString(MyConstants.LISTNAME, listname);
//                        listViewFragment.setArguments(bundle);
//                        android.support.v4.app.FragmentTransaction fragmentTransaction =
//                                getFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.llListsContainer, listViewFragment);
//                        fragmentTransaction.commit();
//                    }else {
                        currentId = dbConnector.addKitRec(kitToAdd, DbConnector.TABLE_KITS);
                        kitToAdd.setLocalId(currentId);
                        kitToAdd.setItemType("1");

//                        dbConnector.updateCategories();

                        if (cloudModeOn && isOnline()) {
//                            onlineId = kitToAdd.saveToOnlineStash(getActivity());
//                            ContentValues cv = new ContentValues(1);
//                            cv.put(DbConnector.COLUMN_ID_ONLINE, onlineId);
//                            dbConnector.editItemById(DbConnector.TABLE_KITS, currentId, cv);
                            saveToOnlineStash(kitToAdd);
                        } else {
                            Toast.makeText(mContext, R.string.online_backup_is_off, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(mContext, R.string.kit_added, Toast.LENGTH_SHORT).show();
                    }
//                }

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
            docId = response.getJsonDocList().get(0).getDocId();
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
}
