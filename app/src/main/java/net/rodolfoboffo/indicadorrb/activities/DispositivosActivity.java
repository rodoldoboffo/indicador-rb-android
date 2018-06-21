package net.rodolfoboffo.indicadorrb.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.ArrayDispositivosAdapter;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.model.dispositivos.GerenciadorDeDispositivos;

public class DispositivosActivity extends AbstractBaseActivity implements View.OnCreateContextMenuListener{
    private ProgressBar progressBarProcurandoDispositivos;
    private ListView listViewDispositivos;
    private ArrayDispositivosAdapter adapterDispositivos;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_dispositivos;
    }

    @Override
    protected int getOptionsMenu() {
        return R.menu.dispositivos_menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.progressBarProcurandoDispositivos = this.findViewById(R.id.progressProcurandoDispositivos);
        this.listViewDispositivos = this.findViewById(R.id.listViewDispositivos);
        this.adapterDispositivos = new ArrayDispositivosAdapter(this);
        this.listViewDispositivos.setAdapter(this.adapterDispositivos);
        this.listViewDispositivos.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (this.service != null) {
            this.adapterDispositivos.setDispositivos(this.service.getGerenciadorDispositivos().getListaDispositivos());
            this.inicializaObservadoresDoServico();
        }
    }

    public void inicializaObservadoresDoServico() {
        this.inicializaObservadorProgressBar();
        this.inicializaObservadorListaDispositivos();
    }

    private void inicializaObservadorProgressBar() {
        DispositivosActivity.this.exibeProgressBarAtualizando(this.service.getGerenciadorDispositivos().isAtualizando().get());
        if (this.service != null) {
            this.service.getGerenciadorDispositivos().isAtualizando().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    DispositivosActivity.this.exibeProgressBarAtualizando(((ObservableBoolean) sender).get());
                }
            });
        }
    }

    private void inicializaObservadorListaDispositivos() {
        if (this.service != null) {
            this.service.getGerenciadorDispositivos().getListaDispositivos().addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<DispositivoBLE>>() {
                @Override
                public void onChanged(ObservableList<DispositivoBLE> sender) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeInserted(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeMoved(ObservableList<DispositivoBLE> sender, int fromPosition, int toPosition, int itemCount) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeRemoved(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                    Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                    DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                }
            });
            DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_dispositivos, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.conectarDispositivo:
                AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int position = contextMenuInfo.position;
                DispositivoBLE dispositivo = this.service.getGerenciadorDispositivos().getListaDispositivos().get(position);
                dispositivo.conectar();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanDispositivos:
                return this.verificarDispositivosButtonClick();
            case R.id.limparDispositivos:
                return this.limparDispositivosButtonClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean verificarDispositivosButtonClick() {
        if (this.service != null) {
            int response = this.service.getGerenciadorDispositivos().atualizarListaDispositivos();
            if (response == GerenciadorDeDispositivos.RESPONSE_BLUETOOTH_NAO_ATIVADO) {
                this.requisitarAtivacaoBluetooth();
            }
            else if (response == GerenciadorDeDispositivos.RESPONSE_SEM_PERMISSAO_DE_LOCALIDADE) {
                this.requisitarPermissoesDeLocalidade();
            }
        }
        return true;
    }

    private Boolean limparDispositivosButtonClick() {
        if (this.service != null) {
            this.service.getGerenciadorDispositivos().limpaListaDispositivos();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUISITAR_PERMISSOES_LOCALIDADE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.service.getGerenciadorDispositivos().atualizarListaDispositivos();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUISITAR_ATIVACAO_BLUETOOTH:
                this.resultAtivacaoBluetooth(resultCode, data);
                break;
        }
    }

    private void resultAtivacaoBluetooth(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.service.getGerenciadorDispositivos().atualizarListaDispositivos();
        }
    }

    private void exibeProgressBarAtualizando(Boolean exibe) {
        int visibilidade = exibe ? View.VISIBLE : View.GONE;
        progressBarProcurandoDispositivos.setVisibility(visibilidade);
    }
}
