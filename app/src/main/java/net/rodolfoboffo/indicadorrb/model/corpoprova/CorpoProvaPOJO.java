package net.rodolfoboffo.indicadorrb.model.corpoprova;

import net.rodolfoboffo.indicadorrb.model.json.POJO;

public class CorpoProvaPOJO extends POJO<CorpoProva> {

    private String id;
    private String nome;

    public CorpoProvaPOJO(CorpoProva o) {
        this.id = o.getId().toString();
        this.nome = o.getNome().get();
    }

    @Override
    public CorpoProva convertToModel() {
        CorpoProva o = new CorpoProva(this.id);
        o.setNome(this.nome);
        return o;
    }
}
