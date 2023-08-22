package com.smf.toolbox.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.smf.toolbox.fragments.HomeFragment;
import com.smf.toolbox.fragments.SettingsFragment;
import com.smf.toolbox.fragments.ToolFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 1 -> new ToolFragment();
            case 2 -> new SettingsFragment();
            default -> new HomeFragment();
        };
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
