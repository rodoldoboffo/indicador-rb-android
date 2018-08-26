package net.rodolfoboffo.indicadorrb.model.condicionador.calibracao;

import android.databinding.ObservableDouble;

import java.io.Serializable;

public class Reta implements Serializable, Cloneable{

    private ObservableDouble a;
    private ObservableDouble b;

    public Reta() {
        this(1.0, 0.0);
    }

    public Reta(Double a, Double b) {
        this.a = new ObservableDouble(a);
        this.b = new ObservableDouble(b);
    }

    public ObservableDouble getA() {
        return a;
    }

    public void setA(Double a) {
        this.a.set(a);
    }

    public ObservableDouble getB() {
        return b;
    }

    public void setB(Double b) {
        this.b.set(b);
    }

    public Reta clone() {
        Reta clone = new Reta(this.a.get(), this.b.get());
        return clone;
    }

    public Double getValorAjustado(Double valor) {
        return valor * this.getA().get() + this.getB().get();
    }
}
