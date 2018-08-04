package cn.yclin.criminallntent;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Map<UUID, Crime> mCrimes;
    private static double mCrimeRate = 1.1;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mCrimes = new LinkedHashMap<>();

        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i % 2 == 0);
            if(Math.random() > mCrimeRate) {
                crime.setmRequiresPolice(1);
                crime.setmTitle("Terrible Crime #" + i);
            }
            mCrimes.put(crime.getmId(),crime);
        }
    }

    public Crime getCrime(UUID id) {
        return mCrimes.get(id);
    }

    public static double getmCrimeRate() {
        return mCrimeRate;
    }

    public static void setmCrimeRate(double mCrimeRate) {
        CrimeLab.mCrimeRate = mCrimeRate;
    }

    public List<Crime> getmCrimes() {
        return new ArrayList<>(mCrimes.values());
    }
}
