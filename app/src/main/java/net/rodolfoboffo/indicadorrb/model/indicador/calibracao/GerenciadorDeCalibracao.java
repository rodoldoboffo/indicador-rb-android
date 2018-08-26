package net.rodolfoboffo.indicadorrb.model.indicador.calibracao;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.databinding.ObservableMap;
import android.util.Log;

import com.google.gson.Gson;

import net.rodolfoboffo.indicadorrb.model.dispositivos.GerenciadorDeDispositivos;
import net.rodolfoboffo.indicadorrb.model.json.CalibracaoPOJO;
import net.rodolfoboffo.indicadorrb.model.json.GsonUtil;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GerenciadorDeCalibracao {

    public static final String CALIBRACOES_KEY = "IndicadorService.Calibracoes";
    public static final String CONJUNTO_CALIBRACOES_KEY = "ConjuntoCalibracoes";

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
        this.adicionarCalibracao(calibracao, true);
        this.salvarCalibracoes();
    }

    public void adicionarCalibracao(Calibracao calibracao) {
        this.adicionarCalibracao(calibracao, false);
    }

    public void adicionarCalibracao(Calibracao calibracao, boolean selecionada) {
        this.mapaCalibracoes.put(calibracao.getId(), calibracao);
        if (selecionada) {
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

    public void salvarCalibracoes() {
        try {
            SharedPreferences preferences = this.servico.getSharedPreferences(CALIBRACOES_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            Gson gson = GsonUtil.getGsonInstance();
            Set<String> conjuntoCalibracoes = new HashSet<>();
            for (Calibracao c : this.getListaCalibracoes()) {
                conjuntoCalibracoes.add(gson.toJson(new CalibracaoPOJO(c)));
            }
            editor.putStringSet(CONJUNTO_CALIBRACOES_KEY, conjuntoCalibracoes);
            editor.commit();
            Log.d(this.getClass().getName(), "Calibrações salvas.");
        }
        catch (Exception e) {
            Log.e(this.getClass().getName(), "Não foi possível salvar calibrações");
        }
    }

    public void carregarCalibracoes() {
        try {
            SharedPreferences preferences = this.servico.getSharedPreferences(CALIBRACOES_KEY, Context.MODE_PRIVATE);
            Set<String> conjuntoCalibracoesJson = preferences.getStringSet(CONJUNTO_CALIBRACOES_KEY, null);
            if (conjuntoCalibracoesJson != null) {
                Gson gson = GsonUtil.getGsonInstance();
                for (String calibracaoJson : conjuntoCalibracoesJson) {
                    CalibracaoPOJO c = gson.fromJson(calibracaoJson, CalibracaoPOJO.class);
                    Calibracao calibracao = c.convertToModel();
                    this.adicionarCalibracao(calibracao);
                    if (calibracao.getSelecionado().get()) {
                        this.selecionaCalibracao(calibracao);
                    }
                }
            }
            Log.d(this.getClass().getName(), "Calibrações carregadas.");
        }
        catch (Exception e) {
            Log.e(this.getClass().getName(), "Não foi possível carregar calibrações");
        }
    }

    public Double getValorAjustado(Double valorDigital) {
        if (this.calibracaoSelecionada.get() != null && this.calibracaoSelecionada.get().getAjuste().get() != null) {
            return this.calibracaoSelecionada.get().getAjuste().get().getValorAjustado(valorDigital);
        }
        return valorDigital;
    }
}
