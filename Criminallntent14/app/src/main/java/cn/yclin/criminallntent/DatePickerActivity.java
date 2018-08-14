package cn.yclin.criminallntent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;


import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity {
    private static final String EXTRA_DATE =
            "cn.yclin.android.criminalintent.date";

    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_DATE);
        return DatePickerFragment
                .newInstance(date);
    }

    public static Intent newIntent(Context packageContext, Date date) {
        Intent intent = new Intent(packageContext, DatePickerActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }
}
