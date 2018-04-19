package in.ac.iitb.cse.a153059007;

/**
 * Created by ajay on 16/2/18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                LoginActivityFragment loginActivityFragment = new LoginActivityFragment();
                return loginActivityFragment;
            case 1:
                SensorActivityFragment sensorActivityFragment = new SensorActivityFragment();
                return sensorActivityFragment;
            case 2:
                RecordActivityFragment recordActivityFragment = new RecordActivityFragment();
                return recordActivityFragment;
            case 3:
                SVMActivityFragment svmActivityFragment = new SVMActivityFragment();
                return svmActivityFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}