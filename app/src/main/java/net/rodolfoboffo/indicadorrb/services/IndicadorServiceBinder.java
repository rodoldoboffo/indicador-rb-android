package net.rodolfoboffo.indicadorrb.services;

import android.os.Binder;

public class IndicadorServiceBinder extends Binder {

    private IndicadorService service;

    public IndicadorServiceBinder(IndicadorService service) {
        this.service = service;
    }

    public IndicadorService getService() {
        return this.service;
    }
}
