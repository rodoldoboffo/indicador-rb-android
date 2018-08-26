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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.ArrayPontosCalibracaoAdapter;
import net.rodolfoboffo.indicadorrb.adapter.EnumArrayAdapter;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.Calibracao;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.PontoCalibracao;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.Reta;
import net.rodolfoboffo.indicadorrb.model.math.RegressaoLinear;

import java.util.Arrays;

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
    private ListView listViewPontosCalibracao;
    private TextView textViewSemPontosCalibracao;
    private ArrayPontosCalibracaoAdapter pontosAdapter;

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
            this.getSupportActionBar().setTitle(R.string.nova_calibracao_activity_label);
        }
        else {
            this.calibracao = this.calibracao.clone();
            this.getSupportActionBar().setTitle(R.string.editar_calibracao_activity_label);
        }

        this.nomeCalibracaoText = this.findViewById(R.id.nomeCalibracaoText);
        this.nomeCalibracaoText.setText(this.calibracao.getNome().get());

        this.spinnerGrandezaAdapter = new EnumArrayAdapter(this, Arrays.asList(new GrandezaEnum[] {GrandezaEnum.forca, GrandezaEnum.temperatura}));
        this.spinnerGrandeza = this.findViewById(R.id.grandezaSpinner);
        this.spinnerGrandeza.setAdapter(spinnerGrandezaAdapter);
        this.spinnerGrandeza.setSelection(this.spinnerGrandezaAdapter.getListaEnums().indexOf(this.calibracao.getGrandeza().get()));

        this.spinnerUnidade = this.findViewById(R.id.unidadeCalibracaoSpinner);
        this.atualizaSpinnerUnidades(this.calibracao.getGrandeza().get());
        this.spinnerUnidade.setSelection(
                this.spinnerUnidadeAdapter.getListaEnums().indexOf(
                        this.calibracao.getUnidadeCalibracao().get()));

        this.pontosAdapter = new ArrayPontosCalibracaoAdapter(this, this.calibracao.getPontosCalibracao());
        this.textViewSemPontosCalibracao = this.findViewById(R.id.textViewSemPontosCalibracao);
        this.listViewPontosCalibracao = this.findViewById(R.id.listViewPontosCalibracao);
        this.listViewPontosCalibracao.setEmptyView(this.textViewSemPontosCalibracao);
        this.listViewPontosCalibracao.setAdapter(this.pontosAdapter);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        this.inicializaObservadores();
    }

    public void inicializaObservadores() {
        this.iniciaObservadorSpinnerGrandeza();
        this.iniciaObservadorListaPontos();
    }

    private void iniciaObservadorListaPontos() {
        this.calibracao.getPontosCalibracao().addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<PontoCalibracao>>() {
            @Override
            public void onChanged(ObservableList<PontoCalibracao> sender) {
                EditarCalibracaoActivity.this.pontosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<PontoCalibracao> sender, int positionStart, int itemCount) {
                EditarCalibracaoActivity.this.pontosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeInserted(ObservableList<PontoCalibracao> sender, int positionStart, int itemCount) {
                EditarCalibracaoActivity.this.pontosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeMoved(ObservableList<PontoCalibracao> sender, int fromPosition, int toPosition, int itemCount) {
                EditarCalibracaoActivity.this.pontosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<PontoCalibracao> sender, int positionStart, int itemCount) {
                EditarCalibracaoActivity.this.pontosAdapter.notifyDataSetChanged();
            }
        });
    }

    private void iniciaObservadorSpinnerGrandeza() {
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
        for (int i = 0; i < this.pontosAdapter.getCount(); i++) {
            View v = this.listViewPontosCalibracao.getChildAt(i);
            EditText editTextDigital = (EditText) v.findViewById(R.id.editTextDigital);
            EditText editTextCalibracao = (EditText) v.findViewById(R.id.editTextCalibracao);
            if (editTextDigital.getText().toString().isEmpty()) {
                editTextDigital.setError(this.getString(R.string.valorDigitalEmbranco));
                return false;
            }
            if (editTextCalibracao.getText().toString().isEmpty()) {
                editTextCalibracao.setError(this.getString(R.string.valorCalibracaoEmbranco));
                return false;
            }
        }
        return true;
    }

    public void onSalvarCalibracaoButtonClick(View view) {
        if (this.validar()) {
            if (this.service != null) {
                this.calibracao.setNome(this.nomeCalibracaoText.getText().toString());
                this.calibracao.setGrandeza((GrandezaEnum) this.spinnerGrandeza.getSelectedItem());
                this.calibracao.setUnidadeCalibracao((UnidadeEnum) this.spinnerUnidade.getSelectedItem());
                Reta ajuste = RegressaoLinear.getAjuste(this.calibracao.getPontosCalibracao());
                this.calibracao.setAjuste(ajuste);
                this.service.getGerenciadorCalibracao().salvarCalibracao(this.calibracao);
                this.setResult(RESULT_OK);
                this.finish();
            }
        }
    }

    public void onAdicionarPontoButtonClick(View view) {
        if (this.service != null && this.service.getCondicionadorSinais().get() != null &&
                this.service.getCondicionadorSinais().get().getConexao().getPronto().get() &&
                this.service.getCondicionadorSinais().get().getAquisicaoAutomatica().get() &&
                !Double.isNaN(this.service.getCondicionadorSinais().get().getUltimoValorLido().get())) {
            this.calibracao.adicionaPontoCalibracao(new PontoCalibracao(this.service.getCondicionadorSinais().get().getUltimoValorLido().get(), Double.NaN));
        }
        else {
            this.calibracao.adicionaPontoCalibracao(new PontoCalibracao());
        }
    }
}
