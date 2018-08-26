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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Calibracao implements Cloneable, Serializable, Comparable<Calibracao> {
    protected UUID id;
    protected ObservableField<String> nome;
    protected ObservableField<GrandezaEnum> grandeza;
    protected ObservableField<UnidadeEnum> unidadeCalibracao;
    protected ObservableList<PontoCalibracao> pontosCalibracao;
    protected ObservableBoolean selecionado;
    protected ObservableField<Reta> ajuste;

    public Calibracao() {
        this(UUID.randomUUID().toString());
    }

    public Calibracao(String id) {
        this.id = UUID.fromString(id);
        this.nome = new ObservableField<>("");
        this.grandeza = new ObservableField<>(GrandezaEnum.forca);
        this.unidadeCalibracao = new ObservableField<>();
        this.unidadeCalibracao.set(UnidadeEnum.kgf);
        this.pontosCalibracao = new ObservableArrayList<>();
        this.selecionado = new ObservableBoolean(false);
        this.ajuste = new ObservableField<>(new Reta());
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

    public void setPontosCalibracao(List<PontoCalibracao> pontos) {
        this.pontosCalibracao.clear();
        this.pontosCalibracao.addAll(pontos);
    }

    public ObservableField<Reta> getAjuste() {
        return ajuste;
    }

    public void setAjuste(Reta ajuste) {
        this.ajuste.set(ajuste);
    }

    public Calibracao clone() {
        try {
            Calibracao clone = (Calibracao)super.clone();
            clone.id = this.getId();
            clone.setGrandeza(this.getGrandeza().get());
            clone.setNome(this.getNome().get());
            clone.setUnidadeCalibracao(this.getUnidadeCalibracao().get());
            List<PontoCalibracao> clonePontos = new ArrayList<>();
            for (int i = 0; i < this.getPontosCalibracao().size(); i++) {
                clonePontos.add(this.getPontosCalibracao().get(i).clone());
            }
            clone.setPontosCalibracao(clonePontos);
            clone.setAjuste((Reta) this.ajuste.get().clone());
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
