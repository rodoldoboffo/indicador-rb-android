package net.rodolfoboffo.indicadorrb.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.corpoprova.CorpoProva;

public class EditarCorpoProvaActivity extends AbstractEditarItemPersistenteActivity<CorpoProva> {

    private EditText nomeCorpoProvaEditText;

    private CorpoProva item;

    @Override
    public CorpoProva getItem() {
        return item;
    }

    @Override
    public void setItem(CorpoProva item) {
        this.item = item;
    }

    @Override
    protected Class getItemClass() {
        return CorpoProva.class;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_editar_corpo_prova;
    }

    @Override
    protected int getNovoItemResource() {
        return R.string.novo_corpo_prova_activity_label;
    }

    @Override
    protected int getEditarItemResource() {
        return R.string.editar_corpo_prova_activity_label;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.nomeCorpoProvaEditText = this.findViewById(R.id.nomeCorpoProvaText);
        this.nomeCorpoProvaEditText.setText(this.getItem().getNome().get());
    }

    public Boolean validar() {
        if (this.nomeCorpoProvaEditText.getText().toString().isEmpty()) {
            this.nomeCorpoProvaEditText.setError(this.getString(R.string.nomeCorpoProvaVazioError));
            return false;
        }
        if (!this.service.getGerenciadorCorpoProva().validarNomeObjeto(this.nomeCorpoProvaEditText.getText().toString(), this.getItem())) {
            this.nomeCorpoProvaEditText.setError(this.getString(R.string.corpoProvaComMesmoNome));
            return false;
        }
        return true;
    }

    public static void novoItem(Activity context) {
        Intent intent = new Intent(context, EditarCorpoProvaActivity.class);
        Bundle bundle = new Bundle();
        context.startActivityForResult(intent, NOVO_ITEM, bundle);
    }

    public static void editarItem(Activity context, CorpoProva item) {
        Intent intent = new Intent(context, EditarCorpoProvaActivity.class);
        intent.putExtra(ITEM_EXTRA, item);
        Bundle bundle = new Bundle();
        context.startActivityForResult(intent, EDITAR_ITEM, bundle);
    }

    @Override
    protected void salvarItem(CorpoProva item) {
        item.setNome(this.nomeCorpoProvaEditText.getText().toString());
        this.service.getGerenciadorCorpoProva().salvarObjeto(item);
        this.setResult(RESULT_OK);
        this.finish();
    }
}
