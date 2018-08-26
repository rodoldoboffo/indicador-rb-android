package net.rodolfoboffo.indicadorrb.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.ObservableField;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import net.rodolfoboffo.indicadorrb.model.indicador.AbstractIndicador;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.model.dispositivos.GerenciadorDeDispositivos;
import net.rodolfoboffo.indicadorrb.model.indicador.calibracao.Calibracao;
import net.rodolfoboffo.indicadorrb.model.indicador.calibracao.GerenciadorDeCalibracao;
import net.rodolfoboffo.indicadorrb.model.indicador.hardware.indicadorrb.IndicadorRB;
import net.rodolfoboffo.indicadorrb.model.json.CalibracaoPOJO;
import net.rodolfoboffo.indicadorrb.model.json.GsonUtil;
import net.rodolfoboffo.indicadorrb.model.permissoes.GerenciadorDePermissoes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class IndicadorService extends Service {

    public static final String MAIN_PREFERENCES_KEY = "IndicadorService.MainPreferences";
    public static final String BLE_ADDRESS_KEY = "BLEAddress";
    public static final String ID_CALIBRACAO_KEY = "NomeCalibracao";

    private IndicadorServiceBinder binder;
    private GerenciadorDeDispositivos gerenciadorDispositivos;
    private GerenciadorDePermissoes gerenciadoPermissoes;
    private GerenciadorDeCalibracao gerenciadorCalibracao;
    private ObservableField<AbstractIndicador> indicador;

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
        this.gerenciadorCalibracao.carregarCalibracoes();
        this.indicador = new ObservableField<>();
        this.carregarPreferencias();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        this.salvarPreferencias();
        this.gerenciadorCalibracao.salvarCalibracoes();
        return super.onUnbind(intent);
    }

    public void salvarPreferencias() {
        SharedPreferences preferences = this.getSharedPreferences(MAIN_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (this.indicador.get() != null) {
            String enderecoBLE = this.indicador.get().getConexao().getEndereco().get();
            editor.putString(BLE_ADDRESS_KEY, enderecoBLE);
        }
        if (this.gerenciadorCalibracao.getCalibracaoSelecionada().get() != null) {
            UUID idCalibracao = this.gerenciadorCalibracao.getCalibracaoSelecionada().get().getId();
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
            AbstractIndicador indicador = new IndicadorRB(conexao, this);
            this.indicador.set(indicador);
        }
        if (idCalibracaoString != null) {
            this.gerenciadorCalibracao.getCalibracaoSelecionada().set(this.gerenciadorCalibracao.getCalibracao(idCalibracaoString));
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

    public final ObservableField<AbstractIndicador> getIndicador() {
        return this.indicador;
    }

    public final GerenciadorDeCalibracao getGerenciadorCalibracao() {
        return this.gerenciadorCalibracao;
    }

    public ObservableField<AbstractIndicador> criaIndicador(DispositivoBLE dispositivo) {
        this.indicador.set(new IndicadorRB(dispositivo, this));
        return this.indicador;
    }
}
