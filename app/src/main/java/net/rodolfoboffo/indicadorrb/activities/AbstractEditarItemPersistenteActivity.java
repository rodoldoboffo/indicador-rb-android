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

    private I item;

    protected I getItem() {
        return this.item;
    }

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

        this.item = (I) this.getIntent().getSerializableExtra(ITEM_EXTRA);
        if (this.item == null) {
            try {
                this.item = (I) this.getItemClass().getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.getSupportActionBar().setTitle(this.getNovoItemResource());
        }
        else {
            this.item = (I) this.item.clone();
            this.getSupportActionBar().setTitle(this.getEditarItemResource());
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        this.inicializaObservadores();
    }

    public void inicializaObservadores() {};

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
