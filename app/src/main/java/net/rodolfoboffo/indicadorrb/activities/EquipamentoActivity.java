package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ListView;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;

import java.util.List;

public class EquipamentoActivity extends AbstractListaItemActivity {

    @Override
    protected int getSemItensStringResource() {
        return R.string.naoExistemEquipamentos;
    }

    @Override
    public List getListaItens() {
        return null;
    }
}
