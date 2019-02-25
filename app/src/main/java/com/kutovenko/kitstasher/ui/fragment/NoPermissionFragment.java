package com.kutovenko.kitstasher.ui.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentNoPermissionBinding;
import com.kutovenko.kitstasher.util.MyConstants;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static com.kutovenko.kitstasher.ui.MainActivity.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.kutovenko.kitstasher.ui.MainActivity.MY_PERMISSIONS_REQUEST_WRITE;

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
        FragmentNoPermissionBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_no_permission, container,
                false);

        if (notificationText.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            binding.tvRequestText.setText(com.kutovenko.kitstasher.R.string.permission_storage);
        }else if (notificationText.equals(Manifest.permission.CAMERA)){
            binding.tvRequestText.setText(com.kutovenko.kitstasher.R.string.permission_camera);
        }

        binding.btnRequestPermission.setOnClickListener(new View.OnClickListener() {
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

        binding.btnCancelPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = KitsFragment.newInstance();
                Bundle bundle = new Bundle(1);
                bundle.putString(MyConstants.ITEM_TYPE, workMode);
                fragment.setArguments(bundle);
                assert getFragmentManager() != null;
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(),
                            com.kutovenko.kitstasher.R.string.permission_denied, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.permission_granted, Toast.LENGTH_SHORT).show();
                    Fragment fragment = KitsFragment.newInstance();
                    Bundle args = new Bundle(1);
                    args.putString(MyConstants.ITEM_TYPE, workMode);
                    fragment.setArguments(args);
                    assert getFragmentManager() != null;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }

            case MY_PERMISSIONS_REQUEST_WRITE:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(),
                            com.kutovenko.kitstasher.R.string.permission_denied, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.permission_granted, Toast.LENGTH_SHORT).show();
                    Fragment fragment = KitsFragment.newInstance();
                    Bundle args = new Bundle(1);
                    args.putString(MyConstants.ITEM_TYPE, workMode);
                    fragment.setArguments(args);
                    assert getFragmentManager() != null;
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
        }
    }
}