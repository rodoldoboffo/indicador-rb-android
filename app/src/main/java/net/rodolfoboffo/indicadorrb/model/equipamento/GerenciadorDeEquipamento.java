package net.rodolfoboffo.indicadorrb.model.equipamento;

import com.google.gson.Gson;

import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.Calibracao;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.CalibracaoPOJO;
import net.rodolfoboffo.indicadorrb.model.persistencia.GerenciadorDePersistencia;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class GerenciadorDeEquipamento extends GerenciadorDePersistencia<Equipamento, EquipamentoPOJO> {

    public GerenciadorDeEquipamento(IndicadorService servico) {
        super(servico);
    }

    @Override
    public String getNomeGerenciador() {
        return "Equipamentos";
    }

    @Override
    public EquipamentoPOJO getObjetoPOJO(Equipamento objeto) {
        return new EquipamentoPOJO(objeto);
    }

    @Override
    public EquipamentoPOJO getPojoFromJson(Gson gson, String stringJason) {
        return gson.fromJson(stringJason, EquipamentoPOJO.class);
    }
}
