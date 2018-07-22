package cn.yclin.criminallntent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;
    private static double mCrimeRate = 1.1;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i % 2 == 0);
            if(Math.random() > mCrimeRate) {
                crime.setmRequiresPolice(1);
                crime.setmTitle("Terrible Crime #" + i);
            }
            mCrimes.add(crime);
        }
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getmId().equals(id)) {
                return crime;
            }
        }
        return null;
    }

    public static double getmCrimeRate() {
        return mCrimeRate;
    }

    public static void setmCrimeRate(double mCrimeRate) {
        CrimeLab.mCrimeRate = mCrimeRate;
    }

    public List<Crime> getmCrimes() {
        return mCrimes;
    }
}
