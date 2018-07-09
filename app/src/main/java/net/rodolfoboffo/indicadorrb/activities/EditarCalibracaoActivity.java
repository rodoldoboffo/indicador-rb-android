package net.rodolfoboffo.indicadorrb.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.basicos.Grandeza;

public class EditarCalibracaoActivity extends AbstractBaseActivity {

    public static final int NOVA_CALIBRACAO = 0;

    private Spinner spinnerGrandeza;
    private ArrayAdapter<CharSequence> spinnerGrandezaAdapter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_editar_calibracao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.spinnerGrandezaAdapter = ArrayAdapter.createFromResource(this, R.array.grandezasArray, android.R.layout.simple_spinner_dropdown_item);
        this.spinnerGrandeza = this.findViewById(R.id.grandezaSpinner);
        this.spinnerGrandeza.setAdapter(spinnerGrandezaAdapter);
    }

    public static void novaCalibracao(Activity context) {
        Intent intent = new Intent(context, EditarCalibracaoActivity.class);
        Bundle bundle = new Bundle();
        context.startActivityForResult(intent, NOVA_CALIBRACAO, bundle);
    }
}
