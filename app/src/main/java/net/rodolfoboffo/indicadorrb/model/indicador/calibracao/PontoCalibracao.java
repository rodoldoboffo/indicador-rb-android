package net.rodolfoboffo.indicadorrb.model.indicador.calibracao;

import android.databinding.ObservableDouble;
import android.databinding.ObservableLong;

import java.io.Serializable;

public class PontoCalibracao implements Serializable, Cloneable {
    private ObservableLong valorNaoCalibrado;
    private ObservableDouble valorCalibrado;

    public ObservableLong getValorNaoCalibrado() {
        return valorNaoCalibrado;
    }

    public ObservableDouble getValorCalibrado() {
        return valorCalibrado;
    }

    public void setValorNaoCalibrado(Long valorNaoCalibrado) {
        this.valorNaoCalibrado.set(valorNaoCalibrado);
    }

    public void setValorCalibrado(Double valorCalibrado) {
        this.valorCalibrado.set(valorCalibrado);
    }

    public PontoCalibracao(Long valorNaoCalibrado, Double valorCalibrado) {
        this.valorCalibrado = new ObservableDouble(valorCalibrado);
        this.valorNaoCalibrado = new ObservableLong(valorNaoCalibrado);
    }

    public PontoCalibracao() {
        this(Long.MAX_VALUE, Double.NaN);
    }

    @Override
    protected PontoCalibracao clone() {
        return new PontoCalibracao(this.getValorNaoCalibrado().get(), this.getValorCalibrado().get());
    }
}
