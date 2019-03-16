package net.rodolfoboffo.indicadorrb.activities;

import android.databinding.ObservableList;
import android.view.View;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.corpoprova.CorpoProva;

public class CorpoProvaActivity extends AbstractListaItemActivity<CorpoProva> {

    @Override
    protected int getSemItensStringResource() {
        return R.string.naoExistemCorposProva;
    }

    @Override
    public ObservableList<CorpoProva> getListaItens() {
        return this.service.getGerenciadorCorpoProva().getListaObjetos();
    }

    @Override
    public void removerItem(CorpoProva item) {
        super.removerItem(item);
        if (this.service != null) {
            this.service.getGerenciadorCorpoProva().removerObjeto(item);
        }
    }

    @Override
    public void selecionarItem(CorpoProva item) {
        super.selecionarItem(item);
        if (this.service != null) {
            this.service.getGerenciadorCorpoProva().selecionaObjeto(item);
        }
    }

    @Override
    public void editarItem(CorpoProva item) {
        super.editarItem(item);
        if (this.service != null) {
            EditarCorpoProvaActivity.editarItem(this, item);
        }
    }

    @Override
    public void onNovoItemButtonClick(View view) {
        super.onNovoItemButtonClick(view);
        if (this.service != null) {
            EditarCorpoProvaActivity.novoItem(this);
        }
    }
}
