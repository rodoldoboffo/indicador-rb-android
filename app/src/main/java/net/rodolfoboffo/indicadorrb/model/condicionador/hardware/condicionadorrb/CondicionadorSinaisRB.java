package net.rodolfoboffo.indicadorrb.model.condicionador.hardware.condicionadorrb;

import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.model.dispositivos.Mensagem;
import net.rodolfoboffo.indicadorrb.model.condicionador.AbstractCondicionadorSinais;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class CondicionadorSinaisRB extends AbstractCondicionadorSinais {

    private StringBuffer buffer;
    private ObservableField<String> ultimoComandoRecebido;
    private ObservableBoolean estadoRelay;

    public CondicionadorSinaisRB(DispositivoBLE dispositivo, IndicadorService service) {
        super(dispositivo, service);
        this.buffer = new StringBuffer();
        this.ultimoComandoRecebido = new ObservableField<>();
        this.estadoRelay = new ObservableBoolean(false);

        this.conexao.getPronto().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {

            }
        });

        this.conexao.getMensagemRecebida().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Mensagem m = ((ObservableField<Mensagem>) sender).get();
                synchronized (CondicionadorSinaisRB.this.buffer) {
                    CondicionadorSinaisRB.this.buffer.append(m.getTexto());
                    CondicionadorSinaisRB.this.processaBuffer();
                }
            }
        });

        this.ultimoComandoRecebido.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                String comando = ((ObservableField<String>)sender).get();
                ComandosRecepcao.processaComando(CondicionadorSinaisRB.this,  comando);
            }
        });
    }

    public void setUltimoValorLido(Double ultimoValorLido) {
        this.ultimoValorLido.set(ultimoValorLido);
    }

    public void setAquisicaoAutomatica(Boolean value) {
        this.aquisicaoAutomatica.set(value);
    }

    private synchronized void processaBuffer() {
        int separatorIndex = this.buffer.indexOf(Comandos.SEPARADOR);
        while (separatorIndex != -1) {
            String comando = this.buffer.substring(0, separatorIndex);
            this.ultimoComandoRecebido.set(comando);
            this.buffer.delete(0, separatorIndex+1);
            separatorIndex = this.buffer.indexOf(Comandos.SEPARADOR);
        }
    }

    public ObservableBoolean getEstadoRelay() {
        return estadoRelay;
    }

    public void setEstadoRelay(Boolean estado) {
        this.estadoRelay.set(estado);
    }

    @Override
    public void finalizar() {
        this.conexao.desconectar();
    }

    @Override
    public void inicializar() {
        this.conexao.conectar();
    }

    private void enviaComando(String comando) {
        String comandoComSeparador = String.format("%s%s%s", Comandos.SEPARADOR, comando, Comandos.SEPARADOR);
        this.conexao.enviarDados(comandoComSeparador);
    }

    @Override
    public void solicitarLeitura() {
        this.enviaComando(ComandosEnvio.REQUEST_AD_VALUE);
    }
}
