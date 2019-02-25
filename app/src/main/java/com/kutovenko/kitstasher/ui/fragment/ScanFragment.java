package com.kutovenko.kitstasher.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentTabbedScanningBinding;
import com.kutovenko.kitstasher.model.Item;
import com.kutovenko.kitstasher.network.AsyncApp42ServiceApi;
import com.kutovenko.kitstasher.ui.listener.OnFragmentInteractionListener;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.kutovenko.kitstasher.ui.adapter.UiAlertDialogAdapter;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.kutovenko.kitstasher.ui.MainActivity.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.kutovenko.kitstasher.ui.MainActivity.asyncService;

/**
 * Created by Алексей on 21.04.2017. Adding new items by Scanning
 */

public class ScanFragment extends Fragment implements AsyncApp42ServiceApi.App42StorageServiceListener {

    private String currentBarcode;
    private String ownerId;
    private DbConnector dbConnector;
    private OnFragmentInteractionListener mListener;
    public static String scanTag;
    private String workMode;
    private Context context;
    private FragmentTabbedScanningBinding binding;

    public ScanFragment() {
    }
    @Override
    public void onPause() {
        super.onPause();
        binding.barcodeview.pauseAndWait();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbConnector.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.barcodeview.resume();
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
        binding = FragmentTabbedScanningBinding.inflate(inflater, container, false);

        checkCameraPermissions();

        dbConnector = new DbConnector(getActivity());
        dbConnector.open();

        context = getActivity();

        binding.pbScan.setVisibility(View.GONE);

        currentBarcode = MyConstants.EMPTY;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ownerId = sharedPreferences.getString(MyConstants.USER_ID_PARSE, MyConstants.EMPTY);
        checkMode();
        scanTag = this.getTag();

        return binding.getRoot();
    }



    private void onAttachToParentFragment(Fragment fragment) {
        try {
            mListener = (OnFragmentInteractionListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    fragment.toString() + " must implement SetListener");
        }
    }

    private void checkMode() {
        if (getArguments() != null) {
            workMode = getArguments().getString(MyConstants.ITEM_TYPE);
        } else {
            workMode = MyConstants.TYPE_KIT;
        }
    }

    private void openManualAdd() {

        mListener.onFragmentInteraction(currentBarcode, workMode);
        ViewPager viewPager = getActivity().findViewById(com.kutovenko.kitstasher.R.id.viewpagerAdd);
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
                    break;
                case "1":
                    desc = getString(com.kutovenko.kitstasher.R.string.new_tool);
                    break;
                default:
                    desc = getString(com.kutovenko.kitstasher.R.string.rebox);
                    break;
            }
        }
        return desc;
    }

    private boolean isInLocalBase(String barcode) {
        String bc = barcode.substring(0, barcode.length() - 1);
        int res = dbConnector.isItemDuplicate(bc);
        return res > 0;
    }

    private void initiateScanner(BarcodeCallback callback) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
        scanIntegrator.setBeepEnabled(true);
        scanIntegrator.setOrientationLocked(false);
        binding.barcodeview.decodeContinuous(callback);
    }

    private BarcodeCallback getCallback() {
        return new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null)
                    if (!result.getText().equals(currentBarcode)) {
                        currentBarcode = result.getText();
                        if (isInLocalBase(currentBarcode)) {
                            Toast.makeText(getActivity(), getString(com.kutovenko.kitstasher.R.string.entry_already_exist),
                                    Toast.LENGTH_SHORT).show();
                            initiateScanner(getCallback());
                        } else {
                            if (Helper.isOnline(context)){
                                binding.pbScan.setVisibility(View.VISIBLE);
                                searchCloud(currentBarcode);
                                binding.pbScan.setVisibility(View.INVISIBLE);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.no_internet_connection),
                                        Toast.LENGTH_SHORT).show();
                                openManualAdd();
                            }
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


    private void searchCloud(String bc) {
        binding.pbScan.setVisibility(View.VISIBLE);
        Query query = QueryBuilder.build(MyConstants.TAG_BARCODE,
                bc.substring(0, bc.length() - 1), QueryBuilder.Operator.LIKE);
        asyncService.findDocByQuery(MyConstants.App42DBName, MyConstants.CollectionName, query,
                this);
    }


    @Override
    public void onFindDocSuccess(Storage response) {
        binding.pbScan.setVisibility(View.GONE);
        Item startItem = new Item(MyConstants.EMPTY, getString(com.kutovenko.kitstasher.R.string.add_another_variant));
        final List<Item> itemList = new ArrayList<>();
        itemList.add(startItem);

        final ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        for(int i = 0; i < jsonDocList.size(); i++)
        {
            String inputDoc = jsonDocList.get(i).getJsonDoc();
            try {
                JSONObject object = new JSONObject(inputDoc);
                String boxartUrl = object.getString(MyConstants.TAG_BOXART_URL);
                String year = object.getString(MyConstants.TAG_YEAR);
                String kitName = object.getString(MyConstants.TAG_KIT_NAME);
                String brand = object.getString(MyConstants.TAG_BRAND);
                String brandCatno = object.getString(MyConstants.TAG_BRAND_CATNO);
                String description = object.getString(MyConstants.TAG_DESCRIPTION);
                String scale = object.getString(MyConstants.TAG_SCALE);


                String showKit = kitName + " " + brand
                        + " " + brandCatno + " "
                        + "1/" + scale + " "
                        + setDescription(description) + " "
                        + year;
                Item item = new Item(boxartUrl, showKit);
                itemList.add(item);

            } catch (JSONException e) {
                Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.error_in_online_record, Toast.LENGTH_SHORT).show();
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(com.kutovenko.kitstasher.R.string.Found);
        UiAlertDialogAdapter uiAlertDialogAdapter = new UiAlertDialogAdapter(getActivity(), itemList);
        builder.setAdapter(uiAlertDialogAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (item == 0){
                    openManualAdd();
                }else {
                    StashItem stashItemToAdd = new StashItem.StashItemBuilder(MyConstants.TYPE_KIT)
                            .hasDateAdded(Helper.getTodaysDate())
                            .hasKitNoengName(MyConstants.EMPTY)
                            .hasNotes(MyConstants.EMPTY)
                            .hasDatePurchased(MyConstants.EMPTY)
                            .hasCurrency(MyConstants.EMPTY)
                            .hasSendStatus(MyConstants.EMPTY)
                            .hasBoxartUrl(MyConstants.EMPTY)
                            .hasPlacePurchased(MyConstants.EMPTY)
                            .hasQuantity(1)
                            .hasPrice(0)
                            .build();
                    String brand = MyConstants.EMPTY;
                    String catno = MyConstants.EMPTY;
                    String name = MyConstants.EMPTY;
                    String category = MyConstants.EMPTY;
                    String description = MyConstants.EMPTY;
                    String prototype = MyConstants.EMPTY;//not in use
                    String boxartUrl = MyConstants.EMPTY;
                    String barcode = MyConstants.EMPTY;
                    String scalematesPage = MyConstants.EMPTY;
                    String year = MyConstants.EMPTY;
                    String media = MyConstants.EMPTY;
                    String onlineId = MyConstants.EMPTY;
                    int scale = 0;

                    try {
                        JSONObject outputJson = new JSONObject(jsonDocList.get(item - 1).getJsonDoc());
                        brand = Helper.nullCheck(outputJson.getString(MyConstants.TAG_BRAND));
                        catno = Helper.nullCheck(outputJson.getString(MyConstants.TAG_BRAND_CATNO));
                        name = Helper.nullCheck(outputJson.getString(MyConstants.TAG_KIT_NAME));
                        category = Helper.nullCheck(outputJson.getString(MyConstants.TAG_CATEGORY));
                        description = Helper.nullCheck(outputJson.getString(MyConstants.TAG_DESCRIPTION));
                        prototype = Helper.nullCheck(outputJson.getString(MyConstants.TAG_PROTOTYPE));//not in use
                        boxartUrl = Helper.nullCheck(outputJson.getString(MyConstants.TAG_BOXART_URL));
                        barcode = Helper.nullCheck(outputJson.getString(MyConstants.TAG_BARCODE));
                        scalematesPage = Helper.nullCheck(outputJson.getString(MyConstants.TAG_SCALEMATES_PAGE));
                        year = Helper.nullCheck(outputJson.getString(MyConstants.TAG_YEAR));
                        media = Helper.nullCheck(outputJson.getString(MyConstants.TAG_MEDIA), MyConstants.M_CODE_UNKNOWN);
                        scale = Integer.parseInt(Helper.nullCheck(outputJson.getString(MyConstants.TAG_SCALE), "0"));
                    }catch (JSONException ex) {
                    }
                    stashItemToAdd.setBrand(brand);
                    stashItemToAdd.setBrandCatno(catno);
                    stashItemToAdd.setName(name);
                    stashItemToAdd.setCategory(category);
                    stashItemToAdd.setDescription(description);
                    stashItemToAdd.setPrototype(prototype);
                    stashItemToAdd.setBoxartUrl(boxartUrl);
                    stashItemToAdd.setBarcode(barcode);
                    stashItemToAdd.setScalematesUrl(scalematesPage);
                    stashItemToAdd.setYear(year);
                    stashItemToAdd.setMedia(media);
                    stashItemToAdd.setScale(scale);

                    stashItemToAdd.saveToLocalStash(dbConnector);
                    if (Helper.isOnline(context)){
                        stashItemToAdd.saveOnlineAfterScan(ownerId);
                    }
                    Toast.makeText(context, com.kutovenko.kitstasher.R.string.kit_added, Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        binding.pbScan.setVisibility(View.GONE);

        openManualAdd();
    }

    @Override
    public void onDocumentInserted(Storage response) {
        binding.pbScan.setVisibility(View.GONE);
        String getJson = response.getJsonDocList().get(0).getJsonDoc();
        try {
            JSONObject json = new JSONObject(getJson);
            createAlertDialog("Document Inserted with value: " + json.get("Name"));
        } catch (JSONException ex) {
        }

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        binding.pbScan.setVisibility(View.GONE);
        createAlertDialog(getString(com.kutovenko.kitstasher.R.string.Exception_Occured) + ex.getMessage());
    }

    private void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(
                context);
        alertbox.setTitle(getString(R.string.responce_message));
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1){
            }
        });
        alertbox.show();
    }

    @Override
    public void onUpdateDocSuccess(Storage response) {
        binding.pbScan.setVisibility(View.GONE);
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {
        binding.pbScan.setVisibility(View.GONE);
        createAlertDialog("Exception Occurred : " + ex.getMessage());
    }


    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.we_cant_read_barcodes,
                        Toast.LENGTH_LONG).show();
                Fragment fragment = NoPermissionFragment.newInstance(Manifest.permission.CAMERA, MyConstants.TYPE_SUPPLY);
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }
}
