package net.rodolfoboffo.indicadorrb.model.persistencia;

import java.util.UUID;

public interface IObjetoPersistente extends Cloneable {

    public String getNomePersistencia();

    public UUID getId();

    public void setObjetoSelecionado(Boolean selecionado);

    public Boolean isObjetoSelecionado();

    public IObjetoPersistente clone();
}
