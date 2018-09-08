package net.rodolfoboffo.indicadorrb.model.equipamento;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.adapter.IListaItem;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.persistencia.IObjetoPersistente;

import java.io.Serializable;
import java.util.UUID;

public class Equipamento implements Cloneable, Serializable, Comparable<Equipamento>, IListaItem, IObjetoPersistente {

    private UUID id;
    private ObservableField<String> nome;
    private ObservableField<GrandezaEnum> grandeza;
    private ObservableField<UnidadeEnum> unidade;
    private ObservableDouble capacidade;
    private ObservableBoolean selecionado;

    public Equipamento() {
        this(UUID.randomUUID());
    }

    public Equipamento(String id) {
        this(UUID.fromString(id));
    }

    public Equipamento(UUID id) {
        this.id = id;
        this.nome = new ObservableField<>("");
        this.grandeza = new ObservableField<>(GrandezaEnum.forca);
        this.unidade = new ObservableField<>(UnidadeEnum.kgf);
        this.capacidade = new ObservableDouble(100.0);
        this.selecionado = new ObservableBoolean(false);
    }

    public ObservableField<String> getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public ObservableField<GrandezaEnum> getGrandeza() {
        return grandeza;
    }

    public void setGrandeza(GrandezaEnum grandeza) {
        this.grandeza.set(grandeza);
    }

    public ObservableField<UnidadeEnum> getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeEnum unidade) {
        this.unidade.set(unidade);
    }

    public ObservableDouble getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Double capacidade) {
        this.capacidade.set(capacidade);
    }

    public ObservableBoolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado.set(selecionado);
    }

    @Override
    public int compareTo(@NonNull Equipamento o) {
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
    public Equipamento clone() {
        try {
            Equipamento e = (Equipamento) super.clone();
            e.id = this.getId();
            e.setNome(this.getNome().get());
            e.setGrandeza(this.getGrandeza().get());
            e.setUnidade(this.getUnidade().get());
            e.setSelecionado(this.getSelecionado().get());
            e.setCapacidade(this.getCapacidade().get());
            return e;
        }
        catch (CloneNotSupportedException ex) {
            Log.e(this.getClass().getName(), "Não foi possível clonar Equipamento");
            return null;
        }
    }

    public boolean equals(Object obj) {
        return this.id.equals(((Equipamento)obj).getId());
    }
}
