package net.rodolfoboffo.indicadorrb.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.ArrayDispositivosAdapter;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public class DispositivosActivity extends AbstractBaseActivity implements View.OnCreateContextMenuListener{
    private static UUID UART_SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static UUID UART_CHARACTERISTIC_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    private static final String DESCOBRIMENTO_BLUETOOTH_TAG = "DESCOBRIMENTO_BLUETOOTH";
    private static final String ENDERECOS_DISPOSITIVOS_BUNDLE_KEY = "ENDERECOS_DISPOSITIVOS";
    private static final int REQUISITAR_ATIVACAO_BLUETOOTH = 1;
    private static final int REQUISITAR_PERMISSOES_LOCALIDADE = 2;
    private static final long DURACAO_VERIFICACAO = 10000;

    private ProgressBar progressBarProcurandoDispositivos;
    private ListView listViewDispositivos;

    private Handler handler;
    private Boolean verificando;
    private ArrayDispositivosAdapter adapterDispositivos;
    private BluetoothDevice dispositivoSelecionado;
    private BLEScanCallback bleScanCallback = new BLEScanCallback();
    private BluetoothGattCallback gattCallback = new BLEGattCallback();

    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic uartCharacteristic;

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
        this.adapterDispositivos = new ArrayDispositivosAdapter(this);
        this.listViewDispositivos.setAdapter(this.adapterDispositivos);
        this.listViewDispositivos.setOnCreateContextMenuListener(this);

//        if (savedInstanceState != null && savedInstanceState.containsKey(ENDERECOS_DISPOSITIVOS_BUNDLE_KEY)) {
//            ArrayList<String> enderecos = savedInstanceState.getStringArrayList(ENDERECOS_DISPOSITIVOS_BUNDLE_KEY);
//            this.adicionarDispositivosNaLista(enderecos);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (this.service != null) {
            this.adapterDispositivos.setDispositivos(this.service.getGerenciadorDispositivos().getListaDispositivos());
        }
    }

    @Override
    public void inicializaObservadoresDoServico() {
        this.inicializaObservadorProgressBar();
        this.inicializaObservadorListaDispositivos();
    }

    private void inicializaObservadorProgressBar() {
        if (this.service != null) {
            this.service.getGerenciadorDispositivos().isAtualizando().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    DispositivosActivity.this.exibeProgressBarAtualizando(((ObservableBoolean) sender).get());
                }
            });
        }
    }

    private void inicializaObservadorListaDispositivos() {
        if (this.service != null) {
            this.service.getGerenciadorDispositivos().getListaDispositivos().addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<DispositivoBLE>>() {
                @Override
                public void onChanged(ObservableList<DispositivoBLE> sender) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeInserted(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeMoved(ObservableList<DispositivoBLE> sender, int fromPosition, int toPosition, int itemCount) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeRemoved(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }
            });
        }
    }
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putStringArrayList(ENDERECOS_DISPOSITIVOS_BUNDLE_KEY, this.getEnderecosDeDispositivosEncontrados());
//    }

//    private ArrayList<String> getEnderecosDeDispositivosEncontrados() {
//        ArrayList<String> enderecos = new ArrayList<>();
//        for (BluetoothDevice dispositivo : this.dispositivos) {
//            enderecos.add(dispositivo.getAddress());
//        }
//        return enderecos;
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_dispositivos, menu);
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.conectarDispositivo:
//                AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//                int position = contextMenuInfo.position;
//                BluetoothDevice dispositivo = this.dispositivos.get(position);
//                this.conectarDispositivo(dispositivo);
//                break;
//        }
//        return true;
//    }

    private void conectarDispositivo(BluetoothDevice dispositivo) {
        dispositivo.connectGatt(this, false, this.gattCallback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanDispositivos:
                return this.verificarDispositivosButtonClick();
            case R.id.limparDispositivos:
                return this.limparDispositivosButtonClick();
        }
        return super.onOptionsItemSelected(item);
    }

//    private Boolean verificarDispositivosButtonClick() {
//        if (verificando) {
//            Toast.makeText(this, R.string.procura_em_andamento, Toast.LENGTH_SHORT).show();
//        }
//        else if (!this.verificarBluetoothAtivado()) {
//            this.requisitarAtivacaoBluetooth();
//        }
//        else {
//            this.requisitarPermissoesDeLocalidade();
//        }
//        return true;
//    }
    private Boolean verificarDispositivosButtonClick() {
        if (this.service != null) {
            this.service.getGerenciadorDispositivos().atualizarListaDispositivos();
        }
        return true;
    }

    private Boolean limparDispositivosButtonClick() {
        if (this.service != null) {
            this.service.getGerenciadorDispositivos().limpaListaDispositivos();
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

    private void exibeProgressBarAtualizando(Boolean exibe) {
        int visibilidade = exibe ? View.VISIBLE : View.GONE;
        progressBarProcurandoDispositivos.setVisibility(visibilidade);
    }

    private void iniciaListagem() {
        Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Iniciando listagem de dispositivos.");
        verificando = true;
        this.getBluetoothAdapter().startLeScan(bleScanCallback);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DispositivosActivity.this.exibeProgressBarAtualizando(true);
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
                DispositivosActivity.this.exibeProgressBarAtualizando(false);
            }
        });
    }

    private BluetoothManager getBluetoothManager() {
        return (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return this.getBluetoothManager().getAdapter();
    }

//    private void adicionarDispositivosNaLista(List<String> addresses) {
//        if (addresses != null) {
//            for (String address : addresses) {
//                this.adicionarDispositivoNaLista(address);
//            }
//        }
//    }

    private BluetoothDevice getDispositivoBluetooth(String address) {
        if (verificarBluetoothAtivado()) {
            BluetoothAdapter btAdapter = getBluetoothAdapter();
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            return device;
        }
        return null;
    }

//    private void adicionarDispositivoNaLista(String address) {
//        if (verificarBluetoothAtivado()) {
//            BluetoothDevice device = this.getDispositivoBluetooth(address);
//            this.adicionarDispositivoNaLista(device);
//        }
//    }

//    private void adicionarDispositivoNaLista(DispositivoBLE device) {
//        synchronized (dispositivos) {
//            if (!dispositivos.contains(device)) {
//                dispositivos.add(device);
//                adapterDispositivos.notifyDataSetChanged();
//            }
//        }
//    }

    private class BLEScanCallback implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//            this.dispositivoEncontrado(device);
        }

//        private void dispositivoEncontrado(BluetoothDevice device) {
//            Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Dispositivo --> Nome:" + device.getName() +
//                    ", Address: " + device.getAddress());
//            adicionarDispositivoNaLista(device);
//        }
    }

    public void testUART() {
        if (gatt != null && uartCharacteristic != null) {
            Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Testando TX e RX");
            this.sendData("Pipoca".getBytes());
        }
    }

    public void sendData(byte[] data) {
        uartCharacteristic.setValue(data);
        gatt.writeCharacteristic(uartCharacteristic);
        Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Teste escrito");
    }

    private class BLEGattCallback extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTING) {
                Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Conectando...");
            }
            else if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (gatt.discoverServices()) {
                        Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Conectado!!!");
                        DispositivosActivity.this.gatt = gatt;
                        return;
                    }
                }
                Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Algo deu errado...");
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Desconectado :(");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Erro descobrindo serviços.");
                return;
            }
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService service : services) {
                Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, service.getUuid().toString());
            }
            DispositivosActivity.this.uartCharacteristic = gatt.getService(UART_SERVICE_UUID).getCharacteristic(UART_CHARACTERISTIC_UUID);
            Log.d(DESCOBRIMENTO_BLUETOOTH_TAG, "Serviços descobertos!");
            gatt.setCharacteristicNotification(DispositivosActivity.this.uartCharacteristic, true);
            DispositivosActivity.this.testUART();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] received = characteristic.getValue();
            try {
                Log.d(DESCOBRIMENTO_BLUETOOTH_TAG,"Recebido: " + new String(received, "ASCII"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
