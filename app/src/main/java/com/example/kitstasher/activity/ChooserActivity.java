package com.example.kitstasher.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterChooserList;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.DbConnector;

import java.math.BigDecimal;
import java.util.List;


public class ChooserActivity extends AppCompatActivity {
    public static List<String> choosedIds;
    private ListView stashList;
    private DbConnector dbConnector;
    private Cursor cursor;
    String listname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        listname = getIntent().getExtras().getString("listname");

        dbConnector = new DbConnector(this);
        dbConnector.open();
        cursor = dbConnector.getAllData("category");

        stashList = (ListView)findViewById(R.id.lvChooser);
        AdapterChooserList stashListAdapter =
                new AdapterChooserList(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        stashList.setAdapter(stashListAdapter);

        Button doneButton = (Button)findViewById(R.id.btnChooseDone);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosedIds != null && !choosedIds.isEmpty()) {
                    for (int i = 0; i < choosedIds.size(); i++) {
                        long l = Long.valueOf(choosedIds.get(i));
                            Cursor c = dbConnector.getRecById(l);
                            copyKit(c);
                    }
                }else {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    if (choosedIds != null && !choosedIds.isEmpty()) {
                    choosedIds.clear();
                    }
                    finish();
                }
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                if (choosedIds != null && !choosedIds.isEmpty()) {
                    choosedIds.clear();
                }
                finish();
            }
        });

        Button cancelButton = (Button)findViewById(R.id.btnChooseCancelled);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                if (choosedIds != null && !choosedIds.isEmpty()) {
                    choosedIds.clear();
                }
                finish();
            }
        });
    }

    private void copyKit(Cursor c) {
        c.moveToFirst();
            String boxart_url = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
            String boxart_uri = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
            String brand = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
            String cat_no = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
            String name = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
            int scale = c.getInt(c.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
            String category = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
            String year = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_YEAR));
            String description = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_DESCRIPTION));
            String kit_noengname = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_KIT_NAME));
            String barcode = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BARCODE));
            String status = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_SEND_STATUS));
            String date = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_DATE));

        String onlineId = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_ID_ONLINE));

        String notes = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES));
        String purchaseDate = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_DATE));
        String currency = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY));
        int quantity = c.getInt(c.getColumnIndexOrThrow(DbConnector.COLUMN_QUANTITY));
        int price = c.getInt(c.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE));


        Kit kitToAdd = new Kit.KitBuilder()
                .hasBrand(brand)
                .hasBrand_catno(cat_no)
                .hasKit_name(name)
                .hasScale(scale)
                .hasCategory(category)
                .hasBarcode(barcode)
                .hasKit_noeng_name(kit_noengname)
                .hasDescription(description)

                .hasPrototype("")//not in use

                .hasBoxart_url(boxart_url)
                .hasBoxart_uri(boxart_uri)
                .hasScalemates_url("")
                .hasYear(year)
                .hasOnlineId(onlineId)
                .hasDateAdded(date)
                .hasDatePurchased(purchaseDate)
                .hasQuantity(quantity)
                .hasNotes(notes)
                .hasPrice(price)
                .hasCurrency(currency)
                .build();


//            Kit kitToAdd = new Kit.KitBuilder()
//                    .hasBrand(brand)
//                    .hasBrand_catno(cat_no)
//                    .hasKit_name(name)
//                    .hasScale(scale)
//                    .hasCategory(category)
//                    .hasDescription(description)
////                .hasPrototype(prototype)
//                    .hasKit_noeng_name(noengname)
//                    .hasBoxart_url(url)
//                    .hasBoxart_uri(uri)
//                    .hasBarcode(barcode)
////                .hasScalemates_url(scalemates_page)
//                    .hasYear(year)
////                    .hasOnlineId(onlineId)
//                    .hasNotes(notes)
//                    .hasDatePurchased(purchaseDate)
//
//                    .build();

        if (!dbConnector.searchListForDoubles(listname, brand, cat_no)) {

            dbConnector.addListItem(kitToAdd, listname);
//            dbConnector.addListItem(
//                    kitToAdd.getBarcode(),
//                    kitToAdd.getBrand(),
//                    kitToAdd.getBrand_catno(),
//                    kitToAdd.getScale(),
//                    kitToAdd.getKit_name(),
//                    kitToAdd.getKit_noeng_name(),
//                    status,
//                    date,
//                    kitToAdd.getBoxart_url(),
//                    kitToAdd.getCategory(),
//                    kitToAdd.getBoxart_uri(),
////                kit.getOnlineId(),
//                    kitToAdd.getDescription(),
//                    kitToAdd.getYear(),
//                    notes,
//                    purchaseDate,
//                    quantity,
//                    new BigDecimal(price),
//                    currency,
//                    listname);

        }
    }
}
