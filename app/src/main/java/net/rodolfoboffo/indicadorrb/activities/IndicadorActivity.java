package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
import android.databinding.Observable;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;

public class IndicadorActivity extends AbstractBaseActivity {

    private TextView indicacaoPrincipalText;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_indicador;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.indicacaoPrincipalText = this.findViewById(R.id.indicacao_principal_text);
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

    private void setIndicacao(Double indicacao) {
        if (indicacao.isNaN()) {
            this.indicacaoPrincipalText.setText("Sem indicação");
        }
        else {
            this.indicacaoPrincipalText.setText(String.valueOf(indicacao));
        }
    }

    private void setIndicacao() {
        if (this.service != null && this.service.getIndicador().get() != null) {
            this.setIndicacao(this.service.getIndicador().get().getUltimoValorLido().get());
        }
        else {
            this.setIndicacao(Double.NaN);
        }
    }
}
