package net.rodolfoboffo.indicadorrb.model.indicador;

import android.databinding.ObservableDouble;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public abstract class AbstractIndicador extends AbstractServiceRelatedObject {

    protected DispositivoBLE dispositivo;

    protected AbstractIndicador(IndicadorService service) {
        super(service);
    }

    public AbstractIndicador(DispositivoBLE dispositivo, IndicadorService service) {
        super(service);
        this.dispositivo = dispositivo;
    }

    public DispositivoBLE getDispositivo() {
        return dispositivo;
    }

    public abstract ObservableDouble getUltimoValorLido();

    public abstract void finalizar();

    public abstract  void inicializar();
}
