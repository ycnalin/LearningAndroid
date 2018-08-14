package cn.yclin.criminallntent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";

    public static final String EXTRA_DATE =
            "cn.yclin.android.criminalintent.date";

    private DatePicker mDatePicker;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private Calendar mCalendar = Calendar.getInstance();

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_date, container,false);

        Date date = (Date)getArguments().getSerializable(ARG_DATE);

        mCalendar.setTime(date);
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mDatePicker = view.findViewById(R.id.date_picker);
        mDatePicker.init(year, month, day, null);

        mPositiveButton = view.findViewById(R.id.dialog_date_ok_button);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                mCalendar.set(year,month,day);
                Date date = mCalendar.getTime();
                sendResult(Activity.RESULT_OK, date);
                getActivity().finish();
            }
        });

        mNegativeButton = view.findViewById(R.id.dialog_date_cancel_button);
        mNegativeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return view;

    }

    private void sendResult(int resultCode, Date date) {

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        if (getTargetFragment() == null) {
            getActivity().setResult(resultCode, intent);
        }
        else{
            getTargetFragment()
                    .onActivityResult(getTargetRequestCode(), resultCode, intent);
        }

    }
}
