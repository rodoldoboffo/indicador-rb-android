package net.rodolfoboffo.indicadorrb.model.condicionador.hardware.condicionadorrb;

import android.util.Log;

import net.rodolfoboffo.indicadorrb.model.basicos.Leitura;

import java.time.LocalDateTime;
import java.util.Date;

public class ComandosRecepcao {

    public static final String AD_VALUE = "adval=";
    public static final String RELAY_ON = "rlyon";
    public static final String RELAY_OFF = "rlyoff";

    public static void processaComando(CondicionadorSinaisRB condicionador, String comando) {
        if (comando.startsWith(ComandosRecepcao.AD_VALUE)) {
            String adValueString = comando.substring(ComandosRecepcao.AD_VALUE.length());
            try {
                Double valorLido = Double.parseDouble(adValueString);
                Leitura leitura = new Leitura(valorLido, new Date());
                condicionador.setUltimaLeitura(leitura);
            }
            catch (NumberFormatException e) {
                Log.e(ComandosRecepcao.class.getName(), "Não foi possível interpretar leitura reebida.");
            }
        }
        else if (comando.startsWith(ComandosRecepcao.RELAY_ON)) {
            condicionador.setEstadoRelay(true);
        }
        else if (comando.startsWith(ComandosRecepcao.RELAY_OFF)) {
            condicionador.setEstadoRelay(false);
        }
    }

}
