package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
import android.databinding.Observable;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.ArrayListaItemAdapter;
import net.rodolfoboffo.indicadorrb.adapter.IListaItem;

import java.util.List;

public abstract class AbstractListaItemActivity<T extends IListaItem> extends AbstractBaseActivity implements View.OnCreateContextMenuListener, AdapterView.OnItemClickListener {

    private ArrayListaItemAdapter<T> arrayListaItemAdapter;
    private ListView listaItem;
    private TextView textViewSemItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.listaItem = this.findViewById(R.id.listViewItens);
        this.textViewSemItem = this.findViewById(R.id.textViewSemItens);
        this.textViewSemItem.setText(this.getSemItensStringResource());
        this.listaItem.setEmptyView(this.textViewSemItem);
        this.arrayListaItemAdapter = new ArrayListaItemAdapter<T>(this);
        this.listaItem.setAdapter(this.arrayListaItemAdapter);
        this.listaItem.setOnCreateContextMenuListener(this);
        this.listaItem.setOnItemClickListener(this);
    }

    protected int getSemItensStringResource() {
        return R.string.naoExistemItens;
    }

    public abstract ObservableList<T> getListaItens();

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (this.service != null) {
            this.arrayListaItemAdapter.setLista(this.getListaItens());
            this.arrayListaItemAdapter.notifyDataSetChanged();
            this.inicializaObservadoresDoServico();
        }
    }

    public void inicializaObservadoresDoServico() {
        this.inicializaObservadorListaObjetos();
        this.inicializaObservadorObjeto();
    }

    public void inicializaObservadorObjeto() {
        if (this.service != null) {
            for (T o : this.getListaItens()) {
                this.inicializaObservadorObjeto(o);
            }
        }
    }

    public void inicializaObservadorObjeto(T o) {
        o.getSelecionado().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                AbstractListaItemActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
            }
        });
    }

    public void inicializaObservadorListaObjetos() {
        if (this.service != null) {
            this.getListaItens().addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<T>>() {
                @Override
                public void onChanged(ObservableList<T> sender) {
                    AbstractListaItemActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
                    AbstractListaItemActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                }

                @Override
                public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
                    AbstractListaItemActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                    if (AbstractListaItemActivity.this.service != null) {
                        T o = AbstractListaItemActivity.this.getListaItens().get(positionStart);
                        AbstractListaItemActivity.this.inicializaObservadorObjeto(o);
                    }
                }

                @Override
                public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
                    AbstractListaItemActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                }

                @Override
                public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
                    AbstractListaItemActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_lista;
    }

    public ArrayListaItemAdapter<T> getArrayListaItemAdapter() {
        return arrayListaItemAdapter;
    }

    public void onNovoItemButtonClick(View view) {
        this.vibrarCurto();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.vibrarCurto();
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_lista_itens, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<T> lista = this.getListaItens();
        if (lista != null) {
            this.selecionarItem(lista.get(position));
        }
    }

    public void selecionarItem(T item) {
        this.vibrarCurto();
    }

    public void editarItem(T item) {
        this.vibrarCurto();
    }

    public void removerItem(T item) {
        this.vibrarCurto();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        this.vibrarCurto();
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = contextMenuInfo.position;
        if (this.service != null) {
            T itemSelecionado = this.getListaItens().get(position);
            switch (item.getItemId()) {
                case R.id.editarItem:
                    this.editarItem(itemSelecionado);
                    break;
                case R.id.selecionarItem:
                    this.selecionarItem(itemSelecionado);
                    break;
                case R.id.removerItem:
                    this.removerItem(itemSelecionado);
                    break;
            }
        }
        return true;
    }

}
