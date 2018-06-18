package net.rodolfoboffo.indicadorrb.model.dispositivos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.os.Handler;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class GerenciadorDeDispositivos {

    public static final int INICIANDO_ATUALIZACAO = 1;
    public static final int ATUALIZACAO_JA_INICIADA = 2;
    public static final int BLUETOOTH_NAO_ATIVADO = 3;
    public static final int SEM_PERMISSAO_DE_LOCALIDADE = 4;

    private ObservableList<DispositivoBLE> dispositivos;
    private ObservableBoolean atualizando;
    private Handler handler;
    private BluetoothManager btManager;
    private IndicadorService servico;

    public GerenciadorDeDispositivos(IndicadorService servico) {
        this.dispositivos = new ObservableArrayList<>();
        this.atualizando = new ObservableBoolean(false);
        this.handler = new Handler();
        this.servico = servico;
        this.btManager = (BluetoothManager) this.servico.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private BluetoothAdapter getBluetoothAdapter() {
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
            return ATUALIZACAO_JA_INICIADA;
        }
        else {
            if (!this.verificarBluetoothAtivado()) {
                return BLUETOOTH_NAO_ATIVADO;
            }
            if (!this.servico.getGerenciadorPermissoes().possuiPermissaoDeLocalidade()) {
                return SEM_PERMISSAO_DE_LOCALIDADE;
            }
            this.buscarDispositivos();
            return INICIANDO_ATUALIZACAO;
        }
    }

    private void buscarDispositivos() {
//        if (enable) {
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    terminaListagem();
//                }
//            }, DURACAO_VERIFICACAO);
//            this.iniciaListagem();
//        } else {
//            this.terminaListagem();
//        }
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

    private Boolean verificarBluetoothAtivado() {
        BluetoothAdapter btAdapter = this.getBluetoothAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            return false;
        }
        return true;
    }
}
