package com.example.kitstasher.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ArchiveItemsFragment extends Fragment{
    private RecyclerView rvList;
    @BindView(R.id.tvBarBrand) TextView tvBarBrand;
    @BindView(R.id.tvBarDate) TextView tvBarDate;
    @BindView(R.id.tvBarScale) TextView tvBarScale;
    @BindView(R.id.tvBarName) TextView tvBarName;

    private boolean isSortBrandAsc,
            isSortDateAsc,
            isSortScaleAsc,
            isSortNameAsc;

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
    private ArrayList<Kit> sortedList;
    private ArrayList<Kit> forSort;
    private Unbinder unbinder;


    public static ArchiveItemsFragment newInstance(Bundle bundle){
        ArchiveItemsFragment fragment = new ArchiveItemsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_viewstash, container, false);
    }

//    @OnClick(R.id.tvBarDate)
//    public void date() {
//        if (!isSortDateAsc) {
//            sortByDateAcs();
//        } else {
//            sortByDateDesc();
//        }
//    }
//
//    @OnClick(R.id.tvBarName)
//    public void name() {
//        if (!isSortNameAsc) {
//            sortByNameAsc();
//        } else {
//            sortByNameDesc();
//        }
//    }
//
//    @OnClick(R.id.tvBarBrand)
//    public void brand() {
//        if (!isSortBrandAsc) {
//            sortByBrandAsc();
//        } else {
//            sortByBrandDesc();
//        }
//    }
//
//    @OnClick(R.id.tvBarScale)
//    public void scale() {
//        if (!isSortScaleAsc) {
//            sortByScaleAsc();
//        } else {
//            sortByScaleDesc();
//        }
//    }

//    private void sortByBrandAsc() {
//        forSort.clear();
//        forSort = rvAdapter.getItemsList();
//        Collections.sort(forSort, new Comparator<Kit>() {
//            @Override
//            public int compare(Kit k1, Kit k2) {
//                return k1.getBrand().compareToIgnoreCase(k2.getBrand());
//            }
//        });
//        sortedList.clear();
//        sortedList.addAll(forSort);
//        rvAdapter.setSortedItemList(sortedList);
//        setActive(tvBarBrand);
//        tvBarBrand.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
//                (context, R.drawable.ic_keyboard_arrow_up_white_24dp));
//        isSortBrandAsc = true;
//        currentFilter = BRAND_ASC;
//    }
//
//    private void sortByBrandDesc() {
//        forSort.clear();
//        forSort = rvAdapter.getItemsList();
//        Collections.sort(forSort, new Comparator<Kit>() {
//            @Override
//            public int compare(Kit k1, Kit k2) {
//                return k2.getBrand().compareToIgnoreCase(k1.getBrand());
//            }
//        });
//        sortedList.clear();
//        sortedList.addAll(forSort);
//        rvAdapter.setSortedItemList(sortedList);
//        setActive(tvBarBrand);
//        tvBarBrand.setCompoundDrawablesWithIntrinsicBounds(null, null, null, Helper.getAPICompatVectorDrawable
//                (context, R.drawable.ic_keyboard_arrow_down_white_24dp));
//        isSortBrandAsc = false;
//        currentFilter = BRAND_DESC;
//
//    }
//
//    private void sortByScaleAsc() {
//        forSort.clear();
//        forSort = rvAdapter.getItemsList();
//        Collections.sort(forSort, new Comparator<Kit>() {
//            @Override
//            public int compare(Kit k1, Kit k2) {
//                Integer s1 = k1.getScale();
//                Integer s2 = k2.getScale();
//                return s1.compareTo(s2);
//            }
//        });
//        sortedList.clear();
//        sortedList.addAll(forSort);
//        rvAdapter.setSortedItemList(sortedList);
//        setActive(tvBarScale);
//        tvBarScale.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
//                (context, R.drawable.ic_keyboard_arrow_up_white_24dp));
//        isSortScaleAsc = true;
//        currentFilter = SCALE_ASC;
//    }
//
//    private void sortByScaleDesc() {
//        forSort.clear();
//        forSort = rvAdapter.getItemsList();
//        Collections.sort(forSort, new Comparator<Kit>() {
//            @Override
//            public int compare(Kit k1, Kit k2) {
//                Integer s1 = k1.getScale();
//                Integer s2 = k2.getScale();
//                return s2.compareTo(s1);
//            }
//        });
//        sortedList.clear();
//        sortedList.addAll(forSort);
//        rvAdapter.setSortedItemList(sortedList);
//        setActive(tvBarScale);
//        tvBarScale.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
//                (context, R.drawable.ic_keyboard_arrow_down_white_24dp));
//        isSortScaleAsc = false;
//        currentFilter = SCALE_DESC;
//    }
//
//    private void sortByDateAcs() {
//        forSort.clear();
//        forSort = rvAdapter.getItemsList();
//        Collections.sort(forSort, new Comparator<Kit>() {
//            @Override
//            public int compare(Kit k1, Kit k2) {
//                Long s1 = k1.getLocalId();
//                Long s2 = k2.getLocalId();
//                return s1.compareTo(s2);
//            }
//        });
//        sortedList.clear();
//        sortedList.addAll(forSort);
//        rvAdapter.setSortedItemList(sortedList);
//        setActive(tvBarDate);
//        tvBarDate.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
//                (context, R.drawable.ic_keyboard_arrow_up_white_24dp));
//        isSortDateAsc = true;
//        currentFilter = DATE_ASC;
//    }
//
//    private void sortByDateDesc() {
//        forSort.clear();
//        forSort = rvAdapter.getItemsList();
//        Collections.sort(forSort, new Comparator<Kit>() {
//            @Override
//            public int compare(Kit k1, Kit k2) {
//                Long s1 = k1.getLocalId();
//                Long s2 = k2.getLocalId();
//                return s2.compareTo(s1);
//            }
//        });
//        sortedList.clear();
//        sortedList.addAll(forSort);
//        rvAdapter.setSortedItemList(sortedList);
//        setActive(tvBarDate);
//        tvBarDate.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
//                (context, R.drawable.ic_keyboard_arrow_down_white_24dp));
//        isSortDateAsc = false;
//        currentFilter = DATE_DESC;
//    }
//
//    private void sortByNameAsc() {
//        forSort.clear();
//        forSort = rvAdapter.getItemsList();
//        Collections.sort(forSort, new Comparator<Kit>() {
//            @Override
//            public int compare(Kit k1, Kit k2) {
//                return k1.getKit_name().compareToIgnoreCase(k2.getKit_name());
//            }
//        });
//        sortedList.clear();
//        sortedList.addAll(forSort);
//        rvAdapter.setSortedItemList(sortedList);
//        setActive(tvBarName);
//        tvBarName.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
//                (context, R.drawable.ic_keyboard_arrow_up_white_24dp));
//        isSortNameAsc = true;
//        currentFilter = NAME_ASC;
//    }
//
//    private void sortByNameDesc() {
//        forSort.clear();
//        forSort = rvAdapter.getItemsList();
//        Collections.sort(forSort, new Comparator<Kit>() {
//            @Override
//            public int compare(Kit k1, Kit k2) {
//                return k2.getKit_name().compareToIgnoreCase(k1.getKit_name());
//            }
//        });
//        sortedList.clear();
//        sortedList.addAll(forSort);
//        rvAdapter.setSortedItemList(sortedList);
//        setActive(tvBarName);
//        tvBarName.setCompoundDrawablesWithIntrinsicBounds(null,null, null, Helper.getAPICompatVectorDrawable
//                (context, R.drawable.ic_keyboard_arrow_down_white_24dp));
//        isSortNameAsc = false;
//        currentFilter = NAME_DESC;
//    }

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
