package com.example.kitstasher.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.other.MyConstants;

import static com.example.kitstasher.activity.MainActivity.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.example.kitstasher.activity.MainActivity.MY_PERMISSIONS_REQUEST_WRITE;

public class NoPermissionFragment extends Fragment {
    private static final String NOTIFICATION_ID = "notificationId";
    private static final String WORKMODE = "workMode";

    private String notificationText;
    private String workMode;

    public NoPermissionFragment() {
    }

    public static NoPermissionFragment newInstance(String notificationId, String workMode) {
        NoPermissionFragment fragment = new NoPermissionFragment();
        Bundle args = new Bundle();
        args.putString(NOTIFICATION_ID, notificationId);
        args.putString(WORKMODE, workMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            notificationText = getArguments().getString(NOTIFICATION_ID);
            workMode = getArguments().getString(WORKMODE);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_no_permission, container,
                false);
        TextView textView = view.findViewById(R.id.tvRequestText);
        if (notificationText.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            textView.setText(R.string.permission_storage);
        }else if (notificationText.equals(Manifest.permission.CAMERA)){
            textView.setText(R.string.permission_camera);
        }

        Button btnGrant = view.findViewById(R.id.btnRequestPermission);
        btnGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationText.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE);
                }else if(notificationText.equals(Manifest.permission.CAMERA)){
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        });

        Button btnCancelPermission = view.findViewById(R.id.btnCancelPermission);
        btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v4.app.Fragment fragment = KitsFragment.newInstance();
                Bundle bundle = new Bundle(1);
                bundle.putString(MyConstants.WORK_MODE, workMode);
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(),
                            R.string.permission_denied, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
                    if (workMode.equals("4")) {
                        android.support.v4.app.Fragment fragment =
                                SearchFragment.newInstance();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                    } else {
                        android.support.v4.app.Fragment fragment = KitsFragment.newInstance();
                        Bundle args = new Bundle(1);
                        args.putString(MyConstants.WORK_MODE, workMode);
                        fragment.setArguments(args);
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                }

            case MY_PERMISSIONS_REQUEST_WRITE:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(),
                            R.string.permission_denied, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
                    if (workMode.equals("4")) {
                        android.support.v4.app.Fragment fragment =
                                SearchFragment.newInstance();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                    } else {
                        android.support.v4.app.Fragment fragment = KitsFragment.newInstance();
                        Bundle args = new Bundle(1);
                        args.putString(MyConstants.WORK_MODE, workMode);
                        fragment.setArguments(args);
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                }
        }

    }
}
