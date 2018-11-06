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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class BMIActivity extends AppCompatActivity implements IShareable{

    private DrawerLayout mDrawerLayout;
    private EditText etBmiWeight;
    private EditText etBmiHeight;
    private double bmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        bmi = 0;
        InitUI();
    }

    @Override
    public void onResume(){
        super.onResume();
        InitUI();
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
        if (PreferenceManager.get_name() == ""){
            headerName.setText(R.string.nav_header_error);
        } else {
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
                                break;
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

    public void CreateToolbar(){
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        actionbar.setTitle(getText(R.string.bmi_calc));
    }

    public void SetViews(){
        ArrayList<Weight> weights = new ArrayList<>(HomeActivity.get_weights());
        Collections.reverse(weights);

        TextView tvBmiWeight = findViewById(R.id.tvBmiWeight);
        TextView tvBmiHeight = findViewById(R.id.tvBmiHeight);
        tvBmiHeight.setText(String.format(Locale.ENGLISH,getText(R.string.height_with_unit).toString(),PreferenceManager.get_heightUnit()));
        tvBmiWeight.setText(String.format(Locale.ENGLISH,getText(R.string.weight_with_unit).toString(),PreferenceManager.get_weightUnit()));

        etBmiWeight = findViewById(R.id.etBmiWeight);
        etBmiHeight = findViewById(R.id.etBmiHeight);
        Double weight = weights.get(0).get_weight();
        double height = PreferenceManager.get_height();
        if (!PreferenceManager.is_metric()){height = Calc.CMToInch((int)height);}
        etBmiWeight.setText(String.format(Locale.ENGLISH, "%.2f", weight));
        etBmiHeight.setText(String.format(Locale.ENGLISH, "%.1f", height));

        TextView tvBmiTitle = findViewById(R.id.tvBmiTitle);
        TextView tvBmiBody = findViewById(R.id.tvBmiBody);
        tvBmiTitle.setText("");
        tvBmiBody.setText("");
    }

    public void CalculateBMI(){
        String strWeight = etBmiWeight.getText().toString();
        String strHeight = etBmiHeight.getText().toString();
        double weight = Double.valueOf(strWeight);
        double height = Double.valueOf(strHeight);

        double result = 0;
        if(!PreferenceManager.is_metric()){result = Calc.ImperialBMI(weight,height);}
        if(PreferenceManager.is_metric()){result = Calc.MetricBMI(weight,height);}
        bmi = result;
    }

    public void DisplayBmi(){
        TextView tvBmiTitle = findViewById(R.id.tvBmiTitle);
        TextView tvBmiBody = findViewById(R.id.tvBmiBody);
        String name = PreferenceManager.get_name();
        String titleText = String.format(Locale.ENGLISH, getText(R.string.bmi_main).toString(),bmi);
        tvBmiTitle.setText(titleText);

        String range = GenerateBmiRangeText();
        String bodyText = String.format(Locale.ENGLISH, getText(R.string.bmi_body).toString(),name,range);
        tvBmiBody.setText(bodyText);
    }

    public String GenerateBmiRangeText(){

        if(bmi < 18.5){return getText(R.string.underweight).toString();}
        if(bmi < 24.9){return getText(R.string.normal).toString();}
        if(bmi < 29.9){return getText(R.string.overweight).toString();}
        if(bmi < 34.9){return getText(R.string.obese).toString();}
        if(bmi >=34.9){return getText(R.string.extremely_obese).toString();}
        return "";
    }

    public void BMI(View v){
        CalculateBMI();
        DisplayBmi();
    }

    public void Details(View v){
        Intent i = new Intent(this, BmiDetailsActivity.class);
        startActivity(i);
    }

    public void Share(){
        String name = PreferenceManager.get_name();
        String strBmi = String.format(Locale.US,"%.2f", bmi);
        String result = String.format(Locale.US, getText(R.string.share_bmi).toString(), name, strBmi);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, result);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_title)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        if(this instanceof IShareable) {
            inflater.inflate(R.menu.action_bar, menu);
        } else {
            inflater.inflate(R.menu.action_bar_no_share, menu);
        }
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
                Share();
                return true;

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
