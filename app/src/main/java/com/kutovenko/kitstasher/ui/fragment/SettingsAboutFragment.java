package com.kutovenko.kitstasher.ui.fragment;


import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentSettingsAboutBinding;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.ui.MainActivity;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


public class SettingsAboutFragment extends Fragment {

    public SettingsAboutFragment() {
    }

    public static SettingsAboutFragment newInstance() {
        return new SettingsAboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSettingsAboutBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_about, container, false);
        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(com.kutovenko.kitstasher.R.string.nav_about_privacy));
        binding.tvFacebookLink.setClickable(true);
        binding.tvFacebookLink.setMovementMethod(LinkMovementMethod.getInstance());
        String fbText = "<a href='https://www.facebook.com/business/m/one-sheeters/gdpr-developer-faqs'>"
                + getString(com.kutovenko.kitstasher.R.string.facebook_gdpr)
                + "</a>";
        binding.tvFacebookLink.setText(Helper.fromHtml(fbText));

        binding.tvP4BLink.setClickable(true);
        binding.tvP4BLink.setMovementMethod(LinkMovementMethod.getInstance());
        String p4bText = "<a href='https://buddy.com/privacy-policy'>"
                + getString(com.kutovenko.kitstasher.R.string.p4b_gdpr)
                + "</a>";
        binding.tvP4BLink.setText(Helper.fromHtml(p4bText));

        binding.tvKitstasherPageLink.setClickable(true);
        binding.tvKitstasherPageLink.setMovementMethod(LinkMovementMethod.getInstance());
        String ksText = "<a href='https://www.facebook.com/KitStasher-318293735297557/'>"
                + getString(com.kutovenko.kitstasher.R.string.kitstasher_page)
                + "</a>";
        binding.tvKitstasherPageLink.setText(Helper.fromHtml(ksText));

        return binding.getRoot();
    }
}
