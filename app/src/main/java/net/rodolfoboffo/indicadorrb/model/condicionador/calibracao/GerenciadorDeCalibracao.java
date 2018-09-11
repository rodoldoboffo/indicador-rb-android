package net.rodolfoboffo.indicadorrb.model.condicionador.calibracao;

import com.google.gson.Gson;

import net.rodolfoboffo.indicadorrb.model.persistencia.GerenciadorDePersistencia;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class GerenciadorDeCalibracao extends GerenciadorDePersistencia<Calibracao, CalibracaoPOJO> {

    public GerenciadorDeCalibracao(IndicadorService servico) {
        super(servico);
    }

    @Override
    public String getNomeGerenciador() {
        return "Calibracoes";
    }

    @Override
    public CalibracaoPOJO getObjetoPOJO(Calibracao objeto) {
        return new CalibracaoPOJO(objeto);
    }

    @Override
    public CalibracaoPOJO getPojoFromJson(Gson gson, String stringJason) {
        return gson.fromJson(stringJason, CalibracaoPOJO.class);
    }
}
