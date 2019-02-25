package com.kutovenko.kitstasher.ui.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.ui.MainActivity;
import com.kutovenko.kitstasher.ui.ViewActivity;
import com.kutovenko.kitstasher.ui.adapter.BottomSheetAdapter;
import com.kutovenko.kitstasher.ui.adapter.FragmentKitsAdapter;
import com.kutovenko.kitstasher.databinding.FragmentViewstashBinding;
import com.kutovenko.kitstasher.ui.listener.OnPagerItemInteractionListener;
import com.kutovenko.kitstasher.model.CategoryItem;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by Алексей on 21.04.2018.
 */

public class KitsFragment extends Fragment implements FragmentKitsAdapter.FilterListener,
        OnPagerItemInteractionListener,
        BottomSheetAdapter.ActiveCategoriesListener, View.OnClickListener {
    private FragmentViewstashBinding binding;
    private Context context;
    private DbConnector dbConnector;
    private ArrayList<StashItem> sortedList;
    private ArrayList<StashItem> forSort;
    private ArrayList<CategoryItem> activeCategories;
    private BottomSheetAdapter bottomSheetAdapter;
    private FragmentKitsAdapter rvAdapter;
    private BottomSheetAdapter.ActiveCategoriesListener activeCategoriesListener;
    private boolean isSortBrandAsc;
    private boolean isSortDateAsc;
    private boolean isSortScaleAsc;
    private boolean isSortNameAsc;

    private boolean searchOnlyInSection;

    private String workMode;
    private String currentCategory;
    private String currentSortOrder;
    private int position;
    private final String NAME_ASC = "kit_name ASC";
    private final String NAME_DESC = "kit_name DESC";
    private final String DATE_ASC = "_id ASC";
    private final String DATE_DESC = "_id DESC";
    private final String BRAND_ASC = "brand ASC";
    private final String BRAND_DESC = "brand DESC";
    private final String SCALE_ASC = "scale ASC";
    private final String SCALE_DESC = "scale DESC";
    private final String CURRENT_FILTER = "currentSortOrder";
    private final String CURRENT_CATEGORY = "currentCategory";
    private final String SORT_DATE = "isSortDateAsc";
    private final String SORT_NAME = "isSortNameAsc";
    private final String SORT_SCALE = "isSortScaleAsc";
    private final String SORT_BRAND = "isSortBrandAsc";

    public KitsFragment() {
    }

    public static KitsFragment newInstance() {
        return new KitsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        context = getActivity();
        dbConnector = new DbConnector(context);
        dbConnector.open();
        if (getArguments() != null) {
            workMode = getArguments().getString(MyConstants.ITEM_TYPE);
            currentCategory = getArguments().getString(MyConstants.CATEGORY);
            if (currentCategory == null) currentCategory = MyConstants.CAT_ALL;
            position = getArguments().getInt(MyConstants.POSITION);
        }else{
            if (workMode == null) workMode = MyConstants.TYPE_KIT;
        }
        if (!workMode.equals(MyConstants.TYPE_ALL)){
        activeCategories = dbConnector.getActiveCategories(workMode);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnPagerItemInteractionListener mListener = this;
        activeCategoriesListener = this;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FILTER, currentSortOrder);
        outState.putString(CURRENT_CATEGORY, currentCategory);
        outState.putBoolean(SORT_BRAND, isSortBrandAsc);
        outState.putBoolean(SORT_DATE, isSortDateAsc);
        outState.putBoolean(SORT_NAME, isSortNameAsc);
        if (!workMode.equals(MyConstants.TYPE_SUPPLY)){
            outState.putBoolean(SORT_SCALE, isSortScaleAsc);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbConnector.close();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_viewstash, container, false);

        context = getActivity();

        dbConnector = new DbConnector(context);
        dbConnector.open();

        forSort = new ArrayList<>();
        currentSortOrder = savedInstanceState != null ? savedInstanceState.getString(CURRENT_FILTER, DATE_DESC) : DATE_DESC;
        currentCategory = savedInstanceState != null ? savedInstanceState.getString(CURRENT_CATEGORY, "") : "";
        isSortDateAsc = savedInstanceState != null && savedInstanceState.getBoolean(SORT_DATE, false);
        isSortNameAsc = savedInstanceState != null && savedInstanceState.getBoolean(SORT_NAME, true);
        isSortScaleAsc = savedInstanceState != null && savedInstanceState.getBoolean(SORT_SCALE, true);
        isSortBrandAsc = savedInstanceState != null && savedInstanceState.getBoolean(SORT_BRAND, true);
        binding.tvBarBrand.setOnClickListener(this);
        binding.tvBarDate.setOnClickListener(this);
        binding.tvBarName.setOnClickListener(this);
        binding.tvBarScale.setOnClickListener(this);


        if (getArguments() != null) {
            workMode = getArguments().getString(MyConstants.ITEM_TYPE);
            currentCategory = getArguments().getString(MyConstants.CATEGORY);
            if (currentCategory == null) currentCategory = MyConstants.CAT_ALL;
            if (!workMode.equals(MyConstants.TYPE_ALL)) {
                activeCategories = dbConnector.getActiveCategories(workMode);
            }
            position = getArguments().getInt(MyConstants.POSITION);
            switch (workMode){
                case MyConstants.TYPE_AFTERMARKET:
                    ((MainActivity) getActivity())
                            .setActionBarTitle(getActivity().getResources().getString(com.kutovenko.kitstasher.R.string.aftermarket));
                    break;
                case MyConstants.TYPE_KIT:
                    ((MainActivity) getActivity())
                            .setActionBarTitle(getActivity().getResources().getString(com.kutovenko.kitstasher.R.string.kits));
                    break;
                case MyConstants.TYPE_SUPPLY:
                    ((MainActivity) getActivity())
                            .setActionBarTitle(getActivity().getResources().getString(com.kutovenko.kitstasher.R.string.paints));
                    binding.tvBarScale.setVisibility(View.GONE);
                    break;
            }
        }else{
            workMode = MyConstants.TYPE_KIT;
            currentCategory = MyConstants.CAT_ALL;
            activeCategories = dbConnector.getActiveCategories(workMode);
            ((MainActivity) getActivity())
                    .setActionBarTitle(getActivity().getResources().getString(com.kutovenko.kitstasher.R.string.kits));
        }

        if (currentCategory.equals(MyConstants.CAT_ALL)){
            binding.setChoosedCategory(getString(R.string.all));
        } else {
            binding.setChoosedCategory(currentCategory);
        }

        sortedList = dbConnector.filteredKits(workMode, currentCategory, currentSortOrder);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getFragmentManager() != null;
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString(MyConstants.ITEM_TYPE, workMode);
                switch (workMode){
                    case MyConstants.TYPE_KIT:
                        AddFragment addFragment = AddFragment.newInstance();
                        addFragment.setArguments(bundle);
                        fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, addFragment);
                        fragmentTransaction.commit();
                        break;
                    case MyConstants.TYPE_AFTERMARKET:
                        ManualAddFragment aftermarketAddFragment = ManualAddFragment.newInstance();
                        aftermarketAddFragment.setArguments(bundle);
                        fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, aftermarketAddFragment);
                        fragmentTransaction.commit();
                        break;
                    case MyConstants.TYPE_SUPPLY:
                        AddSupplyFragment paintAddFragment = AddSupplyFragment.newInstance();
                        paintAddFragment.setArguments(bundle);
                        fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, paintAddFragment);
                        fragmentTransaction.commit();
                        break;
                }
            }
        });

        LinearLayoutManager rvKitsManager = new LinearLayoutManager(getActivity());
        binding.rvKits.setLayoutManager(rvKitsManager);
        binding.rvKits.setItemAnimator(new DefaultItemAnimator());
        rvAdapter = new FragmentKitsAdapter(sortedList, workMode, context, this, this);
        binding.rvKits.setAdapter(rvAdapter);
        binding.rvKits.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && binding.fab.getVisibility() == View.VISIBLE) {
                    binding.fab.hide();
                }else if(dy < 0 && binding.fab.getVisibility() != View.VISIBLE){
                    binding.fab.show();
                }
            }
        });


        final BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomsheet);
        mBottomSheetBehavior.setHideable(false);
        LinearLayoutManager rvCategoriesManager = new LinearLayoutManager(getActivity());
        binding.rvActiveCategories.setLayoutManager(rvCategoriesManager);
        binding.rvActiveCategories.setItemAnimator(new DefaultItemAnimator());
        if (!workMode.equals(MyConstants.TYPE_ALL)) {
            bottomSheetAdapter = new BottomSheetAdapter(context, activeCategories, activeCategoriesListener);
            binding.rvActiveCategories.setAdapter(bottomSheetAdapter);

            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                        binding.fab.hide();
                        binding.ivBottomSheetArrow.setVisibility(View.INVISIBLE);
                    } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                        binding.fab.show();
                        binding.ivBottomSheetArrow.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_up_black_24dp)
                                .into(binding.ivBottomSheetArrow);
                    } else if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                        binding.fab.show();
                        binding.ivBottomSheetArrow.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_up_black_24dp)
                                .into(binding.ivBottomSheetArrow);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
        requestSort(currentSortOrder);

        if(position != 0) {
            binding.rvKits.scrollToPosition(position);
        }
        return binding.getRoot();
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

    private String tagToCategoryName(String tag) {
        String name;
        switch (tag) {
            case MyConstants.CODE_AIR:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.Air);
                break;
            case MyConstants.CODE_GROUND:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.ground);
                break;
            case MyConstants.CODE_SEA:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.sea);
                break;
            case MyConstants.CODE_SPACE:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.space);
                break;
            case MyConstants.CODE_AUTOMOTO:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.auto_moto);
                break;
            case MyConstants.CODE_FIGURES:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.Figures);
                break;
            case MyConstants.CODE_FANTASY:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.Fantasy);
                break;
            case MyConstants.CODE_OTHER:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.other);
                break;
            default:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.all);
                break;
        }
        return name;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(com.kutovenko.kitstasher.R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);


        MenuItem closeItem = menu.findItem(com.kutovenko.kitstasher.R.id.action_close);
        closeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getActivity().finish();
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(com.kutovenko.kitstasher.R.id.action_search)
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
        return id == com.kutovenko.kitstasher.R.id.action_search || super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(StashItem stashItem, ArrayList<StashItem> filteredItemList, int position) {
        if (workMode.equals(MyConstants.TYPE_SUPPLY)){
            FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            Bundle bundle = new Bundle(3);
            bundle.putString(MyConstants.ITEM_TYPE, workMode);
            bundle.putBoolean(MyConstants.PAINT_EDIT_MODE, true);
            bundle.putLong(MyConstants.ID, stashItem.getLocalId());
            AddSupplyFragment addFragment = AddSupplyFragment.newInstance();
            addFragment.setArguments(bundle);
            fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, addFragment);
            fragmentTransaction.commit();
        }else{
            Intent intent;
            intent = new Intent(context, ViewActivity.class);
            intent.putExtra(MyConstants.ITEM_TYPE, workMode);
            intent.putExtra(MyConstants.POSITION, position);//ид открытия пейджера
            intent.putExtra(MyConstants.CATEGORY, currentCategory);//???
            intent.putParcelableArrayListExtra(MyConstants.LIST, filteredItemList);
            ((Activity) context).startActivityForResult(intent, MainActivity.REQUEST_CODE_VIEW);
        }
    }


    @Override
    public void onPagerItemDelete(long itemId, int currentPosition, String uri, String onlineId) {
        if (!Helper.isBlank(uri)) {
            File file = new File(uri);
            if (file.exists()) {
                file.delete();
            }
        }
        try {
            deleteFromOnlineStash(onlineId);
        } finally {
            dbConnector.deleteItem(itemId);
        }
        rvAdapter.delete(currentPosition);
        sortedList.clear();
        sortedList.addAll(dbConnector.filteredKits(workMode, currentCategory, "_id DESC"));
        activeCategories = dbConnector.getActiveCategories(workMode);
        bottomSheetAdapter.updateCategories(activeCategories);
    }

    private void deleteFromOnlineStash(String onlineId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_STASH);
        query.getInBackground(onlineId, new GetCallback<ParseObject>() {
            public void done(ParseObject item, ParseException e) {
                if (e == null) {
                    item.put(MyConstants.PARSE_DELETED, true);
                    item.saveInBackground();
                } else {
                    //debug
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCategorySelected(String category) {
        sortedList.clear();
        sortedList.addAll(dbConnector.filteredKits(workMode, category, "_id DESC"));
        rvAdapter.notifyDataSetChanged();
        currentCategory = category;
        binding.tvChoosedCategory.setText(tagToCategoryName(category));
        rvAdapter.notifyItemRangeChanged(0, sortedList.size());

        setAllPassive();
        sortByNameAsc();
    }

    private void sortByBrandAsc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<StashItem>() {
            @Override
            public int compare(StashItem k1, StashItem k2) {
                return k1.getBrand().compareToIgnoreCase(k2.getBrand());
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(binding.tvBarBrand);
        binding.tvBarBrand.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_up_white_24dp));
        isSortBrandAsc = true;
        currentSortOrder = BRAND_ASC;
    }

    private void sortByBrandDesc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<StashItem>() {
            @Override
            public int compare(StashItem k1, StashItem k2) {
                return k2.getBrand().compareToIgnoreCase(k1.getBrand());
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(binding.tvBarBrand);
        binding.tvBarBrand.setCompoundDrawablesWithIntrinsicBounds(null, null, null, Helper.getAPICompatVectorDrawable
                (context, com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_down_white_24dp));
        isSortBrandAsc = false;
        currentSortOrder = BRAND_DESC;

    }

    private void sortByScaleAsc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<StashItem>() {
            @Override
            public int compare(StashItem k1, StashItem k2) {
                Integer s1 = k1.getScale();
                Integer s2 = k2.getScale();
                return s1.compareTo(s2);
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(binding.tvBarScale);
        binding.tvBarScale.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_up_white_24dp));
        isSortScaleAsc = true;
        currentSortOrder = SCALE_ASC;
    }

    private void sortByScaleDesc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<StashItem>() {
            @Override
            public int compare(StashItem k1, StashItem k2) {
                Integer s1 = k1.getScale();
                Integer s2 = k2.getScale();
                return s2.compareTo(s1);
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(binding.tvBarScale);
        binding.tvBarScale.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_down_white_24dp));
        isSortScaleAsc = false;
        currentSortOrder = SCALE_DESC;
    }

    private void sortByDateAcs() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<StashItem>() {
            @Override
            public int compare(StashItem k1, StashItem k2) {
                Long s1 = k1.getLocalId();
                Long s2 = k2.getLocalId();
                return s1.compareTo(s2);
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(binding.tvBarDate);
        binding.tvBarDate.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_up_white_24dp));
        isSortDateAsc = true;
        currentSortOrder = DATE_ASC;
    }

    private void sortByDateDesc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<StashItem>() {
            @Override
            public int compare(StashItem k1, StashItem k2) {
                Long s1 = k1.getLocalId();
                Long s2 = k2.getLocalId();
                return s2.compareTo(s1);
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(binding.tvBarDate);
        binding.tvBarDate.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_down_white_24dp));
        isSortDateAsc = false;
        currentSortOrder = DATE_DESC;
    }

    private void sortByNameAsc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<StashItem>() {
            @Override
            public int compare(StashItem k1, StashItem k2) {
                return k1.getName().compareToIgnoreCase(k2.getName());
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(binding.tvBarName);
        binding.tvBarName.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_up_white_24dp));
        isSortNameAsc = true;
        currentSortOrder = NAME_ASC;
    }

    private void sortByNameDesc() {
        forSort.clear();
        forSort = rvAdapter.getItemsList();
        Collections.sort(forSort, new Comparator<StashItem>() {
            @Override
            public int compare(StashItem k1, StashItem k2) {
                return k2.getName().compareToIgnoreCase(k1.getName());
            }
        });
        sortedList.clear();
        sortedList.addAll(forSort);
        rvAdapter.setSortedItemList(sortedList);
        setActive(binding.tvBarName);
        binding.tvBarName.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
                (context, com.kutovenko.kitstasher.R.drawable.ic_keyboard_arrow_down_white_24dp));
        isSortNameAsc = false;
        currentSortOrder = NAME_DESC;
    }

    private void setActive(TextView item){
        setAllPassive();
        item.setBackgroundColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorAccent));
        item.setTextColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorItem));
    }

    private void setAllPassive() {
        binding.tvBarName.setBackgroundColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorItem));
        binding.tvBarName.setTextColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorPassive));
        binding.tvBarName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        binding.tvBarDate.setBackgroundColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorItem));
        binding.tvBarDate.setTextColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorPassive));
        binding.tvBarDate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        binding.tvBarScale.setBackgroundColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorItem));
        binding.tvBarScale.setTextColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorPassive));
        binding.tvBarScale.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        binding.tvBarBrand.setBackgroundColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorItem));
        binding.tvBarBrand.setTextColor(Helper.getColor(getActivity(), com.kutovenko.kitstasher.R.color.colorPassive));
        binding.tvBarBrand.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvBarDate:
                if (!isSortDateAsc) {
                    sortByDateAcs();
                } else {
                    sortByDateDesc();
                }
                break;
            case R.id.tvBarName:

                if (!isSortNameAsc) {
                    sortByNameAsc();
                } else {
                    sortByNameDesc();
                }
                break;
            case R.id.tvBarBrand:
                if (!isSortBrandAsc) {
                    sortByBrandAsc();
                } else {
                    sortByBrandDesc();
                }
                break;
            case R.id.tvBarScale:

                if (!isSortScaleAsc) {
                    sortByScaleAsc();
                } else {
                    sortByScaleDesc();
                }
                break;
        }
    }
}