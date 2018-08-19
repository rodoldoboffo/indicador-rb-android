package net.rodolfoboffo.indicadorrb.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.Observable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.EnumArrayAdapter;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.indicador.calibracao.Calibracao;

public class EditarCalibracaoActivity extends AbstractBaseActivity {

    public static final String CALIBRACAO_EXTRA = "EditarCalibracao.Calibracao";
    public static final int NOVA_CALIBRACAO = 0;
    public static final int EDITAR_CALIBRACAO = 1;

    private Calibracao calibracao;

    private Spinner spinnerGrandeza;
    private EnumArrayAdapter spinnerGrandezaAdapter;
    private Spinner spinnerUnidade;
    private EnumArrayAdapter spinnerUnidadeAdapter;
    private EditText nomeCalibracaoText;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_editar_calibracao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.calibracao = (Calibracao) this.getIntent().getSerializableExtra(CALIBRACAO_EXTRA);
        if (this.calibracao == null) {
            this.calibracao = new Calibracao();
        }

        this.nomeCalibracaoText = this.findViewById(R.id.nomeCalibracaoText);
        this.nomeCalibracaoText.setText(this.calibracao.getNome().get());

        this.spinnerGrandezaAdapter = new EnumArrayAdapter(this, GrandezaEnum.class);
        this.spinnerGrandeza = this.findViewById(R.id.grandezaSpinner);
        this.spinnerGrandeza.setAdapter(spinnerGrandezaAdapter);
        this.spinnerGrandeza.setSelection(this.spinnerGrandezaAdapter.getListaEnums().indexOf(this.calibracao.getGrandeza().get()));

        this.spinnerUnidade = this.findViewById(R.id.unidadeCalibracaoSpinner);
        this.atualizaSpinnerUnidades(this.calibracao.getGrandeza().get());
        this.spinnerUnidade.setSelection(this.spinnerUnidadeAdapter.getListaEnums().indexOf(this.calibracao.getUnidadeCalibracao().get()));

        this.inicializaObservadores();
    }

    public void inicializaObservadores() {
        this.spinnerGrandeza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GrandezaEnum grandeza = (GrandezaEnum) parent.getItemAtPosition(position);
                EditarCalibracaoActivity.this.atualizaSpinnerUnidades(grandeza);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void atualizaSpinnerUnidades(GrandezaEnum grandeza) {
        this.spinnerUnidadeAdapter = new EnumArrayAdapter(this, UnidadeEnum.getAllByGrandeza(grandeza));
        this.spinnerUnidade.setAdapter(this.spinnerUnidadeAdapter);
    }

    public static void novaCalibracao(Activity context) {
        Intent intent = new Intent(context, EditarCalibracaoActivity.class);
        Bundle bundle = new Bundle();
        context.startActivityForResult(intent, NOVA_CALIBRACAO, bundle);
    }

    public static void editarCalibracao(Activity context, Calibracao calibracao) {
        Intent intent = new Intent(context, EditarCalibracaoActivity.class);
        intent.putExtra(CALIBRACAO_EXTRA, calibracao);
        Bundle bundle = new Bundle();
        context.startActivityForResult(intent, EDITAR_CALIBRACAO, bundle);
    }

    public Boolean validar() {
        if (this.nomeCalibracaoText.getText().toString().isEmpty()) {
            this.nomeCalibracaoText.setError(this.getString(R.string.nomeCalibracaoVazioError));
            return false;
        }
        if (!this.service.getGerenciadorCalibracao().validarNomeCalibracao(this.nomeCalibracaoText.getText().toString(), this.calibracao)) {
            this.nomeCalibracaoText.setError(this.getString(R.string.calibracaoComMesmoNome));
            return false;
        }
        return true;
    }

    public void onSalvarCalibracaoButtonClick(View view) {
        if (this.validar()) {
            if (this.service != null) {
                this.calibracao.setNome(this.nomeCalibracaoText.getText().toString());
                this.calibracao.setGrandeza((GrandezaEnum) this.spinnerGrandeza.getSelectedItem());
                this.calibracao.setUnidadeCalibracao((UnidadeEnum) this.spinnerUnidade.getSelectedItem());
                this.service.getGerenciadorCalibracao().salvarCalibracao(this.calibracao);
                this.setResult(RESULT_OK);
                this.finish();
            }
        }
    }
}
