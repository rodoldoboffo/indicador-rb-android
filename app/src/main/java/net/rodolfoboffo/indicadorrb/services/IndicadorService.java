package net.rodolfoboffo.indicadorrb.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.model.dispositivos.GerenciadorDeDispositivos;
import net.rodolfoboffo.indicadorrb.model.permissoes.GerenciadorDePermissoes;

public class IndicadorService extends Service {

    private IndicadorServiceBinder binder;
    private GerenciadorDeDispositivos gerenciadorDispositivos;
    private GerenciadorDePermissoes gerenciadoPermissoes;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.binder = new IndicadorServiceBinder(this);
        this.gerenciadorDispositivos = new GerenciadorDeDispositivos(this);
        this.gerenciadoPermissoes = new GerenciadorDePermissoes(this);
        Log.d(this.getClass().getSimpleName(), "Criando nova instancia do serviço");
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
}
