package com.example.kitstasher.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.objects.PaintItem;

import java.util.ArrayList;

public class ArchiveFragment extends Fragment {
    ViewPager viewPager;
    ArrayList<Kit> archivedKits;
    ArrayList<Kit> archivedAftermarket;
    ArrayList<PaintItem> archivedPaint;
}
