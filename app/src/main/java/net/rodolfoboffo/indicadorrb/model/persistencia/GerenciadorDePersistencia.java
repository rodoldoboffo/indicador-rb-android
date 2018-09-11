package net.rodolfoboffo.indicadorrb.model.persistencia;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.databinding.ObservableMap;
import android.util.Log;

import com.google.gson.Gson;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.model.json.GsonUtil;
import net.rodolfoboffo.indicadorrb.model.json.POJO;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class GerenciadorDePersistencia<T extends IObjetoPersistente, U extends POJO<T>> extends AbstractServiceRelatedObject {

    public static final String PREFERENCES_KEY = "IndicadorService.%s";
    public static final String CONJUNTO_OBJETOS_KEY = "Conjunto%s";

    private ObservableArrayMap<UUID, T> mapaObjetos;

    private ObservableList<T> listaObjetos;

    private ObservableField<T> objetoSelecionado;

    public abstract String getNomeGerenciador();

    public GerenciadorDePersistencia(IndicadorService servico) {
        super(servico);
        this.objetoSelecionado = new ObservableField<>();
        this.mapaObjetos = new ObservableArrayMap<>();
        this.listaObjetos = new ObservableArrayList<>();
        this.mapaObjetos.addOnMapChangedCallback(new ObservableMap.OnMapChangedCallback<ObservableMap<UUID, T>, UUID, T>() {
            @Override
            public void onMapChanged(ObservableMap<UUID, T> sender, UUID key) {
                GerenciadorDePersistencia.this.listaObjetos.clear();
                GerenciadorDePersistencia.this.listaObjetos.addAll(sender.values());
            }
        });
    }

    public ObservableField<T> getObjetoSelecionado() {
        return objetoSelecionado;
    }

    public Boolean validarNomeObjeto(String nomeObjeto, T objeto) {
        for (T o : mapaObjetos.values()) {
            if (o.getNomePersistencia().trim().equals(nomeObjeto.trim()) && !o.equals(objeto)) {
                return false;
            }
        }
        return true;
    }

    public void salvarObjeto(T objeto) {
        this.adicionarObjeto(objeto, true);
        this.persistirObjetos();
    }

    public void adicionarObjeto(T objeto) {
        this.adicionarObjeto(objeto, false);
    }

    public void adicionarObjeto(T objeto, boolean selecionada) {
        this.mapaObjetos.put(objeto.getId(), objeto);
        if (selecionada) {
            this.selecionaObjeto(objeto);
        }
    }

    public void selecionaObjeto(T objeto) {
        if (this.objetoSelecionado.get() != null) {
            this.objetoSelecionado.get().setObjetoSelecionado(false);
        }
        objeto.setObjetoSelecionado(true);
        this.objetoSelecionado.set(objeto);
        this.persistirObjetos();
    }

    public T getObjetoPorId(String id) {
        return this.getObjetoPorId(UUID.fromString(id));
    }

    public T getObjetoPorId(UUID id) {
        return this.mapaObjetos.get(id);
    }

    public ObservableList<T> getListaObjetos() {
        return listaObjetos;
    }

    public void removerObjeto(T o) {
        if (this.objetoSelecionado.get().equals(o)){
            this.objetoSelecionado.set(null);
        }
        if (this.mapaObjetos.containsKey(o.getId())) {
            this.mapaObjetos.remove(o.getId());
        }
        this.persistirObjetos();
    }

    private String getPreferencesKey() {
        return String.format(PREFERENCES_KEY, this.getNomeGerenciador());
    }

    private String getConjuntoObjetosKey() {
        return String.format(CONJUNTO_OBJETOS_KEY, this.getNomeGerenciador());
    }

    public void persistirObjetos() {
        try {
            SharedPreferences preferences = this.service.getSharedPreferences(this.getPreferencesKey(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            Gson gson = GsonUtil.getGsonInstance();
            Set<String> conjuntoObjetos = new HashSet<>();
            for (T o : this.getListaObjetos()) {
                conjuntoObjetos.add(gson.toJson(this.getObjetoPOJO(o)));
            }
            editor.putStringSet(this.getConjuntoObjetosKey(), conjuntoObjetos);
            editor.commit();
            Log.d(this.getClass().getName(), String.format("%s - persistencia OK.", this.getNomeGerenciador()));
        }
        catch (Exception e) {
            Log.e(this.getClass().getName(), String.format("%s - Não foi possível persistir objetos.", this.getNomeGerenciador()));
        }
    }

    public abstract U getObjetoPOJO(T objeto);

    public abstract U getPojoFromJson(Gson gson, String stringJason);

    public void carregarObjetos() {
        try {
            SharedPreferences preferences = this.service.getSharedPreferences(this.getPreferencesKey(), Context.MODE_PRIVATE);
            Set<String> conjuntoObjetosJson = preferences.getStringSet(this.getConjuntoObjetosKey(), null);
            if (conjuntoObjetosJson != null) {
                Gson gson = GsonUtil.getGsonInstance();
                for (String objetoJson : conjuntoObjetosJson) {
                    U pojo = this.getPojoFromJson(gson, objetoJson);
                    T objeto = pojo.convertToModel();
                    this.adicionarObjeto(objeto);
                    if (objeto.isObjetoSelecionado()) {
                        this.selecionaObjeto(objeto);
                    }
                }
            }
            Log.d(this.getClass().getName(), String.format("%s - Persistencia carregada.", this.getNomeGerenciador()));
        }
        catch (Exception e) {
            Log.e(this.getClass().getName(), String.format("%s - Não foi possível carregar persistencia.", this.getNomeGerenciador()));
        }
    }
}
