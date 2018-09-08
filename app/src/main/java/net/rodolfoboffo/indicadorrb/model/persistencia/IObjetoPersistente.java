package net.rodolfoboffo.indicadorrb.model.persistencia;

import java.util.UUID;

public interface IObjetoPersistente {

    public String getNomePersistencia();

    public UUID getId();

    public void setObjetoSelecionado(Boolean selecionado);

    public Boolean isObjetoSelecionado();
}
