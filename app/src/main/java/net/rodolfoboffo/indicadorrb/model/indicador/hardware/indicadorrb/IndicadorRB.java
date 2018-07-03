package net.rodolfoboffo.indicadorrb.model.indicador.hardware.indicadorrb;

import android.databinding.Observable;
import android.databinding.ObservableField;

import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.model.dispositivos.Mensagem;
import net.rodolfoboffo.indicadorrb.model.indicador.AbstractIndicador;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class IndicadorRB extends AbstractIndicador {

    private StringBuffer buffer;
    private ObservableField<String> ultimoComandoRecebido;

    public IndicadorRB(DispositivoBLE dispositivo, IndicadorService service) {
        super(dispositivo, service);
        this.buffer = new StringBuffer();
        this.ultimoComandoRecebido = new ObservableField<>();

        this.dispositivo.getPronto().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {

            }
        });

        this.dispositivo.getMensagemRecebida().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Mensagem m = ((ObservableField<Mensagem>) sender).get();
                synchronized (IndicadorRB.this.buffer) {
                    IndicadorRB.this.buffer.append(m.getTexto());
                    IndicadorRB.this.processaBuffer();
                }
            }
        });

        this.ultimoComandoRecebido.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                String comando = ((ObservableField<String>)sender).get();
                ComandosRecepcao.processaComando(IndicadorRB.this,  comando);
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

    @Override
    public void finalizar() {
        this.dispositivo.desconectar();
    }

    @Override
    public void inicializar() {
        this.dispositivo.conectar();
    }

    private void enviaComando(String comando) {
        String comandoComSeparador = String.format("%s%s%s", Comandos.SEPARADOR, comando, Comandos.SEPARADOR);
        this.dispositivo.enviarDados(comandoComSeparador);
    }

    @Override
    public void iniciarAquisicaoAutomatica(Boolean iniciar) {
        if (iniciar) {
            this.enviaComando(ComandosEnvio.START);
        }else {
            this.enviaComando(ComandosEnvio.STOP);
        }
    }
}
