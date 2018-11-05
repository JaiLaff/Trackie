package com.lafferty.jai.trackie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        createSettings();
    }

    public void NavigationSetUp(){
        mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        //Access the header layout
        View headerView = navigationView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.tvHeaderName);
        if (PreferenceManager.get_name() == ""){headerName.setText(R.string.nav_header_error);}
        else {
            String text = this.getText(R.string.nav_welcome).toString();
            headerName.setText(String.format(Locale.ENGLISH, text, PreferenceManager.get_name()));
        }
        //TODO: Set some cool text here, stats etc

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

    public void CreateToolbar(){
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        actionbar.setTitle(getText(R.string.settings).toString());
    }

    public void createSettings(){
        SettingsFragment settingsFrag = new SettingsFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.settingsFragPlaceholder, settingsFrag, "SettingsFrag");
        ft.commit();
        fm.executePendingTransactions();
    }

    public void createUserPrefs(){
        UserPrefsFragment prefsFrag = new UserPrefsFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.settingsFragPlaceholder, prefsFrag, "UserPrefsFragment");
        ft.commit();
        fm.executePendingTransactions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent i = new Intent(this, HomeActivity.class);
                startActivity(i);
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
