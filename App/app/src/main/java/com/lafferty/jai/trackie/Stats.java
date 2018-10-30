package com.lafferty.jai.trackie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class Stats {

    public static long getLongDateWithoutTime(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    private static int GainStreak(ArrayList<Weight> weights){
        int days= 0;
        for (int i=0; i < weights.size()-1; i++){
            if(weights.get(i+1).get_weight() < weights.get(i).get_weight()){
                days ++;
            } else {
                days = 0;
            }
        }
        return days;
    }

    private static int LossStreak(ArrayList<Weight> weights){
        int days= 0;
        for (int i=0; i < weights.size()-1; i++){
            if(weights.get(i+1).get_weight() > weights.get(i).get_weight()){
                days ++;
            } else {
                days = 0;
            }
        }
        return days;
    }

    private static float WeightChangeOverLastWeek( ArrayList<Weight> weights){
        float change = 0;
        Collections.reverse(weights);

        try{
            long testDate = weights.get(0).get_date() - (7 * 86400000);
            for (int i=1; i<weights.size(); i++){
                if (weights.get(i).get_date() < testDate){
                } else if (weights.get(i-1).get_date() > testDate){
                    change = weights.get(i-1).get_weight()-weights.get(0).get_weight();
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
            if (weights.get(0).get_weight() > weights.get(1).get_weight()) {
                result = String.format(Locale.ENGLISH, "You are %d days into a weight gain streak!", GainStreak(weights));
            } else if (weights.get(0).get_weight() < weights.get(1).get_weight()) {
                result = String.format(Locale.ENGLISH, "You are %d days into a weight loss streak!", LossStreak(weights));
            } else {
                result = "No current streak";
            }
            return result;
        } catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String[] WeekAverageChange(ArrayList<Weight> weights){
        float fResult = WeightChangeOverLastWeek(weights);
        String sResult;
        String[] result = new String[2];
        if(fResult > 0){
            sResult = String.format(Locale.ENGLISH, "+%.1f", fResult);
            result[1] = "Keep it up!";
        } else {
            sResult = String.valueOf(fResult);
            result[1] = "You can do it keep pushing!";
        }

        result[0] = String.format(Locale.ENGLISH, "Over the last week, your average daily weight change is: %s", sResult);

        return result;
    }
}
