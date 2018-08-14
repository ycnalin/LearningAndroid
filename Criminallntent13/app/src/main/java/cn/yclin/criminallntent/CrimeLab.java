package cn.yclin.criminallntent;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Map<UUID, Crime> mCrimes;
    private List<Crime> mRecyclerViewCrimes;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mCrimes = new LinkedHashMap<>();

        for (int i = 0; i < 3; i++) {
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i % 2 == 0);

            mCrimes.put(crime.getmId(),crime);
        }

        mRecyclerViewCrimes = new LinkedList<>(mCrimes.values());
    }

    public void addCrime(Crime c){
        mCrimes.put(c.getmId(),c);
        mRecyclerViewCrimes.add(c);
    }

    public void deleteCrime(Crime c){
        mCrimes.remove(c.getmId());
        mRecyclerViewCrimes.remove(c);
    }


    public int getSize(){
        return mCrimes.size();
    }

    public Crime getCrime(UUID id) {
        return mCrimes.get(id);
    }

    public List<Crime> getmCrimes() {
        return mRecyclerViewCrimes;
    }
}
