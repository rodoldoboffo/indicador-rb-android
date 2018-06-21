package net.rodolfoboffo.indicadorrb.model.dispositivos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Handler;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class GerenciadorDeDispositivos {

    public static final int RESPONSE_INICIANDO_ATUALIZACAO = 1;
    public static final int RESPONSE_ATUALIZACAO_JA_INICIADA = 2;
    public static final int RESPONSE_BLUETOOTH_NAO_ATIVADO = 3;
    public static final int RESPONSE_SEM_PERMISSAO_DE_LOCALIDADE = 4;

    public final int DURACAO_BUSCA_DISPOSITIVOS_MS = 10000;

    private ObservableList<DispositivoBLE> dispositivos;
    private ObservableBoolean atualizando;
    private Handler handler;
    private BluetoothManager btManager;
    private IndicadorService servico;
    private BLEScanCallback bleScanCallback;
    private ObservableField<DispositivoBLE> dispositivoAtual;

    public GerenciadorDeDispositivos(IndicadorService servico) {
        this.dispositivos = new ObservableArrayList<>();
        this.atualizando = new ObservableBoolean(false);
        this.handler = new Handler();
        this.servico = servico;
        this.dispositivoAtual = new ObservableField<>();
        this.dispositivoAtual.set(null);
        this.btManager = (BluetoothManager) this.servico.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bleScanCallback = new BLEScanCallback();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return this.btManager.getAdapter();
    }

    public final ObservableBoolean isAtualizando() {
        return this.atualizando;
    }

    public final ObservableList<DispositivoBLE> getListaDispositivos() {
        return this.dispositivos;
    }

    public void limpaListaDispositivos() {
        this._limpaListaDispositivos();
    }

    public int atualizarListaDispositivos() {
        if (this.atualizando.get()) {
            return RESPONSE_ATUALIZACAO_JA_INICIADA;
        }
        else {
            if (!this.verificarBluetoothAtivado()) {
                return RESPONSE_BLUETOOTH_NAO_ATIVADO;
            }
            if (!this.servico.getGerenciadorPermissoes().possuiPermissaoDeLocalidade()) {
                return RESPONSE_SEM_PERMISSAO_DE_LOCALIDADE;
            }
            this.buscarDispositivos();
            return RESPONSE_INICIANDO_ATUALIZACAO;
        }
    }

    private void buscarDispositivos() {
        Log.d(this.getClass().getSimpleName(), "Buscando dispositivos.");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                terminaListagem();
            }
        }, DURACAO_BUSCA_DISPOSITIVOS_MS);
        this.iniciaListagem();
    }

    private void iniciaListagem() {
        this.atualizando.set(true);
        this.getBluetoothAdapter().startLeScan(this.bleScanCallback);
    }

    private void terminaListagem() {
        this.atualizando.set(false);
        this.getBluetoothAdapter().stopLeScan(this.bleScanCallback);
    }

    private void adicionaDispositivo(DispositivoBLE dispositivo) {
        synchronized (this.dispositivos) {
            if (!this.dispositivos.contains(dispositivo)) {
                Log.d(GerenciadorDeDispositivos.this.getClass().getSimpleName(), "Adicionando novo dispositivo na lista.");
                this.dispositivos.add(dispositivo);
            }
        }
    }

    private void _limpaListaDispositivos() {
        synchronized (this.dispositivos) {
            this.dispositivos.clear();
        }
    }

    public Boolean verificarBluetoothAtivado() {
        BluetoothAdapter btAdapter = this.getBluetoothAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    private class BLEScanCallback implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            this.dispositivoEncontrado(device);
        }

        private void dispositivoEncontrado(BluetoothDevice device) {
            Log.d(GerenciadorDeDispositivos.this.getClass().getName(), "Dispositivo --> Nome:" + device.getName() +
                    ", Address: " + device.getAddress());
            DispositivoBLE dispositivo = new DispositivoBLE(device.getName(), device.getAddress(), GerenciadorDeDispositivos.this.servico);
            GerenciadorDeDispositivos.this.adicionaDispositivo(dispositivo);
        }
    }
}
