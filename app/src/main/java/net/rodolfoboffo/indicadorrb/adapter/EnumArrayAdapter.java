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

    private int listItemLayoutResource;
    private Context context;
    List<T> list;

    public EnumArrayAdapter(Context context, Class<T> enumClass, int layoutResource) {
        this(context, AbstractEnumeration.getAll(enumClass), layoutResource);
    }

    public EnumArrayAdapter(Context context, List<T> listEnums, int layoutResource) {
        this.listItemLayoutResource = layoutResource;
        this.context = context;
        this.list = listEnums;
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
        View view = inflater.inflate(this.listItemLayoutResource, parent, false);

        AbstractEnumeration enumObject = getItem(position);
        TextView textViewEnum = view.findViewById(R.id.textViewEnum);
        textViewEnum.setText(enumObject.getResourceString());

        return view;
    }

    public List<T> getListaEnums() {
        return this.list;
    }
}
