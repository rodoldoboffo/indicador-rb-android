package net.rodolfoboffo.indicadorrb.activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;
import net.rodolfoboffo.indicadorrb.services.IndicadorServiceBinder;

public abstract class AbstractBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ServiceConnection {

    protected static final int REQUISITAR_ATIVACAO_BLUETOOTH = 1;
    protected static final int REQUISITAR_PERMISSOES_LOCALIDADE = 2;

    protected IndicadorService service;
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
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, IndicadorService.class);
        this.startService(intent);
        this.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.service != null) {
            unbindService(this);
            this.service = null;
        }
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
            case R.id.navConexoes:
                iniciaActivity(ConexoesActivity.class);
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

    protected void requisitarAtivacaoBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUISITAR_ATIVACAO_BLUETOOTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUISITAR_ATIVACAO_BLUETOOTH:
                this.resultAtivacaoBluetooth(resultCode, data);
                break;
        }
    }

    protected void resultAtivacaoBluetooth(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (this.service != null) {
                this.service.getGerenciadorConexoes().atualizarListaDispositivos();
            }
        }
    }

    protected void requisitarPermissoesDeLocalidade() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUISITAR_PERMISSOES_LOCALIDADE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUISITAR_PERMISSOES_LOCALIDADE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (this.service != null) {
                        this.service.getGerenciadorConexoes().atualizarListaDispositivos();
                    }
                }
                break;
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        IndicadorServiceBinder binder = (IndicadorServiceBinder)service;
        this.service = binder.getService();
        Log.d(this.getClass().getSimpleName(), "Serviço conectado.");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;
    }

    protected int getOptionsMenu() {
        return 0;
    }

    protected abstract int getLayoutResourceId();
}
