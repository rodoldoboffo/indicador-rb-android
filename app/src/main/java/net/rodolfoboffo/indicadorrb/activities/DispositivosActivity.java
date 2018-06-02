package net.rodolfoboffo.indicadorrb.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.ArrayDispositivosAdapter;

import java.util.ArrayList;
import java.util.List;

public class DispositivosActivity extends AbstractBaseActivity implements View.OnCreateContextMenuListener{

    private static final String DESCOBRIMENTO_BLUETOOTH_TAG = "DESCOBRIMENTO_BLUETOOTH";
    private static final String ENDERECOS_DISPOSITIVOS_BUNDLE_KEY = "ENDERECOS_DISPOSITIVOS";
    private static final int REQUISITAR_ATIVACAO_BLUETOOTH = 1;
    private static final int REQUISITAR_PERMISSOES_LOCALIDADE = 2;
    private static final long DURACAO_VERIFICACAO = 10000;

    private ProgressBar progressBarProcurandoDispositivos;
    private ListView listViewDispositivos;

    private Handler handler;
    private Boolean verificando;
    private ArrayList<BluetoothDevice> dispositivos;
    private ArrayDispositivosAdapter adapterDispositivos;
    private BluetoothDevice dispositivoSelecionado;
    private BLEScanCallback bleScanCallback = new BLEScanCallback();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_dispositivos;
    }

    @Override
    protected int getOptionsMenu() {
        return R.menu.dispositivos_menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.verificando = false;
        this.handler = new Handler();
        this.progressBarProcurandoDispositivos = this.findViewById(R.id.progressProcurandoDispositivos);
        this.listViewDispositivos = this.findViewById(R.id.listViewDispositivos);
        this.dispositivos = new ArrayList<>();
        this.adapterDispositivos = new ArrayDispositivosAdapter(this, this.dispositivos);
        this.listViewDispositivos.setAdapter(this.adapterDispositivos);
        this.listViewDispositivos.setOnCreateContextMenuListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(ENDERECOS_DISPOSITIVOS_BUNDLE_KEY)) {
            ArrayList<String> enderecos = savedInstanceState.getStringArrayList(ENDERECOS_DISPOSITIVOS_BUNDLE_KEY);
            this.adicionarDispositivosNaLista(enderecos);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(ENDERECOS_DISPOSITIVOS_BUNDLE_KEY, this.getEnderecosDeDispositivosEncontrados());
    }

    private ArrayList<String> getEnderecosDeDispositivosEncontrados() {
        ArrayList<String> enderecos = new ArrayList<>();
        for (BluetoothDevice dispositivo : this.dispositivos) {
            enderecos.add(dispositivo.getAddress());
        }
        return enderecos;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_dispositivos, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanDispositivos:
                return this.verificarDispositivosButtonClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean verificarDispositivosButtonClick() {
        if (verificando) {
            Toast.makeText(this, R.string.procura_em_andamento, Toast.LENGTH_SHORT).show();
        }
        else if (!this.verificarBluetoothAtivado()) {
            this.requisitarAtivacaoBluetooth();
        }
        else {
            this.requisitarPermissoesDeLocalidade();
        }
        return true;
    }

    private void requisitarPermissoesDeLocalidade() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUISITAR_PERMISSOES_LOCALIDADE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUISITAR_PERMISSOES_LOCALIDADE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.listarDispositivos(true);
                }
                break;
        }
    }

    private void requisitarAtivacaoBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUISITAR_ATIVACAO_BLUETOOTH);
    }

    private Boolean verificarBluetoothAtivado() {
        BluetoothAdapter btAdapter = this.getBluetoothAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUISITAR_ATIVACAO_BLUETOOTH:
                this.resultAtivacaoBluetooth(resultCode, data);
                break;
        }
    }

    private void resultAtivacaoBluetooth(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.requisitarPermissoesDeLocalidade();
        }
    }

    private void listarDispositivos(final boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    terminaListagem();
                }
            }, DURACAO_VERIFICACAO);
            this.iniciaListagem();
        } else {
            this.terminaListagem();
        }
    }

    private void iniciaListagem() {
        Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Iniciando listagem de dispositivos.");
        verificando = true;
        this.getBluetoothAdapter().startLeScan(bleScanCallback);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarProcurandoDispositivos.setVisibility(View.VISIBLE);
            }
        });
    }

    private void terminaListagem() {
        Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Desligando listagem de dispositivos.");
        verificando = false;
        this.getBluetoothAdapter().stopLeScan(bleScanCallback);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarProcurandoDispositivos.setVisibility(View.GONE);
            }
        });
    }

    private BluetoothManager getBluetoothManager() {
        return (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return this.getBluetoothManager().getAdapter();
    }

    private void adicionarDispositivosNaLista(List<String> addresses) {
        if (addresses != null) {
            for (String address : addresses) {
                this.adicionarDispositivoNaLista(address);
            }
        }
    }

    private BluetoothDevice getDispositivoBluetooth(String address) {
        if (verificarBluetoothAtivado()) {
            BluetoothAdapter btAdapter = getBluetoothAdapter();
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            return device;
        }
        return null;
    }

    private void adicionarDispositivoNaLista(String address) {
        if (verificarBluetoothAtivado()) {
            BluetoothDevice device = this.getDispositivoBluetooth(address);
            this.adicionarDispositivoNaLista(device);
        }
    }

    private void adicionarDispositivoNaLista(BluetoothDevice device) {
        synchronized (dispositivos) {
            if (!dispositivos.contains(device)) {
                dispositivos.add(device);
                adapterDispositivos.notifyDataSetChanged();
            }
        }
    }

    private class BLEScanCallback implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            this.dispositivoEncontrado(device);
        }

        private void dispositivoEncontrado(BluetoothDevice device) {
            Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Dispositivo --> Nome:" + device.getName() +
                    ", Address: " + device.getAddress());
            adicionarDispositivoNaLista(device);
        }
    }
}
