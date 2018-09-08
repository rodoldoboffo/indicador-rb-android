package net.rodolfoboffo.indicadorrb.model.equipamento;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractEnumeration;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.json.POJO;

public class EquipamentoPOJO extends POJO<Equipamento> {

    private String id;
    private String nome;
    private String grandeza;
    private String unidade;
    private Double capacidade;
    private Boolean selecionado;

    public EquipamentoPOJO(Equipamento o) {
        this.id = o.getId().toString();
        this.nome = o.getNome().get();
        this.grandeza = o.getGrandeza().get().getCodigo();
        this.unidade = o.getUnidade().get().getCodigo();
        this.capacidade = o.getCapacidade().get();
        this.selecionado = o.getSelecionado().get();
    }

    @Override
    public Equipamento convertToModel() {
        Equipamento o = new Equipamento(this.id);
        o.setNome(this.nome);
        o.setGrandeza(AbstractEnumeration.getByKey(GrandezaEnum.class, this.grandeza));
        o.setUnidade(AbstractEnumeration.getByKey(UnidadeEnum.class, this.unidade));
        o.setCapacidade(this.capacidade);
        o.setSelecionado(this.selecionado);
        return o;
    }
}
