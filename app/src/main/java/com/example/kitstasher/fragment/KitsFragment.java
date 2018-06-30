package com.example.kitstasher.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.activity.ViewActivity;
import com.example.kitstasher.adapters.FragmentKitsAdapter;
import com.example.kitstasher.adapters.BottomSheetAdapter;
import com.example.kitstasher.objects.CategoryItem;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.example.kitstasher.other.OnPagerItemInteractionListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Алексей on 21.04.2018.
 */

public class KitsFragment extends Fragment
        implements FragmentKitsAdapter.FilterListener,
        OnPagerItemInteractionListener,
        BottomSheetAdapter.ActiveCategoriesListener {
    private Context context;
    private DbConnector dbConnector;
    @BindView(R.id.tvBarBrand) TextView tvBarBrand;
    @BindView(R.id.tvBarDate) TextView tvBarDate;
    @BindView(R.id.tvBarScale) TextView tvBarScale;
    @BindView(R.id.tvBarName) TextView tvBarName;
    @BindView(R.id.tvChoosedCategory) TextView tvChoosedCategory;
    private boolean isSortBrandAsc,
            isSortDateAsc,
            isSortScaleAsc,
            isSortNameAsc,
            isInAftermarketMode;
    private String workMode;
    private String currentTable,
            currentCategory,
            currentFilter;
    private final String NAME_ASC = "kit_name ASC";
    private final String NAME_DESC = "kit_name DESC";
    private final String DATE_ASC = "_id ASC";
    private final String DATE_DESC = "_id DESC";
    private final String BRAND_ASC = "brand ASC";
    private final String BRAND_DESC = "brand DESC";
    private final String SCALE_ASC = "scale ASC";
    private final String SCALE_DESC = "scale DESC";
    private final String CURRENT_FILTER = "currentFilter";
    private final String CURRENT_CATEGORY = "currentCategory";
    private final String SORT_DATE = "isSortDateAsc";
    private final String SORT_NAME = "isSortNameAsc";
    private final String SORT_SCALE = "isSortScaleAsc";
    private final String SORT_BRAND = "isSortBrandAsc";
    private final int MODE_KIT = 1;
    private final int MODE_AFTERMARKET = 2;
    private final int MODE_PAINT = 3;
    private ArrayList<Kit> sortedList;
    private ArrayList<Kit> forSort;
    private ArrayList<CategoryItem> activeCategories;
    private FragmentKitsAdapter rvAdapter;
    private BottomSheetAdapter.ActiveCategoriesListener listener;
    private Unbinder unbinder;

    public KitsFragment() {
    }

    public static KitsFragment newInstance() {
        return new KitsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        dbConnector = new DbConnector(context);
        dbConnector.open();
        if (getArguments() != null) {
            workMode = getArguments().getString(MyConstants.WORK_MODE);
            currentTable = workMode;
            activeCategories = dbConnector.getActiveCategories(workMode);
//            switch (workMode){
//                case MyConstants.TYPE_AFTERMARKET:
//                    currentTable = MyConstants.TYPE_AFTERMARKET;
//                    activeCategories = dbConnector.getAfterActiveCategories();
//                    break;
//                case MODE_KIT:
//                    currentTable = MyConstants.TYPE_KIT;
//                    activeCategories = dbConnector.getActiveCategories();
//                    break;
//                case MODE_PAINT:
//                    currentTable = MyConstants.TYPE_PAINT;
//            }
//
//
//            if (workMode == MODE_AFTERMARKET) {
//                currentTable = MyConstants.TYPE_AFTERMARKET;
//                activeCategories = dbConnector.getAfterActiveCategories();
//            }else if
//            else {
//                currentTable = MyConstants.TYPE_KIT;
//                activeCategories = dbConnector.getActiveCategories();
//            }
        }else{
            workMode = MyConstants.TYPE_KIT;
            currentTable = workMode;
            activeCategories = dbConnector.getActiveCategories(workMode);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnPagerItemInteractionListener mListener = this;
        listener = this;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FILTER, currentFilter);
        outState.putString(CURRENT_CATEGORY, currentCategory);
        outState.putBoolean(SORT_BRAND, isSortBrandAsc);
        outState.putBoolean(SORT_DATE, isSortDateAsc);
        outState.putBoolean(SORT_NAME, isSortNameAsc);
        outState.putBoolean(SORT_SCALE, isSortScaleAsc);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        dbConnector.close();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewstash, container, false);
        setHasOptionsMenu(true);


        context = getActivity();
        unbinder = ButterKnife.bind(this, view);
        dbConnector = new DbConnector(context);
        dbConnector.open();
        forSort = new ArrayList<>();
        currentFilter = savedInstanceState != null ? savedInstanceState.getString(CURRENT_FILTER, DATE_DESC) : DATE_DESC;
        currentCategory = savedInstanceState != null ? savedInstanceState.getString(CURRENT_CATEGORY, "") : "";
        isSortDateAsc = savedInstanceState != null && savedInstanceState.getBoolean(SORT_DATE, false);
        isSortNameAsc = savedInstanceState != null && savedInstanceState.getBoolean(SORT_NAME, true);
        isSortScaleAsc = savedInstanceState != null && savedInstanceState.getBoolean(SORT_SCALE, true);
        isSortBrandAsc = savedInstanceState != null && savedInstanceState.getBoolean(SORT_BRAND, true);

//        if (getArguments() != null) {
//            isInAftermarketMode = getArguments().getBoolean(MyConstants.WORK_MODE);
//            if (isInAftermarketMode) {
////            currentTable = DbConnector.TABLE_AFTERMARKET;
//                activeCategories = dbConnector.getAfterActiveCategories();
//                ((MainActivity) getActivity())
//                        .setActionBarTitle(getActivity().getResources().getString(R.string.aftermarket));
//                currentTable = MyConstants.TYPE_AFTERMARKET;
//            } else {
////            currentTable = currentTable;
//                currentTable = MyConstants.TYPE_KIT;
//                activeCategories = dbConnector.getActiveCategories();
//                ((MainActivity) getActivity())
//                        .setActionBarTitle(getActivity().getResources().getString(R.string.kits));
//            }
//        }else{
//            isInAftermarketMode = false;
//            currentTable = MyConstants.TYPE_KIT;
//            activeCategories = dbConnector.getActiveCategories();
//            ((MainActivity) getActivity())
//                    .setActionBarTitle(getActivity().getResources().getString(R.string.kits));
//        }



        if (getArguments() != null) {
            workMode = getArguments().getString(MyConstants.WORK_MODE);
            activeCategories = dbConnector.getActiveCategories(workMode);
            switch (workMode){
                case MyConstants.TYPE_AFTERMARKET:
                    ((MainActivity) getActivity())
                            .setActionBarTitle(getActivity().getResources().getString(R.string.aftermarket));
                    currentTable = MyConstants.TYPE_AFTERMARKET;
                    break;
                case MyConstants.TYPE_KIT:
                    ((MainActivity) getActivity())
                            .setActionBarTitle(getActivity().getResources().getString(R.string.kits));
                    currentTable = MyConstants.TYPE_KIT;
                    break;
                case MyConstants.TYPE_PAINT:
                    ((MainActivity) getActivity())
                            .setActionBarTitle(getActivity().getResources().getString(R.string.paints));
                    currentTable = MyConstants.TYPE_PAINT;
                    break;
            }
        }else{
            workMode = MyConstants.TYPE_KIT;
            currentTable = MyConstants.TYPE_KIT;
            activeCategories = dbConnector.getActiveCategories(workMode);
            ((MainActivity) getActivity())
                    .setActionBarTitle(getActivity().getResources().getString(R.string.kits));
        }



        tvChoosedCategory.setText(tagToCategoryName(currentCategory));
        sortedList = dbConnector.filteredKits(workMode, currentCategory, currentFilter);

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getFragmentManager() != null;
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString(MyConstants.WORK_MODE, workMode);
                switch (workMode){
                    case MyConstants.TYPE_KIT:

                        AddFragment addFragment = new AddFragment();
//                        bundle.putString(MyConstants.WORK_MODE, workMode);
//                        bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_KIT);
                        addFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.mainactivityContainer, addFragment);
                        fragmentTransaction.commit();
                        break;
                    case MyConstants.TYPE_AFTERMARKET:
                        ManualAddFragment aftermarketAddFragment = ManualAddFragment.newInstance();
//                        bundle.putBoolean(MyConstants.WORK_MODE, true);
//                        bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_AFTERMARKET);
                        aftermarketAddFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.mainactivityContainer, aftermarketAddFragment);
                        fragmentTransaction.commit();
                        break;
                    case MyConstants.TYPE_PAINT:
                        AddPaintFragment paintAddFragment = AddPaintFragment.newInstance();
//                        bundle.putBoolean(MyConstants.WORK_MODE, true);
//                        bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_AFTERMARKET);
                        paintAddFragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.mainactivityContainer, paintAddFragment);
                        fragmentTransaction.commit();
                        break;
                }
//                if (!isInAftermarketMode) {
//                    Bundle bundle = new Bundle();
//                    AddFragment addFragment = new AddFragment();
//                    bundle.putBoolean(MyConstants.WORK_MODE, false);
//                    bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_KIT);
//                    addFragment.setArguments(bundle);
//                    fragmentTransaction.replace(R.id.mainactivityContainer, addFragment);
//                    fragmentTransaction.commit();
//                } else {
//                    Bundle bundle = new Bundle();
//                    ManualAddFragment manualAddFragment = ManualAddFragment.newInstance();
//                    bundle.putBoolean(MyConstants.WORK_MODE, true);
//                    bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_AFTERMARKET);
//                    manualAddFragment.setArguments(bundle);
//                    fragmentTransaction.replace(R.id.mainactivityContainer, manualAddFragment);
//                    fragmentTransaction.commit();
//                }
            }
        });

        RecyclerView rvKits = view.findViewById(R.id.rvViewKits);
        LinearLayoutManager rvKitsManager = new LinearLayoutManager(getActivity());
        rvKits.setLayoutManager(rvKitsManager);
        rvKits.setItemAnimator(new DefaultItemAnimator());
        rvAdapter = new FragmentKitsAdapter(sortedList, context, this, this);
        rvKits.setAdapter(rvAdapter);
        rvKits.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                }else if(dy < 0 && fab.getVisibility() != View.VISIBLE){
                    fab.show();
                }
            }
        });

        View bottomSheet = view.findViewById(R.id.bottomsheet);
        final BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(false);
        final RecyclerView rvCategories = view.findViewById(R.id.rvActiveCategories);
        LinearLayoutManager rvCategoriesManager = new LinearLayoutManager(getActivity());
        rvCategories.setLayoutManager(rvCategoriesManager);
        rvCategories.setItemAnimator(new DefaultItemAnimator());
        BottomSheetAdapter bottomSheetAdapter = new BottomSheetAdapter(context, activeCategories, listener);
        rvCategories.setAdapter(bottomSheetAdapter);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    fab.hide();
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    fab.show();
                } else if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    fab.show();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        requestSort(currentFilter);

        return view;
    }

    private void requestSort(String filterMode) {
        switch (filterMode){
            case DATE_ASC:
                sortByDateAcs();
                break;
            case DATE_DESC:
                sortByDateDesc();
                break;
            case BRAND_ASC:
                sortByBrandAsc();
                break;
            case BRAND_DESC:
                sortByBrandDesc();
                break;
            case SCALE_ASC:
                sortByScaleAsc();
                break;
            case SCALE_DESC:
                sortByScaleDesc();
                break;
            case NAME_ASC:
                sortByNameAsc();
                break;
            case NAME_DESC:
                sortByNameDesc();
                break;
        }
    }

    @OnClick(R.id.tvBarDate)
    public void date() {
        if (!isSortDateAsc) {
            sortByDateAcs();
        } else {
            sortByDateDesc();
        }
    }

    @OnClick(R.id.tvBarName)
    public void name() {
        if (!isSortNameAsc) {
            sortByNameAsc();
        } else {
            sortByNameDesc();
        }
    }

    @OnClick(R.id.tvBarBrand)
    public void brand() {
        if (!isSortBrandAsc) {
            sortByBrandAsc();
        } else {
            sortByBrandDesc();
        }
    }

    @OnClick(R.id.tvBarScale)
    public void scale() {
        if (!isSortScaleAsc) {
            sortByScaleAsc();
        } else {
            sortByScaleDesc();
        }
    }


    private String tagToCategoryName(String tag) {
        String name;
        switch (tag) {
            case "1":
                name = context.getResources().getString(R.string.Air);
                break;
            case "2":
                name = context.getResources().getString(R.string.ground);
                break;
            case "3":
                name = context.getResources().getString(R.string.sea);
                break;
            case "4":
                name = context.getResources().getString(R.string.space);
                break;
            case "5":
                name = context.getResources().getString(R.string.cars_bikes);
                break;
            case "6":
                name = context.getResources().getString(R.string.Figures);
                break;
            case "7":
                name = context.getResources().getString(R.string.Fantasy);
                break;
            case "0":
                name = context.getResources().getString(R.string.other);
                break;
            default:
                name = context.getResources().getString(R.string.all);
                break;
        }
        return name;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rvAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                rvAdapter.getFilter().filter(query);
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_search || super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Kit kit, ArrayList<Kit> filteredItemList, int position) {
        Intent intent;
        intent = new Intent(context, ViewActivity.class);
        intent.putExtra(MyConstants.WORK_MODE, workMode);
        intent.putExtra(MyConstants.POSITION, position);//ид открытия пейджера
        intent.putExtra(MyConstants.CATEGORY_TAB, currentTable);
        intent.putParcelableArrayListExtra(MyConstants.LIST, filteredItemList);
        ((Activity) context).startActivityForResult(intent, MainActivity.REQUEST_CODE_VIEW);
    }


    @Override
    public void onPagerItemDelete(long itemId, int currentPosition, String uri, String onlineId) {
        if (!Helper.isBlank(uri)) {
            File file = new File(uri);
            if (file.exists()) {
                file.delete();
            }
        }
//        dbConnector.deleteAllAftermarketForKit(itemId);
        try {
            deleteFromOnlineStash(onlineId);
        } finally {
            dbConnector.delRec(DbConnector.TABLE_KITS, itemId);
        }
        rvAdapter.delete(currentPosition);
        sortedList.clear();
        sortedList.addAll(dbConnector.filteredKits(currentTable, currentCategory, "_id DESC"));
    }

    private void deleteFromOnlineStash(String onlineId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_STASH);
        query.getInBackground(onlineId, new GetCallback<ParseObject>() {
            public void done(ParseObject item, ParseException e) {
                if (e == null) {
                    item.put(MyConstants.PARSE_DELETED, true);
                    item.saveInBackground();
                }
            }
        });
    }

    @Override
    public void onCategorySelected(String category) {
        sortedList.clear();
        sortedList.addAll(dbConnector.filteredKits(currentTable, category, "_id DESC"));
        rvAdapter.notifyDataSetChanged();
        currentCategory = category;
        tvChoosedCategory.setText(tagToCategoryName(category));
        rvAdapter.notifyItemRangeChanged(0, sortedList.size());
    }

    private void sortByBrandAsc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<Kit>() {
            @Override
            public int compare(Kit k1, Kit k2) {
                return k1.getBrand().compareToIgnoreCase(k2.getBrand());
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(tvBarBrand);
        tvBarBrand.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, R.drawable.ic_keyboard_arrow_up_white_24dp));
        isSortBrandAsc = true;
        currentFilter = BRAND_ASC;
    }

    private void sortByBrandDesc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<Kit>() {
            @Override
            public int compare(Kit k1, Kit k2) {
                return k2.getBrand().compareToIgnoreCase(k1.getBrand());
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(tvBarBrand);
        tvBarBrand.setCompoundDrawablesWithIntrinsicBounds(null, null, null, Helper.getAPICompatVectorDrawable
                (context, R.drawable.ic_keyboard_arrow_down_white_24dp));
        isSortBrandAsc = false;
        currentFilter = BRAND_DESC;

    }

    private void sortByScaleAsc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<Kit>() {
            @Override
            public int compare(Kit k1, Kit k2) {
                Integer s1 = k1.getScale();
                Integer s2 = k2.getScale();
                return s1.compareTo(s2);
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(tvBarScale);
        tvBarScale.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, R.drawable.ic_keyboard_arrow_up_white_24dp));
        isSortScaleAsc = true;
        currentFilter = SCALE_ASC;
    }

    private void sortByScaleDesc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<Kit>() {
            @Override
            public int compare(Kit k1, Kit k2) {
                Integer s1 = k1.getScale();
                Integer s2 = k2.getScale();
                return s2.compareTo(s1);
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(tvBarScale);
        tvBarScale.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, R.drawable.ic_keyboard_arrow_down_white_24dp));
        isSortScaleAsc = false;
        currentFilter = SCALE_DESC;
    }

    private void sortByDateAcs() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<Kit>() {
            @Override
            public int compare(Kit k1, Kit k2) {
                Long s1 = k1.getLocalId();
                Long s2 = k2.getLocalId();
                return s1.compareTo(s2);
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(tvBarDate);
        tvBarDate.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, R.drawable.ic_keyboard_arrow_up_white_24dp));
        isSortDateAsc = true;
        currentFilter = DATE_ASC;
    }

    private void sortByDateDesc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<Kit>() {
            @Override
            public int compare(Kit k1, Kit k2) {
                Long s1 = k1.getLocalId();
                Long s2 = k2.getLocalId();
                return s2.compareTo(s1);
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(tvBarDate);
        tvBarDate.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, R.drawable.ic_keyboard_arrow_down_white_24dp));
        isSortDateAsc = false;
        currentFilter = DATE_DESC;
    }

    private void sortByNameAsc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<Kit>() {
            @Override
            public int compare(Kit k1, Kit k2) {
                return k1.getKit_name().compareToIgnoreCase(k2.getKit_name());
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(tvBarName);
        tvBarName.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, R.drawable.ic_keyboard_arrow_up_white_24dp));
        isSortNameAsc = true;
        currentFilter = NAME_ASC;
    }

    private void sortByNameDesc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<Kit>() {
            @Override
            public int compare(Kit k1, Kit k2) {
                return k2.getKit_name().compareToIgnoreCase(k1.getKit_name());
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(tvBarName);
        tvBarName.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, R.drawable.ic_keyboard_arrow_down_white_24dp));
        isSortNameAsc = false;
        currentFilter = NAME_DESC;
    }

    private void setActive(TextView item){
        setAllPassive();
        item.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
        item.setTextColor(Helper.getColor(getActivity(), R.color.colorItem));
    }

    private void setAllPassive() {
        tvBarName.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorItem));
        tvBarName.setTextColor(Helper.getColor(getActivity(), R.color.colorPassive));
        tvBarName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        tvBarDate.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorItem));
        tvBarDate.setTextColor(Helper.getColor(getActivity(), R.color.colorPassive));
        tvBarDate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        tvBarScale.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorItem));
        tvBarScale.setTextColor(Helper.getColor(getActivity(), R.color.colorPassive));
        tvBarScale.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        tvBarBrand.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorItem));
        tvBarBrand.setTextColor(Helper.getColor(getActivity(), R.color.colorPassive));
        tvBarBrand.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
}
