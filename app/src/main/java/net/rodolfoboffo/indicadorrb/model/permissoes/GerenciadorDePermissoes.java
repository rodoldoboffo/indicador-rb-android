package net.rodolfoboffo.indicadorrb.model.permissoes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class GerenciadorDePermissoes {

    private IndicadorService servico;

    public GerenciadorDePermissoes(IndicadorService servico) {
        this.servico = servico;
    }

    public boolean possuiPermissaoDeLocalidade() {
        return ContextCompat.checkSelfPermission(this.servico, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
