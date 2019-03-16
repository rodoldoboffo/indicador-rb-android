package net.rodolfoboffo.indicadorrb.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.EnumArrayAdapter;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.equipamento.Equipamento;

import java.text.NumberFormat;
import java.util.Arrays;

public class EditarEquipamentoActivity extends AbstractBaseActivity {

    public static final String EQUIPAMENTO_EXTRA = "EditarEquipamento.Equipamento";
    public static final int NOVO_EQUIPAMENTO = 0;
    public static final int EDITAR_EQUIPAMENTO = 1;

    private static final int MAX_SEEKBAR_VALUE = 10000;

    private Equipamento equipamento;

    private Spinner spinnerGrandeza;
    private EditText capacidadeEditText;
    private EnumArrayAdapter spinnerGrandezaAdapter;
    private Spinner spinnerUnidade;
    private EnumArrayAdapter spinnerUnidadeAdapter;
    private EditText nomeEquipamentoEditText;
    private CheckBox habilitarAvisoCheckbox;
    private RadioButton normalmenteLigadoRadioButton;
    private RadioButton normalmenteDesligadoRadioButton;
    private SeekBar limiarSeekBar;
    private TextView limiarSobrecargaTextView;

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

        this.nomeEquipamentoEditText = this.findViewById(R.id.nomeEquipamentoText);
        this.nomeEquipamentoEditText.setText(this.equipamento.getNome().get());

        this.capacidadeEditText = this.findViewById(R.id.capacidadeText);
        this.capacidadeEditText.setText(String.valueOf(this.equipamento.getCapacidade().get()));

        this.spinnerGrandezaAdapter = new EnumArrayAdapter(this, Arrays.asList(new GrandezaEnum[] {GrandezaEnum.forca, GrandezaEnum.temperatura}), R.layout.list_item_enum_small_red);
        this.spinnerGrandeza = this.findViewById(R.id.grandezaSpinner);
        this.spinnerGrandeza.setAdapter(spinnerGrandezaAdapter);
        this.spinnerGrandeza.setSelection(this.spinnerGrandezaAdapter.getListaEnums().indexOf(this.equipamento.getGrandeza().get()));

        this.spinnerUnidade = this.findViewById(R.id.unidadeEquipamentoSpinner);
        this.atualizaSpinnerUnidades(this.equipamento.getGrandeza().get());
        this.spinnerUnidade.setSelection(
                this.spinnerUnidadeAdapter.getListaEnums().indexOf(
                        this.equipamento.getUnidade().get()));

        this.habilitarAvisoCheckbox = this.findViewById(R.id.habilitarAvisoCheckbox);
        this.habilitarAvisoCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditarEquipamentoActivity.this.vibrarCurto();
            }
        });
        this.normalmenteLigadoRadioButton = this.findViewById(R.id.releLigadoRadioButton);
        this.normalmenteLigadoRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditarEquipamentoActivity.this.vibrarCurto();
            }
        });
        this.normalmenteDesligadoRadioButton = this.findViewById(R.id.releDesligadoRadioButton);
        this.normalmenteDesligadoRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditarEquipamentoActivity.this.vibrarCurto();
            }
        });
        this.limiarSeekBar = this.findViewById(R.id.limiarSobrecargaSeekbar);
        this.limiarSobrecargaTextView = this.findViewById(R.id.percentualSobrecargaText);

        this.habilitarAvisoCheckbox.setChecked(this.equipamento.getAvisoSobrecarga().get());
        this.normalmenteLigadoRadioButton.setChecked(this.equipamento.getReleNormalmenteLigado().get());
        this.normalmenteDesligadoRadioButton.setChecked(!this.equipamento.getReleNormalmenteLigado().get());
        this.limiarSeekBar.setMax(MAX_SEEKBAR_VALUE);
        int seekBarValue = new Double(this.equipamento.getLimiarSobrecarga().get() * MAX_SEEKBAR_VALUE).intValue();
        this.limiarSeekBar.setProgress(seekBarValue);
        this.atualizaIndicacaoPercentalSobrecarga(seekBarValue);
    }

    private void atualizaIndicacaoPercentalSobrecarga(int value) {
        Double percentualSobrecarga = (double)value / MAX_SEEKBAR_VALUE;
        this.equipamento.setLimiarSobrecarga(percentualSobrecarga);
        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        this.limiarSobrecargaTextView.setText(format.format(percentualSobrecarga*100.0) + "%");
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        this.inicializaObservadores();
    }

    public void inicializaObservadores() {
        this.iniciaObservadorSpinnerGrandeza();
        this.iniciaObservadorSeekBar();
    }

    private void iniciaObservadorSeekBar() {
        this.limiarSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                EditarEquipamentoActivity.this.atualizaIndicacaoPercentalSobrecarga(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                EditarEquipamentoActivity.this.vibrarCurto();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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

    public static void novoItem(Activity context) {
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
        if (this.nomeEquipamentoEditText.getText().toString().isEmpty()) {
            this.nomeEquipamentoEditText.setError(this.getString(R.string.nomeEquipamentoVazioError));
            return false;
        }
        if (!this.service.getGerenciadorEquipamento().validarNomeObjeto(this.nomeEquipamentoEditText.getText().toString(), this.equipamento)) {
            this.nomeEquipamentoEditText.setError(this.getString(R.string.equipamentoComMesmoNome));
            return false;
        }

        Double capacidade = Double.NaN;
        try {
            capacidade = Double.valueOf(this.capacidadeEditText.getText().toString());
        }
        catch (Exception e) {

        }
        if (capacidade.isNaN()) {
            this.capacidadeEditText.setError(this.getString(R.string.capacidadeNaoEhNumero));
            return false;
        }
        else if (capacidade <= 0.0) {
            this.capacidadeEditText.setError(this.getString(R.string.capacidadeNaoPositivo));
            return false;
        }
        return true;
    }

    public void onSalvarEquipamentoButtonClick(View view) {
        this.vibrarCurto();
        if (this.validar()) {
            if (this.service != null) {
                this.equipamento.setNome(this.nomeEquipamentoEditText.getText().toString());
                this.equipamento.setGrandeza((GrandezaEnum) this.spinnerGrandeza.getSelectedItem());
                this.equipamento.setUnidade((UnidadeEnum) this.spinnerUnidade.getSelectedItem());
                this.equipamento.setCapacidade(Double.valueOf(this.capacidadeEditText.getText().toString()));
                this.equipamento.setAvisoSobrecarga(this.habilitarAvisoCheckbox.isChecked());
                this.equipamento.setReleNormalmenteLigado(this.normalmenteLigadoRadioButton.isChecked());
                this.service.getGerenciadorEquipamento().salvarObjeto(this.equipamento);
                this.setResult(RESULT_OK);
                this.finish();
            }
        }
    }
}
