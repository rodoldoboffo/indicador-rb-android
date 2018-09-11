package net.rodolfoboffo.indicadorrb.model.condicionador.calibracao;

import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.Reta;
import net.rodolfoboffo.indicadorrb.model.json.POJO;

public class RetaPOJO extends POJO<Reta> {

    private double a;
    private double b;

    public RetaPOJO() {};

    public RetaPOJO(Reta reta) {
        this.setA(reta.getA().get());
        this.setB(reta.getB().get());
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    @Override
    public Reta convertToModel() {
        Reta reta = new Reta(this.a, this.b);
        return reta;
    }
}
