package net.rodolfoboffo.indicadorrb.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.EnumArrayAdapter;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.corpoprova.CorpoProva;
import net.rodolfoboffo.indicadorrb.model.equipamento.Equipamento;
import net.rodolfoboffo.indicadorrb.model.persistencia.IObjetoPersistente;

import java.text.NumberFormat;
import java.util.Arrays;

public class EditarCorpoProvaActivity extends AbstractEditarItemPersistenteActivity<CorpoProva> {

    private EditText nomeCorpoProvaEditText;

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

    @Override
    protected void salvarItem(CorpoProva item) {
        item.setNome(this.nomeCorpoProvaEditText.getText().toString());
        this.service.getGerenciadorCorpoProva().salvarObjeto(item);
        this.setResult(RESULT_OK);
        this.finish();
    }
}
