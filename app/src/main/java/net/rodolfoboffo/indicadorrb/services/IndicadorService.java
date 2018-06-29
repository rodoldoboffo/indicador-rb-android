package net.rodolfoboffo.indicadorrb.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.ObservableField;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.model.indicador.AbstractIndicador;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.model.dispositivos.GerenciadorDeDispositivos;
import net.rodolfoboffo.indicadorrb.model.indicador.Indicador;
import net.rodolfoboffo.indicadorrb.model.permissoes.GerenciadorDePermissoes;

public class IndicadorService extends Service {

    private IndicadorServiceBinder binder;
    private GerenciadorDeDispositivos gerenciadorDispositivos;
    private GerenciadorDePermissoes gerenciadoPermissoes;
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
        this.indicador = new ObservableField<>();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    public final GerenciadorDeDispositivos getGerenciadorDispositivos() {
        return gerenciadorDispositivos;
    }

    public final GerenciadorDePermissoes getGerenciadorPermissoes() {
        return this.gerenciadoPermissoes;
    }

    public final ObservableField<AbstractIndicador> getIndicador() {
        return this.indicador;
    }

    public void iniciarIndicador(DispositivoBLE dispositivo) {
        if (this.indicador.get() != null) {
            this.indicador.get().finalizar();
        }
        this.indicador.set(new Indicador(dispositivo, this));
        this.indicador.get().inicializar();
    }
}
