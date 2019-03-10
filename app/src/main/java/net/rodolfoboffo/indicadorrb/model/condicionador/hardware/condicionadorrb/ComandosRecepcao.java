package net.rodolfoboffo.indicadorrb.model.condicionador.hardware.condicionadorrb;

import android.util.Log;

import net.rodolfoboffo.indicadorrb.model.basicos.Leitura;
import net.rodolfoboffo.indicadorrb.model.util.HexUtil;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;
import java.util.Date;

public class ComandosRecepcao {

    public static final String AD_VALUE = "adval=";
    public static final String STATE = "stt=";
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
        else if (comando.startsWith(ComandosRecepcao.STATE)) {
            String stateSubstring = comando.substring(ComandosRecepcao.STATE.length());
            try {
                byte[] stateHex = Hex.decodeHex(stateSubstring.toCharArray());
                byte[] valorAdBytes = Arrays.copyOfRange(stateHex, 0, 4);
                double valorLido = Double.valueOf(HexUtil.byteArrayToInt(valorAdBytes));
                Leitura leitura = new Leitura(valorLido, new Date());
                condicionador.setUltimaLeitura(leitura);
                byte[] estadoReleBytes = Arrays.copyOfRange(stateHex, 4, 5);
                boolean estadoRele = HexUtil.byteArrayToBoolean(estadoReleBytes);
                condicionador.setEstadoRelay(estadoRele);
            } catch (DecoderException e) {
                Log.e(ComandosRecepcao.class.getName(), "Não foi possível interpretar estado recebido.");
                e.printStackTrace();
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
