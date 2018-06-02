package net.rodolfoboffo.indicadorrb.adapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;

import java.util.ArrayList;
import java.util.List;

public class ArrayDispositivosAdapter extends BaseAdapter {

    private List<BluetoothDevice> dispositivos;
    private Context context;

    public ArrayDispositivosAdapter(Context context, ArrayList<BluetoothDevice> list) {
        this.context = context;
        this.dispositivos = list;
    }

    @Override
    public Object getItem(int position) {
        return this.dispositivos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return this.dispositivos.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.list_item_dispositivos, parent, false);

        TextView textViewNome = (TextView) view.findViewById(R.id.itemDispositivoNomeText);
        String nomeDoDispositivo = this.dispositivos.get(position).getName() != null ?
                this.dispositivos.get(position).getName() :
                String.format("<%s>", this.context.getString(R.string.dispositivo_sem_nome));
        textViewNome.setText(nomeDoDispositivo);

        return view;
    }
}
