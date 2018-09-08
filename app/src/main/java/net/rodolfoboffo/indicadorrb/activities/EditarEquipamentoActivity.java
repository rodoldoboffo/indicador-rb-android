package net.rodolfoboffo.indicadorrb.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.ArrayPontosCalibracaoAdapter;
import net.rodolfoboffo.indicadorrb.adapter.EnumArrayAdapter;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.Calibracao;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.PontoCalibracao;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.Reta;
import net.rodolfoboffo.indicadorrb.model.equipamento.Equipamento;
import net.rodolfoboffo.indicadorrb.model.math.RegressaoLinear;

import java.util.Arrays;

public class EditarEquipamentoActivity extends AbstractBaseActivity {

    public static final String EQUIPAMENTO_EXTRA = "EditarEquipamento.Equipamento";
    public static final int NOVO_EQUIPAMENTO = 0;
    public static final int EDITAR_EQUIPAMENTO = 1;

    private Equipamento equipamento;

    private Spinner spinnerGrandeza;
    private EditText capacidadeText;
    private EnumArrayAdapter spinnerGrandezaAdapter;
    private Spinner spinnerUnidade;
    private EnumArrayAdapter spinnerUnidadeAdapter;
    private EditText nomeEquipamentoText;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_editar_equipamento;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.equipamento = (Equipamento) this.getIntent().getSerializableExtra(EQUIPAMENTO_EXTRA);
        if (this.equipamento == null) {
            this.equipamento = new Equipamento();
            this.getSupportActionBar().setTitle(R.string.nova_calibracao_activity_label);
        }
        else {
            this.equipamento = this.equipamento.clone();
            this.getSupportActionBar().setTitle(R.string.editar_equipamento_activity_label);
        }

        this.nomeEquipamentoText = this.findViewById(R.id.nomeEquipamentoText);
        this.nomeEquipamentoText.setText(this.equipamento.getNome().get());

        this.capacidadeText = this.findViewById(R.id.capacidadeText);
        this.capacidadeText.setText(String.valueOf(this.equipamento.getCapacidade().get()));

        this.spinnerGrandezaAdapter = new EnumArrayAdapter(this, Arrays.asList(new GrandezaEnum[] {GrandezaEnum.forca, GrandezaEnum.temperatura}), R.layout.list_item_enum_small_red);
        this.spinnerGrandeza = this.findViewById(R.id.grandezaSpinner);
        this.spinnerGrandeza.setAdapter(spinnerGrandezaAdapter);
        this.spinnerGrandeza.setSelection(this.spinnerGrandezaAdapter.getListaEnums().indexOf(this.equipamento.getGrandeza().get()));

        this.spinnerUnidade = this.findViewById(R.id.unidadeEquipamentoSpinner);
        this.atualizaSpinnerUnidades(this.equipamento.getGrandeza().get());
        this.spinnerUnidade.setSelection(
                this.spinnerUnidadeAdapter.getListaEnums().indexOf(
                        this.equipamento.getUnidade().get()));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        this.inicializaObservadores();
    }

    public void inicializaObservadores() {
        this.iniciaObservadorSpinnerGrandeza();
    }

    private void iniciaObservadorSpinnerGrandeza() {
        this.spinnerGrandeza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GrandezaEnum grandeza = (GrandezaEnum) parent.getItemAtPosition(position);
                EditarEquipamentoActivity.this.atualizaSpinnerUnidades(grandeza);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void atualizaSpinnerUnidades(GrandezaEnum grandeza) {
        this.spinnerUnidadeAdapter = new EnumArrayAdapter(this, UnidadeEnum.getAllByGrandeza(grandeza), R.layout.list_item_enum_small_red);
        this.spinnerUnidade.setAdapter(this.spinnerUnidadeAdapter);
    }

    public static void novoEquipamento(Activity context) {
        Intent intent = new Intent(context, EditarEquipamentoActivity.class);
        Bundle bundle = new Bundle();
        context.startActivityForResult(intent, NOVO_EQUIPAMENTO, bundle);
    }

    public static void editarEquipamento(Activity context, Equipamento equipamento) {
        Intent intent = new Intent(context, EditarEquipamentoActivity.class);
        intent.putExtra(EQUIPAMENTO_EXTRA, equipamento);
        Bundle bundle = new Bundle();
        context.startActivityForResult(intent, EDITAR_EQUIPAMENTO, bundle);
    }

    public Boolean validar() {
        if (this.nomeEquipamentoText.getText().toString().isEmpty()) {
            this.nomeEquipamentoText.setError(this.getString(R.string.nomeEquipamentoVazioError));
            return false;
        }
        if (!this.service.getGerenciadorEquipamento().validarNomeObjeto(this.nomeEquipamentoText.getText().toString(), this.equipamento)) {
            this.nomeEquipamentoText.setError(this.getString(R.string.equipamentoComMesmoNome));
            return false;
        }

        Double capacidade = Double.NaN;
        try {
            capacidade = Double.valueOf(this.capacidadeText.getText().toString());
        }
        catch (Exception e) {

        }
        if (capacidade.isNaN()) {
            this.capacidadeText.setError(this.getString(R.string.capacidadeNaoEhNumero));
            return false;
        }
        else if (capacidade <= 0.0) {
            this.capacidadeText.setError(this.getString(R.string.capacidadeNaoPositivo));
            return false;
        }
        return true;
    }

    public void onSalvarEquipamentoButtonClick(View view) {
        if (this.validar()) {
            if (this.service != null) {
                this.equipamento.setNome(this.nomeEquipamentoText.getText().toString());
                this.equipamento.setGrandeza((GrandezaEnum) this.spinnerGrandeza.getSelectedItem());
                this.equipamento.setUnidade((UnidadeEnum) this.spinnerUnidade.getSelectedItem());
                this.equipamento.setCapacidade(Double.valueOf(this.capacidadeText.getText().toString()));
                this.service.getGerenciadorEquipamento().salvarObjeto(this.equipamento);
                this.setResult(RESULT_OK);
                this.finish();
            }
        }
    }
}
