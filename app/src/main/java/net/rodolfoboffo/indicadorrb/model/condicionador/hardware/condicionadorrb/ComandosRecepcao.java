package net.rodolfoboffo.indicadorrb.model.condicionador.hardware.condicionadorrb;

public class ComandosRecepcao {

    public static final String AD_VALUE = "adval=";
    public static final String RELAY_ON = "rlyon";
    public static final String RELAY_OFF = "rlyoff";

    public static void processaComando(CondicionadorSinaisRB indicador, String comando) {
        if (comando.startsWith(ComandosRecepcao.AD_VALUE)) {
            String adValueString = comando.substring(ComandosRecepcao.AD_VALUE.length());
            try {
                Double valorLido = Double.parseDouble(adValueString);
                indicador.setUltimoValorLido(valorLido);
            }
            catch (NumberFormatException e) {
                indicador.setUltimoValorLido(Double.NaN);
            }
        }
        else if (comando.startsWith(ComandosRecepcao.RELAY_ON)) {
            indicador.setEstadoRelay(true);
        }
        else if (comando.startsWith(ComandosRecepcao.RELAY_OFF)) {
            indicador.setEstadoRelay(false);
        }
    }

}
