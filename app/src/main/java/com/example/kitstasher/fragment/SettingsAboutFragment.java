package com.example.kitstasher.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.other.Helper;


public class SettingsAboutFragment extends Fragment {

    public SettingsAboutFragment() {
        // Required empty public constructor
    }

    public static SettingsAboutFragment newInstance() {
        return new SettingsAboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_about, container, false);
        TextView tvFacebookLink = view.findViewById(R.id.tvFacebookLink);
        tvFacebookLink.setClickable(true);
        tvFacebookLink.setMovementMethod(LinkMovementMethod.getInstance());
        String fbText = "<a href='https://www.facebook.com/business/m/one-sheeters/gdpr-developer-faqs'>"
                + getString(R.string.facebook_gdpr)
                + "</a>";
        tvFacebookLink.setText(Helper.fromHtml(fbText));

        TextView tvP4BLink = view.findViewById(R.id.tvP4BLink);
        tvP4BLink.setClickable(true);
        tvP4BLink.setMovementMethod(LinkMovementMethod.getInstance());
        String p4bText = "<a href='https://buddy.com/privacy-policy'>"
                + getString(R.string.p4b_gdpr)
                + "</a>";
        tvP4BLink.setText(Helper.fromHtml(p4bText));

        TextView tvKitstasherPageLink = view.findViewById(R.id.tvKitstasherPageLink);
        tvKitstasherPageLink.setClickable(true);
        tvKitstasherPageLink.setMovementMethod(LinkMovementMethod.getInstance());
        String ksText = "<a href='https://www.facebook.com/KitStasher-318293735297557/'>"
                + getString(R.string.kitstasher_page)
                + "</a>";
        tvKitstasherPageLink.setText(Helper.fromHtml(ksText));


        return view;
    }

}
