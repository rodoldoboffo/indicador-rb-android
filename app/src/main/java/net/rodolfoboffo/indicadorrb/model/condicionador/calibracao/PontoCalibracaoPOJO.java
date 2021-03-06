package net.rodolfoboffo.indicadorrb.model.condicionador.calibracao;

import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.PontoCalibracao;
import net.rodolfoboffo.indicadorrb.model.json.POJO;

public class PontoCalibracaoPOJO extends POJO<PontoCalibracao> {

    private double valorDigital;
    private double valorCalibrado;

    public PontoCalibracaoPOJO() {}

    public PontoCalibracaoPOJO(PontoCalibracao objeto) {
        this.valorDigital = objeto.getValorNaoCalibrado().get();
        this.valorCalibrado = objeto.getValorCalibrado().get();
    }

    public double getValorDigital() {
        return valorDigital;
    }

    public void setValorDigital(long valorDigital) {
        this.valorDigital = valorDigital;
    }

    public double getValorCalibrado() {
        return valorCalibrado;
    }

    public void setValorCalibrado(double valorCalibrado) {
        this.valorCalibrado = valorCalibrado;
    }

    @Override
    public PontoCalibracao convertToModel() {
        return new PontoCalibracao(valorDigital, valorCalibrado);
    }
}
