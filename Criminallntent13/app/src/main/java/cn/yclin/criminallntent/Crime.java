package cn.yclin.criminallntent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private int mRequiresPolice;

    public Crime(){
        mId = UUID.randomUUID();
        mDate = new Date();
        mRequiresPolice = 0;
    }

    public UUID getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public Date getmDate() {
        return mDate;
    }

    public boolean ismSolved() {
        return mSolved;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public void setmSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }

    public int getmRequiresPolice() {
        return mRequiresPolice;
    }

    public void setmRequiresPolice(int mRequiresPolice) {
        this.mRequiresPolice = mRequiresPolice;
    }
}
