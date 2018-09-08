package net.rodolfoboffo.indicadorrb.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.ObservableField;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.model.condicionador.AbstractCondicionadorSinais;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.model.dispositivos.GerenciadorDeDispositivos;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.GerenciadorDeCalibracao;
import net.rodolfoboffo.indicadorrb.model.condicionador.hardware.condicionadorrb.CondicionadorSinaisRB;
import net.rodolfoboffo.indicadorrb.model.equipamento.GerenciadorDeEquipamento;
import net.rodolfoboffo.indicadorrb.model.indicador.IndicadorBase;
import net.rodolfoboffo.indicadorrb.model.permissoes.GerenciadorDePermissoes;

import java.util.UUID;

public class IndicadorService extends Service {

    public static final String MAIN_PREFERENCES_KEY = "IndicadorService.MainPreferences";
    public static final String BLE_ADDRESS_KEY = "BLEAddress";
    public static final String ID_CALIBRACAO_KEY = "NomeCalibracao";

    private IndicadorServiceBinder binder;
    private GerenciadorDeDispositivos gerenciadorDispositivos;
    private GerenciadorDePermissoes gerenciadoPermissoes;
    private GerenciadorDeCalibracao gerenciadorCalibracao;
    private GerenciadorDeEquipamento gerenciadorEquipamento;
    private ObservableField<AbstractCondicionadorSinais> condicionadorSinais;
    private IndicadorBase indicador;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public void onCreate() {
        Log.d(this.getClass().getSimpleName(), "Criando nova instancia do serviço");
        super.onCreate();
        this.binder = new IndicadorServiceBinder(this);
        this.gerenciadorDispositivos = new GerenciadorDeDispositivos(this);
        this.gerenciadoPermissoes = new GerenciadorDePermissoes(this);
        this.gerenciadorCalibracao = new GerenciadorDeCalibracao(this);
        this.gerenciadorCalibracao.carregarObjetos();
        this.gerenciadorEquipamento = new GerenciadorDeEquipamento(this);
        this.gerenciadorEquipamento.carregarObjetos();
        this.condicionadorSinais = new ObservableField<>();
        this.indicador = new IndicadorBase(this);
        this.carregarPreferencias();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        this.salvarPreferencias();
        this.gerenciadorCalibracao.persistirObjetos();
        return super.onUnbind(intent);
    }

    public void salvarPreferencias() {
        SharedPreferences preferences = this.getSharedPreferences(MAIN_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (this.condicionadorSinais.get() != null) {
            String enderecoBLE = this.condicionadorSinais.get().getConexao().getEndereco().get();
            editor.putString(BLE_ADDRESS_KEY, enderecoBLE);
        }
        if (this.gerenciadorCalibracao.getObjetoSelecionado().get() != null) {
            UUID idCalibracao = this.gerenciadorCalibracao.getObjetoSelecionado().get().getId();
            editor.putString(ID_CALIBRACAO_KEY, idCalibracao.toString());
        }
        editor.commit();
        Log.d(this.getClass().getName(), "Preferencias salvas.");
    }

    public void carregarPreferencias() {
        SharedPreferences preferences = this.getSharedPreferences(MAIN_PREFERENCES_KEY, MODE_PRIVATE);
        String enderecoBLE = preferences.getString(BLE_ADDRESS_KEY, null);
        String idCalibracaoString = preferences.getString(ID_CALIBRACAO_KEY, null);
        if (enderecoBLE != null) {
            DispositivoBLE conexao = new DispositivoBLE(null, enderecoBLE, this);
            AbstractCondicionadorSinais indicador = new CondicionadorSinaisRB(conexao, this);
            this.condicionadorSinais.set(indicador);
        }
        if (idCalibracaoString != null) {
            this.gerenciadorCalibracao.getObjetoSelecionado().set(this.gerenciadorCalibracao.getObjetoPorId(idCalibracaoString));
        }
        Log.d(this.getClass().getName(), "Preferencias carregadas.");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    public final GerenciadorDeDispositivos getGerenciadorConexoes() {
        return gerenciadorDispositivos;
    }

    public final GerenciadorDePermissoes getGerenciadorPermissoes() {
        return this.gerenciadoPermissoes;
    }

    public final ObservableField<AbstractCondicionadorSinais> getCondicionadorSinais() {
        return this.condicionadorSinais;
    }

    public final GerenciadorDeCalibracao getGerenciadorCalibracao() {
        return this.gerenciadorCalibracao;
    }

    public final GerenciadorDeEquipamento getGerenciadorEquipamento() {
        return this.gerenciadorEquipamento;
    }

    public final IndicadorBase getIndicador() {
        return this.indicador;
    }

    public ObservableField<AbstractCondicionadorSinais> criaCondicionadorSinais(DispositivoBLE dispositivo) {
        this.condicionadorSinais.set(new CondicionadorSinaisRB(dispositivo, this));
        this.salvarPreferencias();
        return this.condicionadorSinais;
    }
}
