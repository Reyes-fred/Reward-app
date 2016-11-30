package jivamukti.techdev.com.jivamukti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * Created by Alfredo on 04/11/2015.
 */
public class TutorialAdapter extends FragmentPagerAdapter {

    public TutorialAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return new tutotabFragment();
            case 1:

                return new tutotabFragment2();
            case 2:

                return new tutotabFragment3();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
