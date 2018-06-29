package net.rodolfoboffo.indicadorrb.model.indicador;

import android.databinding.Observable;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.model.dispositivos.Mensagem;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class Indicador extends AbstractIndicador{

    private StringBuffer buffer;
    private ObservableDouble ultimoValorLido;

    public Indicador(DispositivoBLE dispositivo, IndicadorService service) {
        super(dispositivo, service);
        this.buffer = new StringBuffer();
        this.ultimoValorLido = new ObservableDouble(Double.NaN);
        this.dispositivo.getPronto().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {

            }
        });
        this.dispositivo.getMensagemRecebida().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Mensagem m = ((ObservableField<Mensagem>) sender).get();
                synchronized (Indicador.this.buffer) {
                    Indicador.this.buffer.append(m.getTexto());
                    Indicador.this.processaBuffer();
                }
            }
        });
    }

    @Override
    public ObservableDouble getUltimoValorLido() {
        return this.ultimoValorLido;
    }

    private synchronized void processaBuffer() {
        int separatorIndex = this.buffer.indexOf(Comandos.SEPARADOR);
        while (separatorIndex != -1) {
            String comando = this.buffer.substring(0, separatorIndex);
            if (comando.startsWith(Comandos.AD_VALUE)) {
                String adValueString = comando.substring(Comandos.AD_VALUE.length());
                this.ultimoValorLido.set(Double.parseDouble(adValueString));
            }
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
}
