package net.rodolfoboffo.indicadorrb.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import net.rodolfoboffo.indicadorrb.R;

public abstract class AbstractBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    protected FrameLayout contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_base);

        contentFrame = (FrameLayout)this.findViewById(R.id.contentFrame);
        View contentView = this.getLayoutInflater().inflate(this.getLayoutResourceId(), contentFrame);

        drawerLayout = (DrawerLayout)this.findViewById(R.id.drawerLayout);
        NavigationView navigationView = this.findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        int activityLabelRes = this.getActivityLabel();
        if (activityLabelRes != 0) {
            toolbar.setTitle(activityLabelRes);
        }
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START, false);
        switch (item.getItemId()) {
            case R.id.navIndicador:
                iniciaActivity(IndicadorActivity.class);
                break;
            case R.id.navDispositivos:
                iniciaActivity(DispositivosActivity.class);
                break;
            default:
                break;
        }
        return true;
    }

    protected boolean iniciaActivity(Class<? extends Activity> clss) {
        if (!this.getClass().equals(clss)) {
            this.startActivity(new Intent(this, clss));
            return true;
        }
        return false;
    }

    protected int getActivityLabel() {
        try {
            ActivityInfo activityInfo = this.getPackageManager().getActivityInfo(this.getComponentName(), PackageManager.GET_META_DATA);
            return activityInfo.labelRes;
        }
        catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        int menuRes = this.getOptionsMenu();
        if (menuRes != 0) {
            inflater.inflate(menuRes, menu);
        }
        return true;
    }

    protected int getOptionsMenu() {
        return 0;
    }

    protected abstract int getLayoutResourceId();
}
