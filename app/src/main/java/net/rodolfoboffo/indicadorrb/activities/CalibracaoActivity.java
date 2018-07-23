package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.ArrayCalibracoesAdapter;
import net.rodolfoboffo.indicadorrb.model.indicador.calibracao.Calibracao;

public class CalibracaoActivity extends AbstractBaseActivity implements View.OnCreateContextMenuListener {

    private ArrayCalibracoesAdapter arrayCalibracoesAdapter;
    private ListView listaCalibracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.listaCalibracoes = this.findViewById(R.id.listViewCalibracoes);
        this.arrayCalibracoesAdapter = new ArrayCalibracoesAdapter(this);
        this.listaCalibracoes.setAdapter(this.arrayCalibracoesAdapter);
        this.listaCalibracoes.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (this.service != null) {
            this.arrayCalibracoesAdapter.setCalibracoes(this.service.getGerenciadorCalibracao().getListaCalibracoes());
            this.inicializaObservadoresDoServico();
        }
    }

    public void inicializaObservadoresDoServico() {
        if (this.service != null) {
            this.service.getGerenciadorCalibracao().getListaCalibracoes().addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<Calibracao>>() {
                @Override
                public void onChanged(ObservableList<Calibracao> sender) {
                    CalibracaoActivity.this.arrayCalibracoesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList<Calibracao> sender, int positionStart, int itemCount) {
                    CalibracaoActivity.this.arrayCalibracoesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeInserted(ObservableList<Calibracao> sender, int positionStart, int itemCount) {
                    CalibracaoActivity.this.arrayCalibracoesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeMoved(ObservableList<Calibracao> sender, int fromPosition, int toPosition, int itemCount) {
                    CalibracaoActivity.this.arrayCalibracoesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeRemoved(ObservableList<Calibracao> sender, int positionStart, int itemCount) {
                    CalibracaoActivity.this.arrayCalibracoesAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_calibracao;
    }

    public void onNovaCalibracaoButtonClick(View view) {
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_calibracoes, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = contextMenuInfo.position;
        if (this.service != null) {
            Calibracao calibracao = this.service.getGerenciadorCalibracao().getListaCalibracoes().get(position);
            switch (item.getItemId()) {
                case R.id.editarCalibracao:
                    EditarCalibracaoActivity.editarCalibracao(this, calibracao);
                    break;
                case R.id.selecionarCalibracao:
                    this.service.getGerenciadorCalibracao().selecionaCalibracao(calibracao);
                    break;
                case R.id.removerCalibracao:
                    this.confirmaExclusaoCalibracaoDialog(calibracao);
                    break;
            }
        }
        return true;
    }

    private void confirmaExclusaoCalibracaoDialog(final Calibracao calibracao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(this.getString(R.string.confirmacaoExlcuirCalibracao))
                .setPositiveButton(this.getString(R.string.sim),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CalibracaoActivity.this.service.getGerenciadorCalibracao().removerCalibracao(calibracao);
                    }
                })
                .setNegativeButton(this.getString(R.string.nao), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
