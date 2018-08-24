package net.rodolfoboffo.indicadorrb.model.indicador.calibracao;

import android.databinding.ObservableDouble;

import java.io.Serializable;

public class PontoCalibracao implements Serializable, Cloneable {
    private ObservableDouble valorNaoCalibrado;
    private ObservableDouble valorCalibrado;

    public ObservableDouble getValorNaoCalibrado() {
        return valorNaoCalibrado;
    }

    public ObservableDouble getValorCalibrado() {
        return valorCalibrado;
    }

    public void setValorNaoCalibrado(Double valorNaoCalibrado) {
        this.valorNaoCalibrado.set(valorNaoCalibrado);
    }

    public void setValorCalibrado(Double valorCalibrado) {
        this.valorCalibrado.set(valorCalibrado);
    }

    public PontoCalibracao(Double valorNaoCalibrado, Double valorCalibrado) {
        this.valorCalibrado = new ObservableDouble(valorCalibrado);
        this.valorNaoCalibrado = new ObservableDouble(valorNaoCalibrado);
    }

    public PontoCalibracao() {
        this(Double.NaN, Double.NaN);
    }

    @Override
    protected PontoCalibracao clone() {
        return new PontoCalibracao(this.getValorNaoCalibrado().get(), this.getValorCalibrado().get());
    }
}
