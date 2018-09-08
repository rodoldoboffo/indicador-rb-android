package net.rodolfoboffo.indicadorrb.model.condicionador.calibracao;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractEnumeration;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.json.POJO;

import java.util.ArrayList;
import java.util.List;

public class CalibracaoPOJO extends POJO<Calibracao> {

    private String id;
    private String nome;
    private String grandeza;
    private String unidade;
    private Boolean selecionado;
    private List<PontoCalibracaoPOJO> pontosCalibracao;
    private RetaPOJO ajuste;

    public CalibracaoPOJO() {}

    public CalibracaoPOJO (Calibracao calibracao) {
        this();
        this.setId(calibracao.getId().toString());
        this.setNome(calibracao.getNome().get());
        this.setGrandeza(calibracao.getGrandeza().get().getCodigo());
        this.setUnidade(calibracao.getUnidadeCalibracao().get().getCodigo());
        this.setSelecionado(calibracao.getSelecionado().get());
        List<PontoCalibracaoPOJO> pontos = new ArrayList<>();
        for (PontoCalibracao p : calibracao.getPontosCalibracao()) {
            pontos.add(new PontoCalibracaoPOJO(p));
        }
        this.setPontosCalibracao(pontos);
        this.setAjuste(new RetaPOJO(calibracao.getAjuste().get()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrandeza() {
        return grandeza;
    }

    public void setGrandeza(String grandeza) {
        this.grandeza = grandeza;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public List<PontoCalibracaoPOJO> getPontosCalibracao() {
        return pontosCalibracao;
    }

    public void setPontosCalibracao(List<PontoCalibracaoPOJO> pontosCalibracao) {
        this.pontosCalibracao = pontosCalibracao;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public void setAjuste(RetaPOJO reta) {
        this.ajuste = reta;
    }

    public RetaPOJO getAjuste() {
        return this.ajuste;
    }

    public Calibracao convertToModel() {
        Calibracao c = new Calibracao(this.getId());
        c.setNome(this.getNome());
        c.setGrandeza(AbstractEnumeration.getByKey(GrandezaEnum.class, this.getGrandeza()));
        c.setUnidadeCalibracao(AbstractEnumeration.getByKey(UnidadeEnum.class, this.getUnidade()));
        c.setSelecionado(this.selecionado);
        for (PontoCalibracaoPOJO p : this.pontosCalibracao) {
            c.adicionaPontoCalibracao(p.convertToModel());
        }
        c.setAjuste(this.ajuste.convertToModel());
        return c;
    }
}
