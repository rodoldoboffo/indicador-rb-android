package net.rodolfoboffo.indicadorrb.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.activities.AbstractBaseActivity;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.PontoCalibracao;

import java.util.List;

public class ArrayPontosCalibracaoAdapter extends BaseAdapter {

    private List<PontoCalibracao> pontos;
    private Context context;

    public ArrayPontosCalibracaoAdapter(Context context, List<PontoCalibracao> list) {
        this.context = context;
        this.pontos = list;
    }

    public void setListaPontos(List<PontoCalibracao> pontos) {
        this.pontos = pontos;
    }

    @Override
    public Object getItem(int position) {
        if (this.pontos != null) {
            return this.pontos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        if (this.pontos != null) {
            return this.pontos.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.list_item_ponto_calibracao, parent, false);

        TextView textViewIndice = (TextView) view.findViewById(R.id.textViewIndice);
        EditText editTextCalibracao = (EditText) view.findViewById(R.id.editTextCalibracao);
        EditText editTextDigital = (EditText) view.findViewById(R.id.editTextDigital);
        ImageButton buttonRemove = (ImageButton)view.findViewById(R.id.removePontoButton);

        PontoCalibracao ponto = this.pontos.get(position);
        if (!Double.isNaN(ponto.getValorNaoCalibrado().get())) {
            editTextDigital.setText(String.valueOf(new Double(ponto.getValorNaoCalibrado().get()).longValue()));
        }

        if (!Double.isNaN(ponto.getValorCalibrado().get())) {
            editTextCalibracao.setText(String.valueOf(ponto.getValorCalibrado().get()));
        }

        editTextDigital.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    ArrayPontosCalibracaoAdapter.this.pontos.get(position).setValorNaoCalibrado(Double.NaN);
                }
                else {
                    ArrayPontosCalibracaoAdapter.this.pontos.get(position).setValorNaoCalibrado(Double.valueOf(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextCalibracao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    ArrayPontosCalibracaoAdapter.this.pontos.get(position).setValorCalibrado(Double.NaN);
                }
                else {
                    ArrayPontosCalibracaoAdapter.this.pontos.get(position).setValorCalibrado(Double.valueOf(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbstractBaseActivity a = (AbstractBaseActivity)ArrayPontosCalibracaoAdapter.this.context;
                a.vibrarCurto();
                ArrayPontosCalibracaoAdapter.this.pontos.remove(position);
                ArrayPontosCalibracaoAdapter.this.notifyDataSetChanged();
            }
        });

        textViewIndice.setText(String.valueOf(position+1));
        return view;
    }

}
