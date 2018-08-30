package net.rodolfoboffo.indicadorrb.model.condicionador;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractCondicionadorSinais extends AbstractServiceRelatedObject {

    private Timer timerAquisicaoAutomatica;
    private TimerTask timerTask;
    protected DispositivoBLE conexao;
    protected ObservableDouble ultimoValorLido;
    protected ObservableBoolean aquisicaoAutomatica;

    protected AbstractCondicionadorSinais(IndicadorService service) {
        super(service);
    }

    public AbstractCondicionadorSinais(DispositivoBLE conexao, IndicadorService service) {
        super(service);
        this.timerAquisicaoAutomatica = new Timer("timerAquisicaoAutomaticaThread", true);
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

    public void setAquisicaoAutomatica(Boolean aquisicaoAutomatica) {
        this.aquisicaoAutomatica.set(aquisicaoAutomatica);
    }

    public abstract void finalizar();

    public abstract void inicializar();

    public abstract void solicitarLeitura();

    public void iniciarAquisicaoAutomatica(Boolean iniciar) {
        if (iniciar) {
            this.iniciarAquisicaoAutomatica();
        }
        else {
            this.pararAquisicaoAutomatica();
        }
    }

    private void iniciarAquisicaoAutomatica() {
        this.timerTask = new AquisicaoAutomaticaTask(this);
        this.timerAquisicaoAutomatica.schedule(this.timerTask, 500, 100);
        this.aquisicaoAutomatica.set(true);
    }

    private void pararAquisicaoAutomatica() {
        if (this.timerTask != null) {
            this.timerTask.cancel();
            this.timerTask = null;
        }
        this.aquisicaoAutomatica.set(false);
    }

    private class AquisicaoAutomaticaTask extends TimerTask {

        private AbstractCondicionadorSinais condicionador;

        public AquisicaoAutomaticaTask(AbstractCondicionadorSinais condicionador) {
            this.condicionador = condicionador;
        }

        @Override
        public void run() {
            this.condicionador.solicitarLeitura();
        }
    }
}
