package com.lafferty.jai.trackie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class StandardHomeFragment extends Fragment {

    TextView tvStats1;
    TextView tvStats2;
    TextView tvMotivate;
    Button btCheckIn;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_standard_home,container,false);
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        tvStats1 = view.findViewById(R.id.tvStandardStats1);
        tvStats2 = view.findViewById(R.id.tvStandardStats2);
        tvMotivate = view.findViewById(R.id.tvStandardMotivate);
        btCheckIn = view.findViewById(R.id.btCheckIn);

        btCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ((HomeActivity)getActivity()).CreateCheckInFragment();
            }
        });

        SetStats();
    }

    public void SetStats(){
        ArrayList<Weight> weights = ((HomeActivity)getActivity()).get_weights();
        if (weights.size() > 5) {
            String[] weekly = Stats.WeekAverageChange(weights);
            tvStats1.setText(Stats.CurrentStreak(weights));
            tvStats2.setText(weekly[0]);
            tvMotivate.setText(weekly[1]);
        } else {
            tvStats1.setText(getText(R.string.stats_error));

        }
    }
}
