package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
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

    public abstract List<T> getListaItens();

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (this.service != null) {
            this.arrayListaItemAdapter.setLista(this.getListaItens());
            this.arrayListaItemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_lista;
    }

    public ArrayListaItemAdapter<T> getArrayListaItemAdapter() {
        return arrayListaItemAdapter;
    }

    public void onNovoItemButtonClick(View view) {}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
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

    public void selecionarItem(T item) {}

    public void editarItem(T item) {}

    public void removerItem(T item) {}

    @Override
    public boolean onContextItemSelected(MenuItem item) {
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
