package net.rodolfoboffo.indicadorrb.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.rodolfoboffo.indicadorrb.R;

public class IndicadorActivity extends AbstractBaseActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_indicador;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Boolean verificarBluetooth() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    private void listarDispositivos(boolean enable) {

    }

    public void listarDispositivosButtonClick(View view) {
        this.verificarBluetooth();
    }
}
