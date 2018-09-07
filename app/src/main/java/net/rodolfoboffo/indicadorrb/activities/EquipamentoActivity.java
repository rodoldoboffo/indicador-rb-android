package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ListView;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;

public class EquipamentoActivity extends AbstractBaseActivity {

//    private ArrayCalibracoesAdapter arrayCalibracoesAdapter;
    private ListView listaEquipamentos;
    private TextView textViewSemEquipamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.listaEquipamentos = this.findViewById(R.id.listViewEquipamentos);
        this.textViewSemEquipamento = this.findViewById(R.id.textViewSemEquipamento);
        this.listaEquipamentos.setEmptyView(this.textViewSemEquipamento);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (this.service != null) {

        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_equipamento;
    }

}
