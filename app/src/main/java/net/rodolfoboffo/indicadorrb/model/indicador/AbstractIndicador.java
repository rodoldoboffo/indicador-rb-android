package net.rodolfoboffo.indicadorrb.model.indicador;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public abstract class AbstractIndicador extends AbstractServiceRelatedObject {

    protected DispositivoBLE dispositivo;
    protected ObservableDouble ultimoValorLido;
    protected ObservableBoolean aquisicaoAutomatica;

    protected AbstractIndicador(IndicadorService service) {
        super(service);
    }

    public AbstractIndicador(DispositivoBLE dispositivo, IndicadorService service) {
        super(service);
        this.dispositivo = dispositivo;
        this.ultimoValorLido = new ObservableDouble(Double.NaN);
        this.aquisicaoAutomatica = new ObservableBoolean(false);
    }

    public DispositivoBLE getDispositivo() {
        return dispositivo;
    }

    public ObservableDouble getUltimoValorLido() {
        return this.ultimoValorLido;
    }

    public ObservableBoolean getAquisicaoAutomatica() {
        return aquisicaoAutomatica;
    }

    public abstract void finalizar();

    public abstract void inicializar();

    public abstract void iniciarAquisicaoAutomatica(Boolean iniciar);
}
