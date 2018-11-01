package com.lafferty.jai.trackie;

public class Calc {

    private static final double KG_TO_LBS_MULTIPLIER = 2.205;
    private static final double LBS_TO_KG_MULTIPLIER = 0.454;
    private static final int IMPERIAL_BMI_MULTIPLIER = 703;
    private static final double INCH_TO_CENTIMETER_MULTIPLIER = 2.54;
    private static final double CENTIMETER_TO_INCH_MULTIPLIER = 0.394;

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
        return weight / (height * height);
    }

    public static double ImperialBMI(double weight, double height){
        return IMPERIAL_BMI_MULTIPLIER * (weight / (height*height));
    }
}
