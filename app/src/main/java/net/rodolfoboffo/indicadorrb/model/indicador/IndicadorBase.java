package net.rodolfoboffo.indicadorrb.model.indicador;

import android.databinding.Observable;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.Calibracao;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

import java.text.NumberFormat;

public class IndicadorBase extends AbstractServiceRelatedObject {

    private ObservableField<GrandezaEnum> grandezaExibicao;
    private ObservableField<UnidadeEnum> unidadeExibicao;
    private ObservableDouble tara;

    public IndicadorBase(IndicadorService service) {
        super(service);
        this.grandezaExibicao = new ObservableField<>();
        this.unidadeExibicao = new ObservableField<>();
        this.tara = new ObservableDouble(0.0);
        this.inicializaObservadores();
    }

    private void reset() {
        this.grandezaExibicao.set(null);
        this.unidadeExibicao.set(null);
        this.tara.set(0.0);
    }

    private void inicializaObservadores() {
        this.inicializaObservadorCalibracaoSelecionada();
    }

    private void inicializaObservadorCalibracaoSelecionada() {
        this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                IndicadorBase.this.usaCalibracaoSelecionada();
            }
        });
        IndicadorBase.this.usaCalibracaoSelecionada();
    }

    public void usaCalibracaoSelecionada() {
        this.usaCalibracao(this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get());
    }

    public void usaCalibracao(Calibracao calibracao) {
        if (calibracao == null) {
            this.reset();
        }
        else {
            this.setGrandezaExibicao(calibracao.getGrandeza().get());
            this.setUnidadeExibicao(calibracao.getUnidadeCalibracao().get());
        }
    }

    public ObservableField<GrandezaEnum> getGrandezaExibicao() {
        return grandezaExibicao;
    }

    public void setGrandezaExibicao(GrandezaEnum grandezaExibicao) {
        this.grandezaExibicao.set(grandezaExibicao);
    }

    public ObservableField<UnidadeEnum> getUnidadeExibicao() {
        return unidadeExibicao;
    }

    public void setUnidadeExibicao(UnidadeEnum unidadeExibicao) {
        this.unidadeExibicao.set(unidadeExibicao);
    }

    public ObservableDouble getTara() {
        return tara;
    }

    public void setTara(Double tara) {
        this.tara.set(tara);
    }

    public void tara() {
        if (this.service.getCondicionadorSinais().get() != null &&
                this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get() != null &&
                !Double.isNaN(this.service.getCondicionadorSinais().get().getUltimoValorLido().get())) {
            Double tara = this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get().getAjuste().get().getValorAjustado(
                    this.service.getCondicionadorSinais().get().getUltimoValorLido().get()
            );
            this.setTara(tara);
        }
        else {
            this.setTara(0.0);
        }
    }

    public Double getValorIndicador() {
        if (this.service.getCondicionadorSinais().get() != null &&
                this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get() != null &&
                !Double.isNaN(this.service.getCondicionadorSinais().get().getUltimoValorLido().get())) {
            Double valorAjustado = this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get().getAjuste().get().getValorAjustado(
                    this.service.getCondicionadorSinais().get().getUltimoValorLido().get());
            valorAjustado = valorAjustado - this.getTara().get();
            return valorAjustado;
        }
        else {
            return Double.NaN;
        }
    }

    public String getIndicacao() {
        Double valorIndicador = this.getValorIndicador();
        if (valorIndicador.isNaN()) {
            return this.service.getString(R.string.semIndicacao);
        }
        else {
            NumberFormat format = NumberFormat.getNumberInstance();
            format.setMaximumFractionDigits(4);
            format.setMinimumFractionDigits(4);
            String stringValue = format.format(valorIndicador);
            return stringValue;
        }
    }
}
