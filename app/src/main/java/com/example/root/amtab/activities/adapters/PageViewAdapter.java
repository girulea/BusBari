package com.example.root.amtab.activities.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 08/11/16.
 */


//CLASSE USATA PER IL SETUP DEL TABLAYOUT
public class PageViewAdapter extends FragmentPagerAdapter
{
    final List<Fragment> fragmentList = new ArrayList<>();
    final List<String> fragmentTitleList = new ArrayList<>();
    public PageViewAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String fragmentTitle)
    {
        fragmentList.add(fragment);
        fragmentTitleList.add(fragmentTitle);
    }
    @Override
    public android.support.v4.app.Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle( int position )
    {
        return fragmentTitleList.get(position);
    }


}