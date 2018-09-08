package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.IListaItem;
import net.rodolfoboffo.indicadorrb.model.equipamento.Equipamento;

import java.util.List;

public class EquipamentoActivity extends AbstractListaItemActivity<Equipamento> {

    @Override
    protected int getSemItensStringResource() {
        return R.string.naoExistemEquipamentos;
    }

    @Override
    public ObservableList<Equipamento> getListaItens() {
        return this.service.getGerenciadorEquipamento().getListaObjetos();
    }

    @Override
    public void removerItem(Equipamento item) {
        super.removerItem(item);
        if (this.service != null) {
            this.service.getGerenciadorEquipamento().removerObjeto(item);
        }
    }

    @Override
    public void selecionarItem(Equipamento item) {
        super.selecionarItem(item);
        if (this.service != null) {
            this.service.getGerenciadorEquipamento().selecionaObjeto(item);
        }
    }

    @Override
    public void editarItem(Equipamento item) {
        super.editarItem(item);
        if (this.service != null) {
            EditarEquipamentoActivity.editarEquipamento(this, item);
        }
    }

    @Override
    public void onNovoItemButtonClick(View view) {
        super.onNovoItemButtonClick(view);
        if (this.service != null) {
            EditarEquipamentoActivity.novoEquipamento(this);
        }
    }
}
