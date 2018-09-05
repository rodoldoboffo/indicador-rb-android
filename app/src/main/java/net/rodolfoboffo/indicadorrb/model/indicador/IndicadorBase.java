package net.rodolfoboffo.indicadorrb.model.indicador;

import android.databinding.Observable;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.Leitura;
import net.rodolfoboffo.indicadorrb.model.basicos.Medicao;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.Calibracao;
import net.rodolfoboffo.indicadorrb.model.math.ConversorUnidades;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndicadorBase extends AbstractServiceRelatedObject {

    public static final int MAXIMO_CASAS_DECIMAIS = 8;
    public static final int NUMERO_LEITURAS_VELOCIDADE = 5;

    private ObservableField<GrandezaEnum> grandezaExibicao;
    private ObservableField<UnidadeEnum> unidadeExibicao;
    private ObservableDouble velocidade;
    private ObservableField<Medicao> pico;
    private ObservableField<List<Leitura>> ultimasLeituras;
    private ObservableInt casasDecimais;
    private ObservableDouble tara;

    public IndicadorBase(IndicadorService service) {
        super(service);
        this.velocidade = new ObservableDouble(0.0);
        this.pico = new ObservableField<>();
        this.ultimasLeituras = new ObservableField<>((List<Leitura>)new ArrayList<Leitura>());
        this.grandezaExibicao = new ObservableField<>();
        this.unidadeExibicao = new ObservableField<>();
        this.casasDecimais = new ObservableInt(4);
        this.tara = new ObservableDouble(0.0);
        this.inicializaObservadores();
    }

    private void resetTudo() {
        this.grandezaExibicao.set(null);
        this.unidadeExibicao.set(null);
        this.tara.set(0.0);
        this.resetUltimasLeituras();
    }

    private void resetUltimasLeituras() {
        this.ultimasLeituras.get().clear();
        this.ultimasLeituras.notifyChange();
    }

    private void inicializaObservadores() {
        this.inicializaObservadorCalibracaoSelecionada();
        this.inicializaObservadorCondicionadorSinais();
        this.inicializaObservadorUltimasLeituras();
        this.inicializaObservaorUnidadeExibicao();
        this.inicializaObservaorCasasDecimais();
    }

    private void inicializaObservaorCasasDecimais() {
        this.casasDecimais.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                IndicadorBase.this.velocidade.notifyChange();
                IndicadorBase.this.pico.notifyChange();
            }
        });
    }

    private void inicializaObservaorUnidadeExibicao() {
        this.unidadeExibicao.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                UnidadeEnum unidade = ((ObservableField<UnidadeEnum>)sender).get();
                IndicadorBase.this.atualizaVelocidadeEnsaio();
                IndicadorBase.this.atualizaPico(IndicadorBase.this.pico.get().converte(unidade));
            }
        });
        IndicadorBase.this.atualizaVelocidadeEnsaio();
        IndicadorBase.this.atualizaPico();
    }

    private void inicializaObservadorUltimasLeituras() {
        this.ultimasLeituras.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                IndicadorBase.this.atualizaVelocidadeEnsaio();
            }
        });
    }

    private void inicializaObservadorCondicionadorSinais() {
        this.service.getCondicionadorSinais().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                IndicadorBase.this.resetUltimasLeituras();
                IndicadorBase.this.inicializaObservadorUltimaLeitura();
            }
        });
        IndicadorBase.this.resetUltimasLeituras();
        IndicadorBase.this.inicializaObservadorUltimaLeitura();
    }

    private void inicializaObservadorUltimaLeitura() {
        if (this.service.getCondicionadorSinais().get() != null) {
            this.service.getCondicionadorSinais().get().getUltimoLeitura().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorBase.this.adicionaUltimaLeitura();
                    IndicadorBase.this.atualizaPico();
                }
            });
        }
    }

    private void zeraPico() {
        if (this.unidadeExibicao.get() != null) {
            this.pico.set(new Medicao(0.0, this.unidadeExibicao.get()));
        }
    }

    private void atualizaPico() {
        Medicao medicaoAtual = this.getMedicaoIndicador();
        if (medicaoAtual != null) {
            Medicao medicaoConvertida = medicaoAtual.converte(this.pico.get().getUnidade().get());
            if (this.pico.get() == null ||  medicaoConvertida.getValor().get() > this.pico.get().getValor().get()) {
                this.atualizaPico(medicaoConvertida);
            }
        }
        else if (this.pico.get() == null) {
            this.atualizaPico(new Medicao(0.0, this.unidadeExibicao.get()));
        }
    }

    private void atualizaPico(Medicao medicao) {
        this.pico.set(medicao);
    }

    private void adicionaUltimaLeitura() {
        synchronized (this.ultimasLeituras.get()) {
            Leitura l = this.service.getCondicionadorSinais().get().getUltimoLeitura().get();
            this.ultimasLeituras.get().add(l);
            Collections.sort(this.ultimasLeituras.get());
            if (this.ultimasLeituras.get().size() > NUMERO_LEITURAS_VELOCIDADE) {
                this.ultimasLeituras.get().remove(0);
            }
            this.ultimasLeituras.notifyChange();
        }
    }

    private synchronized void atualizaVelocidadeEnsaio() {
        synchronized (this.ultimasLeituras.get()) {
            this.velocidade.set(this.calculaVelocidadeEnsaio(this.ultimasLeituras.get()));
            Log.d(this.getClass().getName(), "Velocidade: " + String.valueOf(this.velocidade.get()));
        }
    }

    private Double calculaVelocidadeEnsaio(List<Leitura> ultimasLeituras) {
        if (ultimasLeituras.size() < 2 ||
                this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get() == null) {
            return 0.0;
        }
        Leitura ultimaLeitura = ultimasLeituras.get(ultimasLeituras.size()-1);
        Leitura primeira = ultimasLeituras.get(0);
        Long milis = ultimaLeitura.getHora().get().getTime() - primeira.getHora().get().getTime();
        final Calibracao c = this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get();
        final UnidadeEnum unidadeExibicao = this.unidadeExibicao.get();
        Double ultimaIndicacao = this.getMedicaoIndicador(ultimaLeitura.getValor().get(), c, unidadeExibicao);
        Double primeiraIndicacao = this.getMedicaoIndicador(primeira.getValor().get(), c, unidadeExibicao);
        Double velocidade = (ultimaIndicacao - primeiraIndicacao) / (milis / 1000.0);
        return velocidade;
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
            this.resetTudo();
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
                this.service.getCondicionadorSinais().get().getUltimoLeitura().get() != null) {
            Double tara = this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get().getAjuste().get().getValorAjustado(
                    this.service.getCondicionadorSinais().get().getUltimoLeitura().get().getValor().get()
            );
            this.setTara(tara);
        }
        else {
            this.setTara(0.0);
        }
    }

    public Medicao getMedicaoIndicador() {
        if (this.service.getCondicionadorSinais().get() != null &&
                this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get() != null &&
                this.service.getCondicionadorSinais().get().getUltimoLeitura().get() != null) {
            Double valorPronto = this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get().getAjuste().get().getValorAjustado(
                    this.service.getCondicionadorSinais().get().getUltimoLeitura().get().getValor().get());
            valorPronto = valorPronto - this.getTara().get();
            valorPronto = ConversorUnidades.converte(valorPronto,
                                                    this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get().getUnidadeCalibracao().get(),
                                                    this.unidadeExibicao.get());
            return new Medicao(valorPronto, this.unidadeExibicao.get());
        }
        else {
            return null;
        }
    }

    private Double getMedicaoIndicador(Double valorLido, Calibracao calibracao, UnidadeEnum unidadeExibicao) {
        if (calibracao != null) {
            Double valorPronto = calibracao.getAjuste().get().getValorAjustado(valorLido);
            valorPronto = valorPronto - this.getTara().get();
            valorPronto = ConversorUnidades.converte(valorPronto,
                                                    calibracao.getUnidadeCalibracao().get(),
                                                    unidadeExibicao);
            return valorPronto;
        }
        else {
            return Double.NaN;
        }
    }

    public String getIndicacaoVelocidadeEnsaio() {
        Double valorIndicador = this.getVelocidade().get();
        if (!Double.isNaN(valorIndicador)) {
            String indicacao = String.format("%s %s/s",
                    this.getIndicao(valorIndicador),
                    this.service.getString(this.unidadeExibicao.get().getResourceString())
                    );
            return indicacao;
        }
        return "";
    }

    public String getIndicacaoPico() {
        Medicao medicaoIndicador = this.getPico().get();
        if (medicaoIndicador != null) {
            String indicacao = String.format("%s %s",
                    this.getIndicao(medicaoIndicador.getValor().get()),
                    this.service.getString(medicaoIndicador.getUnidade().get().getResourceString())
            );
            return indicacao;
        }
        return "";
    }

    public String getIndicacaoPrincipal() {
        Medicao medicaoIndicador = this.getMedicaoIndicador();
        if (medicaoIndicador != null) {
            String valorString = this.getIndicao(medicaoIndicador.getValor().get());
            return valorString;
        }
        return this.service.getString(R.string.semIndicacao);
    }

    private String getIndicao(Double medicao) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(this.casasDecimais.get());
        format.setMinimumFractionDigits(this.casasDecimais.get());
        String stringValue = format.format(medicao);
        return stringValue;
    }

    public void aumentaCasasDecimais() {
        this.setCasasDecimais((this.casasDecimais.get()+1) % (MAXIMO_CASAS_DECIMAIS + 1));
    }

    public void setCasasDecimais(Integer casasDecimais) {
        if (casasDecimais >= 0 && casasDecimais <= MAXIMO_CASAS_DECIMAIS) {
            this.casasDecimais.set(casasDecimais);
        }
    }

    public ObservableInt getCasasDecimais() {
        return casasDecimais;
    }

    public ObservableDouble getVelocidade() {
        return velocidade;
    }

    public ObservableField<Medicao> getPico() {
        return pico;
    }
}
