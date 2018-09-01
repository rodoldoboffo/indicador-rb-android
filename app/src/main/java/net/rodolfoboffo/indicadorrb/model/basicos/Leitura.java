package net.rodolfoboffo.indicadorrb.model.basicos;

import android.databinding.ObservableDouble;
import android.databinding.ObservableField;

import java.time.LocalDateTime;
import java.util.Date;

public class Leitura {

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
}
