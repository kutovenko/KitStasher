package com.example.kitstasher.objects;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by Алексей on 01.02.2018. Custom class for view pager with kits and aftermarket
 */

public class CustomKitsViewPager extends ViewPager {
    public CustomKitsViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void refresh() {
        getAdapter().notifyDataSetChanged();
    }

}
