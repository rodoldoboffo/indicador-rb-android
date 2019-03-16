package net.rodolfoboffo.indicadorrb.model.corpoprova;

import com.google.gson.Gson;

import net.rodolfoboffo.indicadorrb.model.persistencia.GerenciadorDePersistencia;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class GerenciadorDeCorpoProva extends GerenciadorDePersistencia<CorpoProva, CorpoProvaPOJO> {

    public GerenciadorDeCorpoProva(IndicadorService servico) {
        super(servico);
    }

    @Override
    public String getNomeGerenciador() {
        return "CorpoProva";
    }

    @Override
    public CorpoProvaPOJO getObjetoPOJO(CorpoProva objeto) {
        return new CorpoProvaPOJO(objeto);
    }

    @Override
    public CorpoProvaPOJO getPojoFromJson(Gson gson, String stringJason) {
        return gson.fromJson(stringJason, CorpoProvaPOJO.class);
    }
}
