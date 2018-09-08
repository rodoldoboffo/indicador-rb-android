package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.view.View;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.condicionador.calibracao.Calibracao;

import java.util.List;

public class CalibracaoActivity extends AbstractListaItemActivity<Calibracao> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getSemItensStringResource() {
        return R.string.naoExistemCalibracoes;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (this.service != null) {
            this.inicializaObservadoresDoServico();
        }
    }

    @Override
    public List<Calibracao> getListaItens() {
        if (this.service != null) {
            return this.service.getGerenciadorCalibracao().getListaObjetos();
        }
        return null;
    }

    public void inicializaObservadorCalibracoes() {
        if (this.service != null) {
            for (Calibracao c : this.service.getGerenciadorCalibracao().getListaObjetos()) {
                this.inicializaObservadorCalibracao(c);
            }
        }
    }

    public void inicializaObservadorCalibracao(Calibracao c) {
        c.getSelecionado().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                CalibracaoActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
            }
        });
    }

    public void inicializaObservadorListaCalibracoes() {
        if (this.service != null) {
            this.service.getGerenciadorCalibracao().getListaObjetos().addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<Calibracao>>() {
                @Override
                public void onChanged(ObservableList<Calibracao> sender) {
                    CalibracaoActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList<Calibracao> sender, int positionStart, int itemCount) {
                    CalibracaoActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                }

                @Override
                public void onItemRangeInserted(ObservableList<Calibracao> sender, int positionStart, int itemCount) {
                    CalibracaoActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                    if (CalibracaoActivity.this.service != null) {
                        Calibracao c = CalibracaoActivity.this.service.getGerenciadorCalibracao().getListaObjetos().get(positionStart);
                        CalibracaoActivity.this.inicializaObservadorCalibracao(c);
                    }
                }

                @Override
                public void onItemRangeMoved(ObservableList<Calibracao> sender, int fromPosition, int toPosition, int itemCount) {
                    CalibracaoActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                }

                @Override
                public void onItemRangeRemoved(ObservableList<Calibracao> sender, int positionStart, int itemCount) {
                    CalibracaoActivity.this.getArrayListaItemAdapter().notifyDataSetChanged();
                }
            });
        }
    }

    public void inicializaObservadoresDoServico() {
        this.inicializaObservadorListaCalibracoes();
        this.inicializaObservadorCalibracoes();
    }

    @Override
    public void onNovoItemButtonClick(View view) {
        EditarCalibracaoActivity.novaCalibracao(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EditarCalibracaoActivity.NOVA_CALIBRACAO:
                this.resultadoNovaCalibracao(resultCode, data);
            default:
                break;
        }
    }

    private void resultadoNovaCalibracao(int resultCode, Intent data) {

    }

    @Override
    public void selecionarItem(Calibracao item) {
        super.selecionarItem(item);
        if (this.service != null) {
            this.service.getGerenciadorCalibracao().selecionaObjeto(item);
        }
    }

    @Override
    public void editarItem(Calibracao item) {
        super.editarItem(item);
        if (this.service != null) {
            EditarCalibracaoActivity.editarCalibracao(this, item);
        }
    }

    @Override
    public void removerItem(Calibracao item) {
        super.removerItem(item);
        if (this.service != null) {
            this.confirmaExclusaoCalibracaoDialog(item);
        }
    }

    private void confirmaExclusaoCalibracaoDialog(final Calibracao calibracao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(this.getString(R.string.confirmacaoExlcuirCalibracao))
                .setPositiveButton(this.getString(R.string.sim), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CalibracaoActivity.this.service.getGerenciadorCalibracao().removerObjeto(calibracao);
                    }
                })
                .setNegativeButton(this.getString(R.string.nao), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
