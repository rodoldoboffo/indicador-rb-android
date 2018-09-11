package net.rodolfoboffo.indicadorrb.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;

import java.util.List;

public class ArrayListaItemAdapter<T extends IListaItem> extends BaseAdapter {

    private List<T> lista;
    private Context context;

    public ArrayListaItemAdapter(Context context, List<T> list) {
        this.context = context;
        this.lista = list;
    }

    public ArrayListaItemAdapter(Context context) {
        this(context, null);
    }

    public void setLista(List<T> list) {
        this.lista = list;
    }

    @Override
    public Object getItem(int position) {
        if (this.lista != null) {
            return this.lista.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        if (this.lista != null) {
            return this.lista.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.list_item_array_adapter, parent, false);

        TextView textViewNome = (TextView) view.findViewById(R.id.itemNomeExibicaoItemText);
        ImageView imageViewSelecionado = (ImageView) view.findViewById(R.id.imageItemSelecionado);
        if (this.lista != null) {
            T item = this.lista.get(position);
            textViewNome.setText(item.getNomeExibicaoLista());
            if (item.isListsaItemSelecionado()) {
                imageViewSelecionado.setVisibility(View.VISIBLE);
            }
            else {
                imageViewSelecionado.setVisibility(View.GONE);
            }
        }
        else {
            textViewNome.setText("");
            imageViewSelecionado.setVisibility(View.GONE);
        }
        return view;
    }
}
