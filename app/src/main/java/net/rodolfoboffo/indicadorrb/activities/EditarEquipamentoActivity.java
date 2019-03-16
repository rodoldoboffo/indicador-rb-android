package net.rodolfoboffo.indicadorrb.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class EditarEquipamentoActivity extends AbstractEditarItemPersistenteActivity<Equipamento> {

    private static final int MAX_SEEKBAR_VALUE = 10000;

    private Equipamento item;

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
    public Equipamento getItem() {
        return item;
    }

    @Override
    public void setItem(Equipamento item) {
        this.item = item;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_editar_equipamento;
    }

    @Override
    protected int getNovoItemResource() {
        return R.string.nova_calibracao_activity_label;
    }

    @Override
    protected int getEditarItemResource() {
        return R.string.editar_equipamento_activity_label;
    }

    @Override
    protected Class getItemClass() {
        return Equipamento.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.nomeEquipamentoEditText = this.findViewById(R.id.nomeEquipamentoText);
        this.nomeEquipamentoEditText.setText(this.item.getNome().get());

        this.capacidadeEditText = this.findViewById(R.id.capacidadeText);
        this.capacidadeEditText.setText(String.valueOf(this.item.getCapacidade().get()));

        this.spinnerGrandezaAdapter = new EnumArrayAdapter(this, Arrays.asList(new GrandezaEnum[] {GrandezaEnum.forca, GrandezaEnum.temperatura}), R.layout.list_item_enum_small_red);
        this.spinnerGrandeza = this.findViewById(R.id.grandezaSpinner);
        this.spinnerGrandeza.setAdapter(spinnerGrandezaAdapter);
        this.spinnerGrandeza.setSelection(this.spinnerGrandezaAdapter.getListaEnums().indexOf(this.item.getGrandeza().get()));

        this.spinnerUnidade = this.findViewById(R.id.unidadeEquipamentoSpinner);
        this.atualizaSpinnerUnidades(this.item.getGrandeza().get());
        this.spinnerUnidade.setSelection(
                this.spinnerUnidadeAdapter.getListaEnums().indexOf(
                        this.item.getUnidade().get()));

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

        this.habilitarAvisoCheckbox.setChecked(this.item.getAvisoSobrecarga().get());
        this.normalmenteLigadoRadioButton.setChecked(this.item.getReleNormalmenteLigado().get());
        this.normalmenteDesligadoRadioButton.setChecked(!this.item.getReleNormalmenteLigado().get());
        this.limiarSeekBar.setMax(MAX_SEEKBAR_VALUE);
        int seekBarValue = new Double(this.item.getLimiarSobrecarga().get() * MAX_SEEKBAR_VALUE).intValue();
        this.limiarSeekBar.setProgress(seekBarValue);
        this.atualizaIndicacaoPercentalSobrecarga(seekBarValue);
    }

    private void atualizaIndicacaoPercentalSobrecarga(int value) {
        Double percentualSobrecarga = (double)value / MAX_SEEKBAR_VALUE;
        this.item.setLimiarSobrecarga(percentualSobrecarga);
        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        this.limiarSobrecargaTextView.setText(format.format(percentualSobrecarga*100.0) + "%");
    }

    @Override
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

    public Boolean validar() {
        if (this.nomeEquipamentoEditText.getText().toString().isEmpty()) {
            this.nomeEquipamentoEditText.setError(this.getString(R.string.nomeEquipamentoVazioError));
            return false;
        }
        if (!this.service.getGerenciadorEquipamento().validarNomeObjeto(this.nomeEquipamentoEditText.getText().toString(), this.item)) {
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

    public static void novoItem(Activity context) {
        Intent intent = new Intent(context, EditarEquipamentoActivity.class);
        Bundle bundle = new Bundle();
        context.startActivityForResult(intent, NOVO_ITEM, bundle);
    }

    public static void editarItem(Activity context, Equipamento item) {
        Intent intent = new Intent(context, EditarEquipamentoActivity.class);
        intent.putExtra(ITEM_EXTRA, item);
        Bundle bundle = new Bundle();
        context.startActivityForResult(intent, EDITAR_ITEM, bundle);
    }

    @Override
    protected void salvarItem(Equipamento item) {
        this.item.setNome(this.nomeEquipamentoEditText.getText().toString());
        this.item.setGrandeza((GrandezaEnum) this.spinnerGrandeza.getSelectedItem());
        this.item.setUnidade((UnidadeEnum) this.spinnerUnidade.getSelectedItem());
        this.item.setCapacidade(Double.valueOf(this.capacidadeEditText.getText().toString()));
        this.item.setAvisoSobrecarga(this.habilitarAvisoCheckbox.isChecked());
        this.item.setReleNormalmenteLigado(this.normalmenteLigadoRadioButton.isChecked());
        this.service.getGerenciadorEquipamento().salvarObjeto(this.item);
        this.setResult(RESULT_OK);
        this.finish();
    }
}
