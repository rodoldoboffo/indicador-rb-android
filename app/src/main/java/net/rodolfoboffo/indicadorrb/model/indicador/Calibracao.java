package net.rodolfoboffo.indicadorrb.model.indicador;

import android.database.Observable;
import android.databinding.ObservableField;

import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;

public class Calibracao {
    protected ObservableField<String> nome;
    protected ObservableField<GrandezaEnum> grandeza;

    public Calibracao() {
        this.nome = new ObservableField<>("");
        this.grandeza = new ObservableField<>(GrandezaEnum.forca);
    }
}
