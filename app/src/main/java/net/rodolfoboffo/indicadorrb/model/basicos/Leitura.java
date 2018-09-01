package net.rodolfoboffo.indicadorrb.model.basicos;

import android.databinding.ObservableDouble;
import android.databinding.ObservableField;

import java.time.LocalDateTime;

public class Leitura {

    private ObservableDouble valor;
    private ObservableField<LocalDateTime> hora;

    public Leitura(Double valor, LocalDateTime hora) {
        this.valor = new ObservableDouble(valor);
        this.hora = new ObservableField<>(hora);
    }

    public ObservableDouble getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor.set(valor);
    }

    public ObservableField<LocalDateTime> getHora() {
        return hora;
    }

    public void setHora(LocalDateTime hora) {
        this.hora.set(hora);
    }
}
