package net.rodolfoboffo.indicadorrb.activities;

import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.databinding.Observable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.EnumArrayAdapter;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;

import java.util.Collections;

public class IndicadorActivity extends AbstractBaseActivity {

    private TextView indicacaoPrincipalText;
    private TextView velocidadeEnsaioText;
    private TextView picoText;
    private MenuItem conectarMenuItem;
    private Handler handler;
    private Spinner unidadeExibicaoSpinner;
    private EnumArrayAdapter spinnerUnidadeAdapter;

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
        this.velocidadeEnsaioText = this.findViewById(R.id.velocidadeEnsaioText);
        this.picoText = this.findViewById(R.id.picoText);
        this.spinnerUnidadeAdapter = new EnumArrayAdapter(this, Collections.emptyList(), R.layout.list_item_enum_big_black);
        this.unidadeExibicaoSpinner = this.findViewById(R.id.unidadeExibicaoSpinner);
        this.unidadeExibicaoSpinner.setAdapter(this.spinnerUnidadeAdapter);
        this.unidadeExibicaoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UnidadeEnum unidade = (UnidadeEnum) IndicadorActivity.this.spinnerUnidadeAdapter.getItem(position);
                IndicadorActivity.this.service.getIndicador().setUnidadeExibicao(unidade);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        this.inicializaObservadorGrandezaExibicao();
        this.inicializaObservadorUnidadeExibicao();
        this.inicializaObservadorCasasDecimais();
        this.inicializarObservadorCondicionador();
        this.inicializarObservadorConexao();
        this.inicializarObservadorVelocidade();
        this.inicializarObservadorPico();
    }

    private void inicializarObservadorVelocidade() {
        if (this.service != null) {
            this.service.getIndicador().getVelocidade().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.atualizaVelocidade();
                }
            });
            IndicadorActivity.this.atualizaVelocidade();
        }
    }

    private void inicializarObservadorPico() {
        if (this.service != null) {
            this.service.getIndicador().getPico().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.atualizaPico();
                }
            });
            IndicadorActivity.this.atualizaPico();
        }
    }

    private void atualizaVelocidade() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IndicadorActivity.this.service != null) {
                    String velocidade = IndicadorActivity.this.service.getIndicador().getIndicacaoVelocidadeEnsaio();
                    IndicadorActivity.this.setVelocidade(velocidade);
                }
            }
        });
    }

    private void atualizaPico() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IndicadorActivity.this.service != null) {
                    String pico = IndicadorActivity.this.service.getIndicador().getIndicacaoPico();
                    IndicadorActivity.this.setPico(pico);
                }
            }
        });
    }

    private void inicializaObservadorCasasDecimais() {
        if (this.service != null && this.service.getIndicador() != null) {
            this.service.getIndicador().getCasasDecimais().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.setIndicacao();
                }
            });
        }
    }

    private void inicializaObservadorGrandezaExibicao() {
        if (this.service != null && this.service.getIndicador() != null) {
            this.service.getIndicador().getGrandezaExibicao().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                IndicadorActivity.this.atualizaOpcoesSpinnerUnidadeExibicao();
                }
            });
            this.atualizaOpcoesSpinnerUnidadeExibicao();
        }
    }

    private void inicializaObservadorUnidadeExibicao() {
        if (this.service != null && this.service.getIndicador() != null) {
            this.service.getIndicador().getUnidadeExibicao().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.setIndicacao();
                }
            });
            this.setIndicacao();
        }
    }

    private void atualizaVisibilidadeSpinnerUnidadeExibicao() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IndicadorActivity.this.service != null) {
                    if (IndicadorActivity.this.service.getCondicionadorSinais() != null &&
                            IndicadorActivity.this.service.getGerenciadorCalibracao().getCalibracaoSelecionada().get() != null &&
                            IndicadorActivity.this.service.getCondicionadorSinais().get().getUltimoLeitura().get() != null) {
                        if (IndicadorActivity.this.unidadeExibicaoSpinner.getVisibility() != View.VISIBLE) {
                            IndicadorActivity.this.unidadeExibicaoSpinner.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (IndicadorActivity.this.unidadeExibicaoSpinner.getVisibility() != View.GONE) {
                            IndicadorActivity.this.unidadeExibicaoSpinner.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private void atualizaOpcoesSpinnerUnidadeExibicao() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IndicadorActivity.this.service != null) {
                    if (IndicadorActivity.this.service.getIndicador().getGrandezaExibicao().get() != null) {
                        IndicadorActivity.this.spinnerUnidadeAdapter = new EnumArrayAdapter(IndicadorActivity.this, UnidadeEnum.getAllByGrandeza(
                                IndicadorActivity.this.service.getIndicador().getGrandezaExibicao().get()
                        ), R.layout.list_item_enum_big_black);
                        IndicadorActivity.this.unidadeExibicaoSpinner.setAdapter(IndicadorActivity.this.spinnerUnidadeAdapter);
                        IndicadorActivity.this.unidadeExibicaoSpinner.setSelection(
                                IndicadorActivity.this.spinnerUnidadeAdapter.getListaEnums().indexOf(IndicadorActivity.this.service.getIndicador().getUnidadeExibicao().get())
                        );
                    } else {
                        IndicadorActivity.this.spinnerUnidadeAdapter = new EnumArrayAdapter(IndicadorActivity.this, Collections.emptyList(), R.layout.list_item_enum_big_black);
                        IndicadorActivity.this.unidadeExibicaoSpinner.setAdapter(IndicadorActivity.this.spinnerUnidadeAdapter);
                    }
                }
            }
        });
    }

    private void inicializarObservadorConexao() {
        if (this.service != null && this.service.getCondicionadorSinais().get() != null) {
            this.service.getCondicionadorSinais().get().getConexao().getPronto().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.habilitaComponentes();
                }
            });
            this.service.getCondicionadorSinais().get().getConexao().getEstadoBluetooth().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.habilitaConectarMenuItem();
                }
            });
            this.service.getCondicionadorSinais().get().getConexao().getConectando().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    IndicadorActivity.this.habilitaConectarMenuItem();
                }
            });
        }
        IndicadorActivity.this.habilitaComponentes();
        IndicadorActivity.this.habilitaConectarMenuItem();
    }

    private void inicializarObservadorCondicionador() {
        this.service.getCondicionadorSinais().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                IndicadorActivity.this.inicializarObservadorConexao();
                IndicadorActivity.this.habilitaComponentes();
            }
        });
        this.habilitaComponentes();
        this.habilitaConectarMenuItem();
    }

    private void habilitaConectarMenuItem() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IndicadorActivity.this.conectarMenuItem != null) {
                    if (IndicadorActivity.this.service == null || (IndicadorActivity.this.service != null && IndicadorActivity.this.service.getCondicionadorSinais().get() == null)) {
                        IndicadorActivity.this.conectarMenuItem.setTitle(getString(R.string.conectar));
                        IndicadorActivity.this.conectarMenuItem.setEnabled(false);
                        IndicadorActivity.this.conectarMenuItem.setVisible(false);
                    } else {
                        if (IndicadorActivity.this.service.getCondicionadorSinais().get().getConexao().getEstadoBluetooth().get() == BluetoothProfile.STATE_CONNECTED) {
                            IndicadorActivity.this.conectarMenuItem.setTitle(getString(R.string.desconectar));
                        } else {
                            IndicadorActivity.this.conectarMenuItem.setTitle(getString(R.string.conectar));
                        }
                        if (IndicadorActivity.this.service.getCondicionadorSinais().get().getConexao().getConectando().get()) {
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
        if (this.service != null && this.service.getCondicionadorSinais().get() != null && this.service.getCondicionadorSinais().get().getConexao().getPronto().get()) {
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

            }
        });
    }

    private void inicializaObservadorIndicacaoPrincipal() {
        if (this.service != null && this.service.getCondicionadorSinais().get() != null) {
            this.service.getCondicionadorSinais().get().getUltimoLeitura().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                        IndicadorActivity.this.setIndicacao();
                        IndicadorActivity.this.atualizaVisibilidadeSpinnerUnidadeExibicao();
                }
            });
        }
        this.setIndicacao();
        this.atualizaVisibilidadeSpinnerUnidadeExibicao();
    }

    private void setIndicacao(String indicacao) {
        this.indicacaoPrincipalText.setText(indicacao);
    }

    private void setVelocidade(String indicacao) {
        this.velocidadeEnsaioText.setText(indicacao);
    }

    private void setPico(String indicacao) {
        this.picoText.setText(indicacao);
    }

    private void setIndicacao() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IndicadorActivity.this.service != null) {
                    IndicadorActivity.this.setIndicacao(IndicadorActivity.this.service.getIndicador().getIndicacaoPrincipal());
                }
            }
        });
    }

    public void conectarDesconectarClick(MenuItem item) {
        if (this.service != null && this.service.getCondicionadorSinais().get() != null) {
            if (this.service.getCondicionadorSinais().get().getConexao().getPronto().get()) {
                this.service.getCondicionadorSinais().get().finalizar();
            }
            else {
                this.service.getCondicionadorSinais().get().inicializar();
            }
        }
    }

    private void zerar() {
        if (this.service != null) {
            this.service.getIndicador().tara();
        }
    }

    public void zeroButtonOnClick(View view) {
        this.zerar();
    }

    public void indicacaoPrincipalOnClick(View view) {
        this.aumentaCasasDecimais();
    }

    public void limparPicoButtonOnClick(View view) {
        this.limparPico();
    }

    private void limparPico() {
        if (this.service != null && this.service.getIndicador() != null) {
            this.service.getIndicador().limparPico();
        }
    }

    private void aumentaCasasDecimais() {
        if (this.service != null && this.service.getIndicador() != null) {
            this.service.getIndicador().aumentaCasasDecimais();
        }
    }
}
