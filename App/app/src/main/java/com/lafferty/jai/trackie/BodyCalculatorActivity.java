package com.lafferty.jai.trackie;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class BodyCalculatorActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Spinner activityLevelSpinner;
    private TextView tvDailyIntake;
    private TextView tvDays;
    private EditText etDeficit;
    private EditText etGoal;
    private double weight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_calculator);
        InitUI();
    }

    @Override
    public void onResume(){
        super.onResume();
        InitUI();
        InitSpinner();
    }

    public void InitUI(){
        NavigationSetUp();
        CreateToolbar();
        SetViews();
    }

    public void NavigationSetUp(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        //Access the header layout
        View headerView = navigationView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.tvHeaderName);
        if (PreferenceManager.get_name().equals("")){headerName.setText(R.string.nav_header_error);}
        else {
            String text = this.getText(R.string.nav_welcome).toString();
            headerName.setText(String.format(Locale.ENGLISH, text, PreferenceManager.get_name()));
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        switch (menuItem.getItemId()){
                            case R.id.nav_home:
                                Intent home = new Intent(getBaseContext(),HomeActivity.class);
                                startActivity(home);
                            case R.id.nav_converter:
                                Intent body = new Intent(getBaseContext(),BodyCalculatorActivity.class);
                                startActivity(body);
                                break;
                            case R.id.nav_bmi_calc:
                                Intent bmi = new Intent(getBaseContext(),BMIActivity.class);
                                startActivity(bmi);
                                break;
                            case R.id.nav_history:
                                Intent history = new Intent(getBaseContext(),HistoryActivity.class);
                                startActivity(history);
                                break;
                            case R.id.nav_about:
                                Intent about = new Intent(getBaseContext(),AboutActivity.class);
                                startActivity(about);
                                break;
                        }


                        return false;
                    }
                });
    }

    public void CreateToolbar() {
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        actionbar.setTitle(getText(R.string.calculator));
    }

    public void SetViews(){
        activityLevelSpinner = findViewById(R.id.activityLevelSpinner);
        tvDailyIntake = findViewById(R.id.tvMaintenenceCalories);
        etGoal = findViewById(R.id.etGoalWeight);
        etDeficit = findViewById(R.id.etDeficit);
        tvDays = findViewById(R.id.tvDaysToReachGoal);
    }

    public void InitSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.activity_levels,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(adapter);

        activityLevelSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SetDailyIntake(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    public void SetDailyIntake(int spinnerPosition){
        String calories;
        Weight lastWeight = HomeActivity.get_recent_weight();
        if (lastWeight == null){
            tvDailyIntake.setText(getText(R.string.stats_error));
            return;
        }
        weight = lastWeight.get_weight();
        int height = PreferenceManager.get_height();
        int age = PreferenceManager.get_age();
        String gender = PreferenceManager.get_gender();

        calories = String.valueOf(Calc.MaintenanceCalories(weight, height, age, gender, spinnerPosition));
        String calorieText = getText(R.string.maintenence_calories).toString();
        tvDailyIntake.setText(String.format(Locale.US, calorieText, calories));
    }

    public void CalculateDays(View v){
        String strGoal = etGoal.getText().toString();
        String strDeficit = etDeficit.getText().toString();
        if (strGoal.equals("") || strDeficit.equals("")){
            Toast.makeText(this,R.string.body_calc_null, Toast.LENGTH_LONG).show();
            return;
        }
        double goal = Double.parseDouble(etGoal.getText().toString());
        int deficit = Integer.parseInt(etDeficit.getText().toString());
        int result = Calc.DaysToReachGoalWeight(deficit,weight,goal);
        tvDays.setText(String.format(Locale.US,getText(R.string.days_result).toString(),result));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;

            case R.id.action_share:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();

                return super.onOptionsItemSelected(item);

        }
    }
}
