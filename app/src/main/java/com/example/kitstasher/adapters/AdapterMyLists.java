package com.example.kitstasher.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.other.DbConnector;


/**
 * Created by Алексей on 11.08.2017.
 */

public class AdapterMyLists extends CursorAdapter {
    private final Context context;
    private DbConnector dbConnector;
    Cursor list;

    public AdapterMyLists (Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
        dbConnector = new DbConnector(context);
        dbConnector.open();
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_mylists, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final ViewHolderList holder = new ViewHolderList();
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYLISTS_COLUMN_LIST_NAME));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYLISTS_COLUMN_DATE));

        holder.tvListName = (TextView)view.findViewById(R.id.tvMylistName);
        holder.tvListDateAdded = (TextView)view.findViewById(R.id.tvListDateAdded);
        holder.btnDelete = (ImageButton) view.findViewById(R.id.ibtnDeleteMyList);
        holder.btnEdit = (ImageButton) view.findViewById(R.id.ibtnEditMyList);

        Object obj = cursor.getString(cursor.getColumnIndex(DbConnector.MYLISTS_COLUMN_ID));
        holder.btnDelete.setTag(obj);
        holder.btnEdit.setTag(obj);

        holder.btnDelete.setFocusable(false);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object obj = view.getTag();
                String st = obj.toString();
                dbConnector.deleteList(st);
                dbConnector.clearList(name);
                Cursor newcursor = dbConnector.getAllLists();
                changeCursor(newcursor);
                notifyDataSetChanged();
            }

        });

        holder.btnEdit.setFocusable(false);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String nameToChange = holder.tvListName.getText().toString();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.list_alertdialog, null);
                dialogBuilder.setView(dialogView);
                final EditText etNewListName = (EditText) dialogView.findViewById(R.id.etNewListName);

                dialogBuilder.setTitle(context.getString(R.string.Rename_) + nameToChange + "\"");
                dialogBuilder.setPositiveButton(R.string.Done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String listname = etNewListName.getText().toString().trim();
                        if (dbConnector.isListExists(listname)) {
                            etNewListName.setError(context.getResources()
                                    .getString(R.string.List_with_this_name_already_exists));
                            Toast.makeText(context, R.string.List_with_this_name_already_exists,
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            dbConnector.updateList(nameToChange, listname);
                            Cursor newcursor = dbConnector.getAllLists();
                            changeCursor(newcursor);
                            notifyDataSetChanged();
                        }
                    }
                });
                dialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //pass
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
            }

        });

        holder.tvListName.setText(name);
        holder.tvListDateAdded.setText(date);
    }

    static class ViewHolderList {
        TextView tvListName, tvListDateAdded;
        ImageButton btnEdit, btnDelete;
    }
}
