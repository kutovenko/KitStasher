package com.example.kitstasher.fragment;

import android.support.v4.app.Fragment;

import com.example.kitstasher.other.Constants;

/**
 * Created by Алексей on 19.08.2017.
 */

public class BaseViewStashFragment extends Fragment {
    private String category;
    private String sortBy;
    public BaseViewStashFragment(){
    this.category = getArguments().getString(Constants.CATEGORY);
    }
}
