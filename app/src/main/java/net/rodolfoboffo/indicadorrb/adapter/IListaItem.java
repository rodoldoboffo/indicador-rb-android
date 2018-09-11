package net.rodolfoboffo.indicadorrb.adapter;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

public interface IListaItem {

    public String getNomeExibicaoLista();

    public Boolean isListsaItemSelecionado();

    public ObservableBoolean getSelecionado();

}
