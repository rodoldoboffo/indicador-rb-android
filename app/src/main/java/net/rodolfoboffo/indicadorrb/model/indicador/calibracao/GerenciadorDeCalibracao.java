package net.rodolfoboffo.indicadorrb.model.indicador.calibracao;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.databinding.ObservableMap;

import net.rodolfoboffo.indicadorrb.model.dispositivos.GerenciadorDeDispositivos;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GerenciadorDeCalibracao {

    private IndicadorService servico;

    private ObservableArrayMap<UUID, Calibracao> mapaCalibracoes;

    private ObservableList<Calibracao> listaCalibracoes;

    private ObservableField<Calibracao> calibracaoSelecionada;

    public GerenciadorDeCalibracao(IndicadorService servico) {
        this.servico = servico;
        this.calibracaoSelecionada = new ObservableField<>();
        this.mapaCalibracoes = new ObservableArrayMap<>();
        this.listaCalibracoes = new ObservableArrayList<>();
        this.mapaCalibracoes.addOnMapChangedCallback(new ObservableMap.OnMapChangedCallback<ObservableMap<UUID, Calibracao>, UUID, Calibracao>() {
            @Override
            public void onMapChanged(ObservableMap<UUID, Calibracao> sender, UUID key) {
                GerenciadorDeCalibracao.this.listaCalibracoes.clear();
                GerenciadorDeCalibracao.this.listaCalibracoes.addAll(sender.values());
            }
        });
    }

    public ObservableField<Calibracao> getCalibracaoSelecionada() {
        return calibracaoSelecionada;
    }

    public Boolean validarNomeCalibracao(String nomeCalibracao, Calibracao calibracao) {
        for (Calibracao c : mapaCalibracoes.values()) {
            if (c.getNome().get().trim().equals(nomeCalibracao.trim()) && !c.equals(calibracao)) {
                return false;
            }
        }
        return true;
    }

    public void salvarCalibracao(Calibracao calibracao) {
        if (!mapaCalibracoes.containsKey(calibracao.getId())) {
            this.mapaCalibracoes.put(calibracao.getId(), calibracao);
            this.selecionaCalibracao(calibracao);
        }
    }

    public void selecionaCalibracao(Calibracao c) {
        if (this.calibracaoSelecionada.get() != null) {
            this.calibracaoSelecionada.get().setSelecionado(false);
        }
        c.setSelecionado(true);
        this.calibracaoSelecionada.set(c);
    }

    public Calibracao getCalibracao(String idCalibracao) {
        return this.getCalibracao(UUID.fromString(idCalibracao));
    }

    public Calibracao getCalibracao(UUID idCalibracao) {
        return this.mapaCalibracoes.get(idCalibracao);
    }

    public ObservableList<Calibracao> getListaCalibracoes() {
        return listaCalibracoes;
    }

    public void removerCalibracao(Calibracao c) {
        if (this.mapaCalibracoes.containsKey(c.getId())) {
            this.mapaCalibracoes.remove(c.getId());
        }
    }
}
