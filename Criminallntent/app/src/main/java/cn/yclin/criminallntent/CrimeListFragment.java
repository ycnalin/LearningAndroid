package cn.yclin.criminallntent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int mClickedCrimePosition;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("hh:mm a EEEE, MMM dd yyyy", Locale.ENGLISH);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getmCrimes();

        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else{
            //mAdapter.notifyItemChanged(mClickedCrimePosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Button mCallPoliceButton;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            super(inflater.inflate(R.layout.list_item_crime_terrible, parent, false));

            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mCallPoliceButton = itemView.findViewById(R.id.call_police);
            mCallPoliceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // call police
                }
            });
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(getActivity(),mCrime.getmTitle()+" clicked!",Toast.LENGTH_SHORT).show();
            //Intent intent = CrimeActivity.newIntent(getActivity(),mCrime.getmId());
            Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getmId());
            mClickedCrimePosition = getAdapterPosition();
            startActivity(intent);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getmTitle());
            mDateTextView.setText(sdf.format(mCrime.getmDate()));
            mSolvedImageView.setVisibility(crime.ismSolved() ? View.VISIBLE : View.GONE);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            CrimeHolder holder;
            if(viewType == 0){
                holder = new CrimeHolder(layoutInflater, parent);
            }else
            {
                holder = new CrimeHolder(layoutInflater, parent, viewType);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemViewType(int position) {
            return mCrimes.get(position).getmRequiresPolice();
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
