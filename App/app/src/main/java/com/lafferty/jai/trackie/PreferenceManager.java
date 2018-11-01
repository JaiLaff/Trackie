package com.lafferty.jai.trackie;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static SharedPreferences _prefs;
    private static SharedPreferences.Editor _editor;
    private static Context _context;

    public static void Initialise(Context context, String filename){
        _context = context;
        _prefs = _context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        _editor = _prefs.edit();
    }

    public static void Wipe(){
        _editor.clear();
        _editor.commit();
    }

    public static void commitChanges(){
        _editor.apply();
    }

    public static boolean is_metric(){
        //defaults to metric because imperial is silly
        return _prefs.getBoolean("metric", true);
    }

    public static void set_metric(Boolean bool){
        _editor.putBoolean("metric", bool);
    }

    public static String get_name() {
        return _prefs.getString("name", "");
    }

    public static void set_name(String name) {
        _editor.putString("name", name);
    }

    public static int get_age() {
        return _prefs.getInt("age", 0);
    }

    public static void set_age(int age) {
        _editor.putInt("age", age);
    }

    public static int get_height() {
        return _prefs.getInt("height", 0);
    }

    public static void set_height(int height) {
        _editor.putInt("height", height);
    }

    public static String get_gender() {
       return _prefs.getString("gender", "");
    }

    public static void set_gender(String gender) {
        _editor.putString("gender", gender);
    }

    public static String get_weightUnit(){
        String result;
        result = is_metric() ? "kgs" : "lbs";
        return result;
    }

    public static String get_heightUnit(){
        String result;
        result = is_metric() ? "cm" : "inch";
        return result;
    }
}
