package net.rodolfoboffo.indicadorrb.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.indicador.calibracao.PontoCalibracao;

import java.util.ArrayList;
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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.list_item_ponto_calibracao, parent, false);

        TextView textViewIndice = (TextView) view.findViewById(R.id.textViewIndice);
        EditText editTextCalibracao = (EditText) view.findViewById(R.id.editTextCalibracao);
        EditText editTextDigital = (EditText) view.findViewById(R.id.editTextDigital);

        textViewIndice.setText(String.valueOf(position+1));
//
//        String nomeDoDispositivo = null;
//        if (this.dispositivos != null) {
//            DispositivoBLE dispositivo = this.dispositivos.get(position);
//            nomeDoDispositivo = dispositivo.getNome().get();
//            if (dispositivo.getConectando().get()) {
//                progressBarConectando.setVisibility(View.VISIBLE);
//            }
//            else {
//                progressBarConectando.setVisibility(View.GONE);
//            }
//            if (dispositivo.getPronto().get()) {
//                imageViewIcon.setImageResource(R.drawable.ic_bluetooth_connected_24dp);
//                textViewConectado.setVisibility(View.VISIBLE);
//            }
//            else {
//                imageViewIcon.setImageResource(R.drawable.ic_bluetooth_24dp);
//                textViewConectado.setVisibility(View.GONE);
//            }
//        }
//        else {
//            imageViewIcon.setImageResource(R.drawable.ic_bluetooth_24dp);
//            textViewConectado.setVisibility(View.GONE);
//            progressBarConectando.setVisibility(View.GONE);
//        }
//        nomeDoDispositivo = nomeDoDispositivo != null ?
//                nomeDoDispositivo :
//                String.format("<%s>", this.context.getString(R.string.dispositivo_sem_nome));
//        textViewNome.setText(nomeDoDispositivo);
        return view;
    }
}
