package net.rodolfoboffo.indicadorrb.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.model.indicador.calibracao.Calibracao;

import java.util.ArrayList;
import java.util.List;

public class ArrayCalibracoesAdapter extends BaseAdapter {

    private List<Calibracao> calibracoes;
    private Context context;

    public ArrayCalibracoesAdapter(Context context, List<Calibracao> list) {
        this.context = context;
        this.calibracoes = list;
    }

    public ArrayCalibracoesAdapter(Context context) {
        this(context, null);
    }

    public void setCalibracoes(List<Calibracao> calibracoes) {
        this.calibracoes = calibracoes;
    }

    @Override
    public Object getItem(int position) {
        if (this.calibracoes != null) {
            return this.calibracoes.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        if (this.calibracoes != null) {
            return this.calibracoes.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.list_item_calibracao, parent, false);

        TextView textViewNome = (TextView) view.findViewById(R.id.itemNomeCalibracaoText);
        ImageView imageViewSelecionado = (ImageView) view.findViewById(R.id.imageCalibracaoSelecionado);
        if (this.calibracoes != null) {
            Calibracao calibracao = this.calibracoes.get(position);
            textViewNome.setText(calibracao.getNome().get());
            if (calibracao.getSelecionado().get()) {
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
