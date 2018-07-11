package net.rodolfoboffo.indicadorrb.activities;

import android.bluetooth.BluetoothProfile;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.indicador.AbstractIndicador;

public class IndicadorActivity extends AbstractBaseActivity {

    private TextView indicacaoPrincipalText;
    private Switch switchIndicacaoAutomatica;
    private MenuItem conectarMenuItem;
    private Handler handler;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_indicador;
    }

    @Override
    protected int getOptionsMenu() {
        return R.menu.indicador_menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new Handler();
        this.indicacaoPrincipalText = this.findViewById(R.id.indicacao_principal_text);
        this.switchIndicacaoAutomatica = this.findViewById(R.id.switchAquisicaoAutomatica);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean returnValue = super.onCreateOptionsMenu(menu);
        this.conectarMenuItem = menu.findItem(R.id.conectarDesconectarMenuItem);
        this.habilitaConectarMenuItem();
        return returnValue;
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
        this.inicializarObservadorConexao();
    }

    private void inicializarObservadorConexao() {
        if (this.service != null && this.service.getIndicador().get() != null) {
            this.service.getIndicador().get().getConexao().getPronto().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.habilitaComponentes();
                }
            });
            this.service.getIndicador().get().getConexao().getEstadoBluetooth().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.habilitaConectarMenuItem();
                }
            });
            this.service.getIndicador().get().getConexao().getConectando().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.habilitaConectarMenuItem();
                }
            });
        }
        IndicadorActivity.this.habilitaComponentes();
        IndicadorActivity.this.habilitaConectarMenuItem();
    }

    private void inicializarObservadorIndicador() {
        this.service.getIndicador().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ObservableField<AbstractIndicador> indicador = (ObservableField<AbstractIndicador>)sender;
                IndicadorActivity.this.inicializarObservadorConexao();
                IndicadorActivity.this.habilitaComponentes();
            }
        });
        IndicadorActivity.this.habilitaComponentes();
        IndicadorActivity.this.habilitaConectarMenuItem();
    }

    private void habilitaConectarMenuItem() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IndicadorActivity.this.conectarMenuItem != null) {
                    if (IndicadorActivity.this.service == null || (IndicadorActivity.this.service != null && IndicadorActivity.this.service.getIndicador().get() == null)) {
                        IndicadorActivity.this.conectarMenuItem.setTitle(getString(R.string.conectar));
                        IndicadorActivity.this.conectarMenuItem.setEnabled(false);
                        IndicadorActivity.this.conectarMenuItem.setVisible(false);
                    } else {
                        if (IndicadorActivity.this.service.getIndicador().get().getConexao().getEstadoBluetooth().get() == BluetoothProfile.STATE_CONNECTED) {
                            IndicadorActivity.this.conectarMenuItem.setTitle(getString(R.string.desconectar));
                        } else {
                            IndicadorActivity.this.conectarMenuItem.setTitle(getString(R.string.conectar));
                        }
                        if (IndicadorActivity.this.service.getIndicador().get().getConexao().getConectando().get()) {
                            IndicadorActivity.this.conectarMenuItem.setEnabled(false);
                        } else {
                            IndicadorActivity.this.conectarMenuItem.setEnabled(true);
                        }
                    }
                }
            }
        });
    }

    private void habilitaComponentes() {
        if (this.service != null && this.service.getIndicador().get() != null && this.service.getIndicador().get().getConexao().getPronto().get()) {
            this.habilitaComponentes(true);
        }
        else {
            this.habilitaComponentes(false);
        }
    }

    private void habilitaComponentes(final Boolean habilita) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                IndicadorActivity.this.switchIndicacaoAutomatica.setEnabled(habilita);
            }
        });
    }

    private void inicializaObservadorIndicacaoPrincipal() {
        if (this.service != null && this.service.getIndicador().get() != null) {
            this.service.getIndicador().get().getUltimoValorLido().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                        IndicadorActivity.this.setIndicacao();
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
                    IndicadorActivity.this.setAquisicaoAutomatica();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IndicadorActivity.this.service != null && IndicadorActivity.this.service.getIndicador().get() != null) {
                    IndicadorActivity.this.setAquisicaoAutomatica(IndicadorActivity.this.service.getIndicador().get().getAquisicaoAutomatica().get());
                }
            }
        });
    }

    private void setAquisicaoAutomatica(Boolean ativado) {
        this.switchIndicacaoAutomatica.setChecked(ativado);
    }

    private void setIndicacao() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IndicadorActivity.this.service != null && IndicadorActivity.this.service.getIndicador().get() != null) {
                    IndicadorActivity.this.setIndicacao(IndicadorActivity.this.service.getIndicador().get().getUltimoValorLido().get());
                }
                else {
                    IndicadorActivity.this.setIndicacao(Double.NaN);
                }
            }
        });
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

    public void conectarDesconectarClick(MenuItem item) {
        if (this.service != null && this.service.getIndicador().get() != null) {
            if (this.service.getIndicador().get().getConexao().getPronto().get()) {
                this.service.getIndicador().get().getConexao().desconectar();
            }
            else {
                this.service.getIndicador().get().getConexao().conectar();
            }
        }
    }
}
