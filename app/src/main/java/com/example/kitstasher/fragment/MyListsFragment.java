package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.ListActivity;
import com.example.kitstasher.adapters.AdapterMyLists;
import com.example.kitstasher.other.DbConnector;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Алексей on 11.08.2017.
 */

public class MyListsFragment extends Fragment implements View.OnClickListener {
    private DbConnector dbConnector;
    private Cursor cursor;
    private View view;
    public AdapterMyLists adapterMyLists;
    private Context mContext;
    private ListView lvMyLists;
    private Button btnAddMyList;

    public MyListsFragment() {
    }

    public static MyListsFragment newInstance() {
        MyListsFragment fragment = new MyListsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        cursor = dbConnector.getAllLists();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // открываем подключение к БД
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_mylists, container, false);

        lvMyLists = (ListView) view.findViewById(R.id.lvMyLists);
        cursor = dbConnector.getAllLists();
        adapterMyLists = new AdapterMyLists(getActivity(), cursor);
        lvMyLists.setAdapter(adapterMyLists);
        lvMyLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("list_name", l);
                startActivity(intent);
            }
        });
        btnAddMyList = (Button) view.findViewById(R.id.btnAddMylist);
        btnAddMyList.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddMylist:
                showAddNewListDialog();
                break;
        }
    }

    public void showAddNewListDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(R.layout.list_alertdialog, null);
        dialogBuilder.setView(dialogView);

        final EditText etNewListName = (EditText) dialogView.findViewById(R.id.etNewListName);

        dialogBuilder.setTitle(R.string.new_list);
        dialogBuilder.setPositiveButton(R.string.Done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String date = df.format(c.getTime());
                String listname = etNewListName.getText().toString().trim();
                if (dbConnector.isListExists(listname)){
                    etNewListName.setError(mContext.getResources()
                            .getString(R.string.List_with_this_name_already_exists));
                    Toast.makeText(mContext, R.string.List_with_this_name_already_exists,
                            Toast.LENGTH_SHORT).show();
                }else{
                dbConnector.addList(etNewListName.getText().toString().trim(), date);
                cursor = dbConnector.getAllLists();
                adapterMyLists = new AdapterMyLists(getActivity(), cursor);
                lvMyLists.setAdapter(adapterMyLists);
                }
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
