package com.lafferty.jai.trackie;

import java.lang.reflect.Array;

public class Calc {

    private static final double KG_TO_LBS_MULTIPLIER = 2.205;
    private static final double LBS_TO_KG_MULTIPLIER = 0.454;
    private static final int IMPERIAL_BMI_MULTIPLIER = 703;
    private static final double INCH_TO_CENTIMETER_MULTIPLIER = 2.54;
    private static final double CENTIMETER_TO_INCH_MULTIPLIER = 0.394;
    private static final double[] ACTIVITY_FACTORS = {1.2,1.375,1.55,1.725,1.9};

    public static double KgToPound(double kg){
        return kg*KG_TO_LBS_MULTIPLIER;
    }

    public static double PoundToKg(double lbs){
        return lbs*LBS_TO_KG_MULTIPLIER;
    }

    public static double InchToCM(int height){
        return height*INCH_TO_CENTIMETER_MULTIPLIER;
    }

    public static double CMToInch(int height){
        return height*CENTIMETER_TO_INCH_MULTIPLIER;
    }

    public static double MetricBMI(double weight, double height) {
        height = height/100;
        return weight / (height * height);
    }

    public static double MaintenanceCalories(double weight, int height, int age, String gender, int activityLevel){
        //http://www.checkyourhealth.org/eat-healthy/cal_calculator.php
        double result = 0;
        double activityFactor = ACTIVITY_FACTORS[activityLevel];
        double BMR = 0;
        if(PreferenceManager.is_metric()){
            weight = KgToPound(weight);
            height = (int)Math.floor(CMToInch(height));
        }

        if (gender == "Female") {
            BMR = 665 + (4.3 * weight) + (4.6 * height) - (4.7 * age);
        } else {
            //Assuming a male body type for "other" and "not selected"
            //Acts as a worst case scenario
            BMR = 66 + (6.3 * weight) + (12.9 * height) - (6.8 * age);
        }

        result = activityFactor * BMR;
        return result;
    }

    public static double ImperialBMI(double weight, double height){
        return IMPERIAL_BMI_MULTIPLIER * (weight / (height*height));
    }
}
