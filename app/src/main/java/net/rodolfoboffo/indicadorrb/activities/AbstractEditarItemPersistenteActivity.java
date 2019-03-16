package net.rodolfoboffo.indicadorrb.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.corpoprova.CorpoProva;
import net.rodolfoboffo.indicadorrb.model.persistencia.IObjetoPersistente;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractEditarItemPersistenteActivity<I extends IObjetoPersistente> extends AbstractBaseActivity {

    public static final String ITEM_EXTRA = "EditarItem.Item";
    public static final int NOVO_ITEM = 0;
    public static final int EDITAR_ITEM = 1;

    protected abstract I getItem();

    protected abstract void setItem(I item);

    protected int getNovoItemResource() {
        return R.string.novo_item_activity_label;
    }

    protected int getEditarItemResource() {
        return R.string.editar_item_activity_label;
    }

    protected abstract Class getItemClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setItem((I) this.getIntent().getSerializableExtra(ITEM_EXTRA));
        if (this.getItem() == null) {
            try {
                this.setItem((I) this.getItemClass().getConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.getSupportActionBar().setTitle(this.getNovoItemResource());
        }
        else {
            this.setItem((I) this.getItem().clone());
            this.getSupportActionBar().setTitle(this.getEditarItemResource());
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        this.inicializaObservadores();
    }

    public void inicializaObservadores() {};

    public abstract Boolean validar();

    public void onSalvarItemButtonClick(View view) {
        this.vibrarCurto();
        if (this.validar()) {
            if (this.service != null) {
                this.salvarItem(this.getItem());
            }
        }
    }

    protected abstract void salvarItem(I item);
}
