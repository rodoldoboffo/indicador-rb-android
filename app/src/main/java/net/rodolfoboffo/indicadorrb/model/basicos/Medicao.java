package net.rodolfoboffo.indicadorrb.model.basicos;

import android.databinding.ObservableDouble;
import android.databinding.ObservableField;

import net.rodolfoboffo.indicadorrb.model.math.ConversorUnidades;

public class Medicao {

    private ObservableDouble valor;
    private ObservableField<UnidadeEnum> unidade;

    public Medicao(Double valor, UnidadeEnum unidade) {
        this.valor = new ObservableDouble(valor);
        this.unidade = new ObservableField<>(unidade);
    }

    public ObservableDouble getValor() {
        return valor;
    }

    public ObservableField<UnidadeEnum> getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeEnum unidade) {
        this.unidade.set(unidade);
    }

    public void setValor(Double valor) {
        this.valor.set(valor);
    }

    public Medicao converte(UnidadeEnum unidadeEnum) {
        return new Medicao(ConversorUnidades.converte(
                                                    this.getValor().get(),
                                                    this.getUnidade().get(),
                                                    unidadeEnum),
                    unidadeEnum);
    }
}
