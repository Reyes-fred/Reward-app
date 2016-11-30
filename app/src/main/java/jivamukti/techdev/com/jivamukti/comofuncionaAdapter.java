package jivamukti.techdev.com.jivamukti;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class comofuncionaAdapter extends FragmentPagerAdapter {

    public comofuncionaAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new comofunciona1Fragment();
            case 1:
                return new comofunciona2Fragment();
            case 2:
                return new comofunciona3Fragment();
            case 3:
                return new comofunciona4Fragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
