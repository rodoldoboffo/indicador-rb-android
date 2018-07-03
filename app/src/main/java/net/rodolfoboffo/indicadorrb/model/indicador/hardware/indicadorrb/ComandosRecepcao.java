package net.rodolfoboffo.indicadorrb.model.indicador.hardware.indicadorrb;

public class ComandosRecepcao {

    public static final String STARTED = "started";
    public static final String STOPPED = "stopped";
    public static final String AD_VALUE = "adval=";

    public static void processaComando(IndicadorRB indicador, String comando) {
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
        else if (comando.startsWith(ComandosRecepcao.STARTED)) {
            indicador.setAquisicaoAutomatica(true);
        }
        else if (comando.startsWith(ComandosRecepcao.STOPPED)) {
            indicador.setAquisicaoAutomatica(false);
        }
    }

}
