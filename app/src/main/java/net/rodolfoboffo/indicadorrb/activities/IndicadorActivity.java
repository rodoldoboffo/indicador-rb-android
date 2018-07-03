package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.indicador.AbstractIndicador;

public class IndicadorActivity extends AbstractBaseActivity {

    private TextView indicacaoPrincipalText;
    private Switch switchIndicacaoAutomatica;

    private Handler handler;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_indicador;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new Handler();
        this.indicacaoPrincipalText = this.findViewById(R.id.indicacao_principal_text);
        this.switchIndicacaoAutomatica = this.findViewById(R.id.switchAquisicaoAutomatica);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (this.service != null) {
            this.inicializaObservadoresDoServico();
        }
    }

    public void inicializaObservadoresDoServico() {
        this.inicializaObservadorIndicacaoPrincipal();
        this.inicializarObservadorAquisicaoAutomatica();
        this.inicializarObservadorIndicador();
    }

    private void inicializarObservadorIndicador() {
        this.service.getIndicador().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ObservableField<AbstractIndicador> indicador = (ObservableField<AbstractIndicador>)sender;
                if (indicador.get() != null) {
                    indicador.get().getDispositivo().getPronto().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            IndicadorActivity.this.habilitaComponentes();
                        }
                    });
                }
                IndicadorActivity.this.habilitaComponentes();
            }
        });
        IndicadorActivity.this.habilitaComponentes();
    }

    private void habilitaComponentes() {
        if (this.service != null && this.service.getIndicador().get() != null && this.service.getIndicador().get().getDispositivo().getPronto().get()) {
            this.habilitaComponentes(true);
        }
        else {
            this.habilitaComponentes(false);
        }
    }

    private void habilitaComponentes(Boolean habilita) {
        this.switchIndicacaoAutomatica.setEnabled(habilita);
    }

    private void inicializaObservadorIndicacaoPrincipal() {
        if (this.service != null && this.service.getIndicador().get() != null) {
            this.service.getIndicador().get().getUltimoValorLido().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IndicadorActivity.this.setIndicacao();
                        }
                    });
                }
            });
        }
        this.setIndicacao();
    }

    private void inicializarObservadorAquisicaoAutomatica() {
        if (this.service != null && this.service.getIndicador().get() != null) {
            this.service.getIndicador().get().getAquisicaoAutomatica().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IndicadorActivity.this.setAquisicaoAutomatica();
                        }
                    });
                }
            });
        }
        this.setAquisicaoAutomatica();
    }

    private void setIndicacao(Double indicacao) {
        if (indicacao.isNaN()) {
            this.indicacaoPrincipalText.setText("Sem indicação");
        }
        else {
            this.indicacaoPrincipalText.setText(String.valueOf(indicacao));
        }
    }

    private void setAquisicaoAutomatica() {
        if (this.service != null && this.service.getIndicador().get() != null) {
            this.setAquisicaoAutomatica(this.service.getIndicador().get().getAquisicaoAutomatica().get());
        }
    }

    private void setAquisicaoAutomatica(Boolean ativado) {
        this.switchIndicacaoAutomatica.setChecked(ativado);
    }

    private void setIndicacao() {
        if (this.service != null && this.service.getIndicador().get() != null) {
            this.setIndicacao(this.service.getIndicador().get().getUltimoValorLido().get());
        }
        else {
            this.setIndicacao(Double.NaN);
        }
    }

    public void aquisicaoAutomaticaOnClick(View view) {
        this.alternaAquisicaoAutomatica();
    }

    private void alternaAquisicaoAutomatica() {
        if (this.service != null && this.service.getIndicador().get() != null) {
            this.switchIndicacaoAutomatica.setEnabled(false);
            Boolean ligado = IndicadorActivity.this.service.getIndicador().get().getAquisicaoAutomatica().get();
            this.service.getIndicador().get().iniciarAquisicaoAutomatica(!ligado);
            this.handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            IndicadorActivity.this.switchIndicacaoAutomatica.setChecked(IndicadorActivity.this.service.getIndicador().get().getAquisicaoAutomatica().get());
                            IndicadorActivity.this.switchIndicacaoAutomatica.setEnabled(true);
                        }
                    },
                    500
            );
        }
    }
}
