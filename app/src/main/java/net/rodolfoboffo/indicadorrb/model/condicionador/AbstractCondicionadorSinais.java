package net.rodolfoboffo.indicadorrb.model.condicionador;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public abstract class AbstractCondicionadorSinais extends AbstractServiceRelatedObject {

    protected DispositivoBLE conexao;
    protected ObservableDouble ultimoValorLido;
    protected ObservableBoolean aquisicaoAutomatica;

    protected AbstractCondicionadorSinais(IndicadorService service) {
        super(service);
    }

    public AbstractCondicionadorSinais(DispositivoBLE conexao, IndicadorService service) {
        super(service);
        this.conexao = conexao;
        this.ultimoValorLido = new ObservableDouble(Double.NaN);
        this.aquisicaoAutomatica = new ObservableBoolean(false);
    }

    public DispositivoBLE getConexao() {
        return conexao;
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
