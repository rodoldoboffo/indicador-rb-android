package net.rodolfoboffo.indicadorrb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.basicos.AbstractEnumeration;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnumArrayAdapter<T extends AbstractEnumeration> extends BaseAdapter {

    private Context context;
    List<T> list;

    public EnumArrayAdapter(Context context, Class<T> enumClass) {
        this.context = context;
        this.list = AbstractEnumeration.getAll(enumClass);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public AbstractEnumeration getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.list_item_enum, parent, false);

        AbstractEnumeration enumObject = getItem(position);
        TextView textViewEnum = view.findViewById(R.id.textViewEnum);
        textViewEnum.setText(enumObject.getResourceString());

        return view;
    }
}
