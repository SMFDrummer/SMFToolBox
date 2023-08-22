package com.smf.toolbox.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smf.toolbox.R;
import com.smf.toolbox.adapters.ViewPagerAdapter;
import com.smf.toolbox.fragments.HomeFragment;
import com.smf.toolbox.fragments.SettingsFragment;
import com.smf.toolbox.fragments.ToolFragment;

public class MainActivity extends AppCompatActivity {
    private final HomeFragment homeFragment = new HomeFragment();
    private final ToolFragment toolFragment = new ToolFragment();
    private final SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setCurrentItem(0);

        viewPager2.setAdapter(new ViewPagerAdapter(this));

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.homeFragment);
                } else if (position == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.toolFragment);
                } else if (position == 2) {
                    bottomNavigationView.setSelectedItemId(R.id.settingsFragment);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        ViewPager2.PageTransformer pageTransformer = (page, position) -> {
            page.setCameraDistance(12000); // Increase the camera distance

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                page.setAlpha(1);
                page.setPivotX(page.getWidth() * 0.5f);
                page.setPivotY(page.getHeight() * 0.5f);
                page.setRotationY(position * 90);
                page.setScaleX(1 - Math.abs(position) / 2);
                page.setScaleY(1 - Math.abs(position) / 2);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }
        };


        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setNavigationBarColor(Color.TRANSPARENT);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            MenuItem homeItem = bottomNavigationView.getMenu().findItem(R.id.homeFragment);
            MenuItem toolItem = bottomNavigationView.getMenu().findItem(R.id.toolFragment);
            MenuItem settingItem = bottomNavigationView.getMenu().findItem(R.id.settingsFragment);
            homeItem.setIcon(R.drawable.ic_home);
            toolItem.setIcon(R.drawable.ic_tool);
            settingItem.setIcon(R.drawable.ic_settings);
            if (item.getItemId() == R.id.homeFragment) {
                item.setIcon(R.drawable.ic_home_selected);
                viewPager2.setCurrentItem(0);
                viewPager2.setPageTransformer(pageTransformer);
                return true;
            } else if (item.getItemId() == R.id.toolFragment) {
                item.setIcon(R.drawable.ic_tool_selected);
                viewPager2.setCurrentItem(1);
                viewPager2.setPageTransformer(pageTransformer);
                return true;
            } else if (item.getItemId() == R.id.settingsFragment) {
                item.setIcon(R.drawable.ic_settings_selected);
                viewPager2.setCurrentItem(2);
                viewPager2.setPageTransformer(pageTransformer);
                return true;
            }
            return false;
        });
    }
}