package net.rodolfoboffo.indicadorrb.model.corpoprova;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.adapter.IListaItem;
import net.rodolfoboffo.indicadorrb.model.persistencia.IObjetoPersistente;

import java.io.Serializable;
import java.util.UUID;

public class CorpoProva implements Cloneable, Serializable, Comparable<CorpoProva>, IListaItem, IObjetoPersistente {

    private UUID id;
    private ObservableField<String> nome;
    private ObservableBoolean selecionado;

    public CorpoProva() {
        this(UUID.randomUUID());
    }

    public CorpoProva(String id) {
        this(UUID.fromString(id));
    }

    public CorpoProva(UUID id) {
        this.id = id;
        this.nome = new ObservableField<>("");
        this.selecionado = new ObservableBoolean(false);
    }

    public ObservableField<String> getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public ObservableBoolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado.set(selecionado);
    }

    @Override
    public int compareTo(@NonNull CorpoProva o) {
        return this.getNome().get().compareTo(o.getNome().get());
    }

    @Override
    public String getNomeExibicaoLista() {
        return this.getNome().get();
    }

    @Override
    public Boolean isListsaItemSelecionado() {
        return this.getSelecionado().get();
    }

    @Override
    public String getNomePersistencia() {
        return this.getNome().get();
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public void setObjetoSelecionado(Boolean selecionado) {
        this.setSelecionado(selecionado);
    }

    @Override
    public Boolean isObjetoSelecionado() {
        return this.getSelecionado().get();
    }

    @Override
    public CorpoProva clone() {
        try {
            CorpoProva e = (CorpoProva) super.clone();
            e.id = this.getId();
            e.setNome(this.getNome().get());
            e.setSelecionado(this.getSelecionado().get());
            return e;
        }
        catch (CloneNotSupportedException ex) {
            Log.e(this.getClass().getName(), "Não foi possível clonar Copor de Prova");
            return null;
        }
    }

    public boolean equals(Object obj) {
        return this.id.equals(((CorpoProva)obj).getId());
    }
}
