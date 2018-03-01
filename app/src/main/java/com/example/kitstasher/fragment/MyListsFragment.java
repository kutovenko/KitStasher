package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.MyListCursorAdapter;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Алексей on 11.08.2017. Viewing list of personal lists
 */

public class MyListsFragment extends Fragment implements View.OnClickListener {
    private DbConnector dbConnector;
    private Cursor cursor;
    private Context mContext;
    private View view;
    private LinearLayout linLayoutDate, linLayoutListName;
    private ImageView ivSortDate, ivSortListName;
    private boolean sortDate, sortName;
    private MyListCursorAdapter rvAdapter;
    public MyListsFragment() {
    }

    public static MyListsFragment newInstance() {
        return new MyListsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // открываем подключение к БД
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_mylists, container, false);
        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(R.string.Wishlists));

        RecyclerView rvMyLists = view.findViewById(R.id.rvMyLists);
        LinearLayoutManager rvListsManager = new LinearLayoutManager(mContext);
        rvMyLists.setHasFixedSize(true);
        rvMyLists.setLayoutManager(rvListsManager);
        rvMyLists.setItemAnimator(new DefaultItemAnimator());
        cursor = dbConnector.getLists("_id DESC");
        rvAdapter = new MyListCursorAdapter(cursor, mContext, MyConstants.MODE_A_LIST);
        rvAdapter.hasStableIds();
        rvMyLists.setAdapter(rvAdapter);
        cursor = dbConnector.getLists("_id DESC");

        Button btnAddMyList = view.findViewById(R.id.btnAddMylist);
        btnAddMyList.setOnClickListener(this);

        linLayoutDate = view.findViewById(R.id.linLayoutListSortDate);
        linLayoutDate.setOnClickListener(this);
        linLayoutListName = view.findViewById(R.id.linLayoutListSortName);
        linLayoutListName.setOnClickListener(this);

        ivSortDate = view.findViewById(R.id.ivListSortDate);
        ivSortDate.setVisibility(View.INVISIBLE);
        ivSortListName = view.findViewById(R.id.ivListSortName);
        ivSortListName.setVisibility(View.INVISIBLE);

        setActive(R.id.linLayoutListSortDate, ivSortDate);
        sortDate = true;
        sortName = true;

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddMylist:
                showAddNewListDialog();
                break;

            case R.id.linLayoutListSortDate:
                setActive(R.id.linLayoutListSortDate, ivSortDate);
                if (sortDate) {
                    SortByDateAcs();
                    sortDate = false;
                } else {
                    SortByDateDesc();
                    sortDate = true;
                }
                sortName = true;
                break;

            case R.id.linLayoutListSortName:
                setActive(R.id.linLayoutListSortName, ivSortListName);
                if (sortName) {
                    SortByNameAsc();
                    sortName = false;
                } else {
                    SortByNameDesc();
                    sortName = true;
                }
                sortDate = true;
                break;
        }
    }

    private void setActive(int linLayout, ImageView arrow) {
        linLayoutDate.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutDate, 0);
        linLayoutListName.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutListName, 0);
        LinearLayout activeLayout = view.findViewById(linLayout);
        activeLayout.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
        setTextColor(activeLayout, 1);
        ivSortListName.setVisibility(View.INVISIBLE);
        ivSortDate.setVisibility(View.INVISIBLE);
        arrow.setVisibility(View.VISIBLE);
    }

    private void setTextColor(LinearLayout linearLayout, int mode) {
        View view = linearLayout.getChildAt(0);
        int color;
        if (mode == 0) {
            color = Helper.getColor(getActivity(), R.color.colorPassive);
        } else {
            color = Helper.getColor(getActivity(), R.color.colorItem);
        }
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTextColor(color);
        }
    }

    public void showAddNewListDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(R.layout.list_alertdialog, null);
        dialogBuilder.setView(dialogView);

        final EditText etNewListName = dialogView.findViewById(R.id.etNewListName);

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
                    cursor = dbConnector.getLists("_id DESC");
                    rvAdapter.notifyItemInserted(0);
                    prepareListAndAdapter(cursor);
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

    public void SortByDateAcs() {
        cursor = dbConnector.getLists("_id");
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortDate = true;
    }

    public void SortByDateDesc() {
        cursor = dbConnector.getLists("_id DESC");
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortDate = false;
    }

    public void SortByNameAsc() {
        cursor = dbConnector.getLists("listname");
        prepareListAndAdapter(cursor);
        ivSortListName.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortName = true;
    }

    public void SortByNameDesc() {
        cursor = dbConnector.getLists("listname DESC");
        prepareListAndAdapter(cursor);
        ivSortListName.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortName = false;
    }

    public void prepareListAndAdapter(Cursor cursor) {
        rvAdapter.changeCursor(cursor);
        rvAdapter.notifyDataSetChanged();
    }
}
