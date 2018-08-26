package net.rodolfoboffo.indicadorrb.model.math;

import net.rodolfoboffo.indicadorrb.model.indicador.calibracao.PontoCalibracao;
import net.rodolfoboffo.indicadorrb.model.indicador.calibracao.Reta;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.List;

public class RegressaoLinear {

    public static Reta getAjuste(List<PontoCalibracao> pontos) {
        Reta ajuste = new Reta();
        SimpleRegression regressao = new SimpleRegression(true);
        for (PontoCalibracao ponto : pontos) {
            regressao.addData(ponto.getValorNaoCalibrado().get(), ponto.getValorCalibrado().get());
        }
        ajuste.setA(regressao.getSlope());
        ajuste.setB(regressao.getIntercept());
        return ajuste;
    }

}
