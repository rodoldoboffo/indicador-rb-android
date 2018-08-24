package net.rodolfoboffo.indicadorrb.model.indicador.calibracao;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;

import java.io.Serializable;
import java.util.UUID;

public class Calibracao implements Cloneable, Serializable, Comparable<Calibracao> {
    protected UUID id;
    protected ObservableField<String> nome;
    protected ObservableField<GrandezaEnum> grandeza;
    protected ObservableField<UnidadeEnum> unidadeCalibracao;
    protected ObservableList<PontoCalibracao> pontosCalibracao;
    protected ObservableBoolean selecionado;

    public Calibracao() {
        this.id = UUID.randomUUID();
        this.nome = new ObservableField<>("");
        this.grandeza = new ObservableField<>(GrandezaEnum.forca);
        this.unidadeCalibracao = new ObservableField<>();
        this.unidadeCalibracao.set(UnidadeEnum.kgf);
        this.pontosCalibracao = new ObservableArrayList<>();
        this.selecionado = new ObservableBoolean(false);
    }

    public final UUID getId() {
        return this.id;
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

    public ObservableField<UnidadeEnum> getUnidadeCalibracao() {
        return unidadeCalibracao;
    }

    public void setUnidadeCalibracao(UnidadeEnum unidadeCalibracao) {
        this.unidadeCalibracao.set(unidadeCalibracao);
    }

    public void setSelecionado(Boolean selecioando) {
        this.selecionado.set(selecioando);
    }

    public ObservableBoolean getSelecionado() {
        return selecionado;
    }

    public ObservableList<PontoCalibracao> getPontosCalibracao() {
        return pontosCalibracao;
    }

    public Calibracao clone() {
        try {
            Calibracao clone = (Calibracao)super.clone();
            clone.setGrandeza(this.getGrandeza().get());
            clone.setNome(this.getNome().get());
            clone.setUnidadeCalibracao(this.getUnidadeCalibracao().get());
            clone.getPontosCalibracao().clear();
            for (int i = 0; i < this.getPontosCalibracao().size(); i++) {
                clone.adicionaPontoCalibracao(this.getPontosCalibracao().get(i).clone());
            }
            return clone;
        }
        catch (CloneNotSupportedException ex) {
            Log.e(this.getClass().getName(), "Não foi possível clonar Calibração");
            return null;
        }
    }

    public void adicionaPontoCalibracao(PontoCalibracao ponto) {
        this.getPontosCalibracao().add(ponto);
    }

    @Override
    public int compareTo(@NonNull Calibracao o) {
        return this.getNome().get().compareTo(o.getNome().get());
    }

    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((Calibracao)obj).getId());
    }
}
