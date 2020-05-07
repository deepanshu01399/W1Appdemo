package com.deepanshu.whatsappdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter  {

    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }




    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CameraFragment cameraFragment = new CameraFragment();
                return cameraFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:
                GroupFragment groupFragment=new GroupFragment();
                return groupFragment;
            case 3:
                Reqeust_fragment request_fragment = new Reqeust_fragment();
                return request_fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Camera";
            case 1:
                return "Chat";
            case 2:
                return "Groups";
            case 3:
                return "Request";
            default:
                return null;
        }
    }
}