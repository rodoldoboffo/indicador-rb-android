package net.rodolfoboffo.indicadorrb.model.basicos;

import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Representa uma leitura Digital do condicionador de sinais, junto com a data/hora
 * de aquisicao da leitura
 */
public class Leitura implements Comparable<Leitura> {

    private ObservableDouble valor;
    private ObservableField<Date> hora;

    public Leitura(Double valor, Date hora) {
        this.valor = new ObservableDouble(valor);
        this.hora = new ObservableField<>(hora);
    }

    public ObservableDouble getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor.set(valor);
    }

    public ObservableField<Date> getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora.set(hora);
    }

    @Override
    public int compareTo(@NonNull Leitura o) {
        return this.getHora().get().compareTo(o.getHora().get());
    }
}
