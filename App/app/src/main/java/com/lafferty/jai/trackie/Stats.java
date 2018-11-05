package com.lafferty.jai.trackie;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class Stats {

    private static Context _context;

    public static void Initialise(Context context) {
        _context = context;
    }

    public static long getLongDateWithoutTime(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    private static int GainStreak(ArrayList<Weight> weights){
        ArrayList<Weight> _weights = new ArrayList<>(weights);
        Collections.reverse(_weights);
        int days= 0;
        for (int i=0; i < _weights.size()-1; i++){
            if(_weights.get(i+1).get_weight() < _weights.get(i).get_weight()){
                days ++;
            } else {
                return days;
            }
        }
        return 0;
    }

    private static int LossStreak(ArrayList<Weight> weights){
        ArrayList<Weight> _weights = new ArrayList<>(weights);
        Collections.reverse(_weights);
        int days= 0;
        for (int i=0; i < _weights.size()-1; i++){
            if(_weights.get(i+1).get_weight() > _weights.get(i).get_weight()){
                days ++;
            } else {
                return days;
            }
        }
        return 0;
    }

    private static double WeightChangeOverLastWeek( ArrayList<Weight> weights){
        double change = 0;
        int i;
        ArrayList<Weight> _weights = new ArrayList<>(weights);
        Collections.reverse(_weights);
        Collections.reverse(weights);

        try{
            long testDate = _weights.get(0).get_date() - (7 * 86400000);
            //If the second to most recent date is out of the range
            //Then there is no change as only 1 date is within range
            if(_weights.get(1).get_date() < testDate) {
                return 0;
            }
            for (i = 1; _weights.get(i).get_date() >= testDate;i++){
                if((i == _weights.size()-1) || (_weights.get(i).get_date() <= testDate)){
                    change = _weights.get(0).get_weight() - _weights.get(i).get_weight();
                    return change;
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return change;
    }

    public static String CurrentStreak(ArrayList<Weight> weights){
        Collections.reverse(weights);
        try {
            if (weights.get(1) == null) {
                return null;
            }
            String result;
            int gain = GainStreak(weights);
            int loss = LossStreak(weights);
            if (gain > 2) {
                result = String.format(Locale.ENGLISH, _context.getText(R.string.gain_streak).toString(), gain);
            } else if (loss > 2) {
                result = String.format(Locale.ENGLISH, _context.getText(R.string.loss_streak).toString(), loss);
            } else {
                result = _context.getText(R.string.no_streak).toString();
            }
            return result;
        } catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String[] WeekAverageChange(ArrayList<Weight> weights){
        double fResult = WeightChangeOverLastWeek(weights);
        String sResult;
        String[] result = new String[2];
        if(fResult < 0){
            sResult = String.format(Locale.ENGLISH, "%.1f", fResult);
            result[1] = _context.getText(R.string.keep_it_up).toString();
        } else {
            sResult = String.format(Locale.ENGLISH, "+%.1f", fResult);
            result[1] = _context.getText(R.string.keep_pushing).toString();
        }

        result[0] = String.format(Locale.ENGLISH, _context.getText(R.string.weight_change_last_week).toString(), sResult,PreferenceManager.get_weightUnit());

        return result;
    }
}
