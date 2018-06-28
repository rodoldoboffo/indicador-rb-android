package net.rodolfoboffo.indicadorrb.activities;

import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.IBinder;
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
    private ObservableList.OnListChangedCallback<ObservableList<DispositivoBLE>> observableListaDispositivosCallback;
    private Observable.OnPropertyChangedCallback oservableDispositivoCallback;

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
        this.observableListaDispositivosCallback = new ObservableList.OnListChangedCallback<ObservableList<DispositivoBLE>>() {
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
                Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Dispositivo inserido na lista de dispositivos.");
                DispositivoBLE dispositivo = DispositivosActivity.this.service.getGerenciadorDispositivos().getListaDispositivos().get(positionStart);
                DispositivosActivity.this.inicializaObservadorDispositivo(dispositivo);
                DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeMoved(ObservableList<DispositivoBLE> sender, int fromPosition, int toPosition, int itemCount) {
                Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Dispositivo movido na lista de dispositivos.");
                DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Dispositivo removido na lista de dispositivos.");
                DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
            }
        };
        this.oservableDispositivoCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.d(DispositivosActivity.this.getClass().getSimpleName(), "Estado do dispositivo foi alterado.");
                DispositivosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
                    }
                });
            }
        };
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
        this.inicializaObservadoresDispositivos();
        this.inicializaObservadorListaDispositivos();
    }

    private void inicializaObservadoresDispositivos() {
        if (this.service != null) {
            for (DispositivoBLE dispositivo : this.service.getGerenciadorDispositivos().getListaDispositivos()) {
                this.inicializaObservadorDispositivo(dispositivo);
            }
        }
    }

    private void inicializaObservadorDispositivo(DispositivoBLE dispositivo) {
        dispositivo.getPronto().addOnPropertyChangedCallback(DispositivosActivity.this.oservableDispositivoCallback);
        dispositivo.getConectando().addOnPropertyChangedCallback(DispositivosActivity.this.oservableDispositivoCallback);
    }

    private void inicializaObservadorProgressBar() {
        if (this.service != null) {
            this.service.getGerenciadorDispositivos().isAtualizando().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    DispositivosActivity.this.exibeProgressBarAtualizando(((ObservableBoolean) sender).get());
                }
            });
            DispositivosActivity.this.exibeProgressBarAtualizando(this.service.getGerenciadorDispositivos().isAtualizando().get());
        }
    }

    private void inicializaObservadorListaDispositivos() {
        if (this.service != null) {
            this.service.getGerenciadorDispositivos().getListaDispositivos().addOnListChangedCallback(DispositivosActivity.this.observableListaDispositivosCallback);
            DispositivosActivity.this.adapterDispositivos.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_dispositivos, menu);
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = contextMenuInfo.position;
        if (this.service != null) {
            DispositivoBLE dispositivoBLE = this.service.getGerenciadorDispositivos().getListaDispositivos().get(position);
            if (dispositivoBLE.getEstadoBluetooth().get() == BluetoothProfile.STATE_CONNECTED) {
                MenuItem desconectarMenuItem = menu.findItem(R.id.desconectarDispositivo);
                desconectarMenuItem.setEnabled(true);
            }
            else {
                MenuItem conectarMenuItem = menu.findItem(R.id.conectarDispositivo);
                conectarMenuItem.setEnabled(true);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = contextMenuInfo.position;
        if (this.service != null) {
            DispositivoBLE dispositivo = this.service.getGerenciadorDispositivos().getListaDispositivos().get(position);
            switch (item.getItemId()) {
                case R.id.conectarDispositivo:
                    this.service.iniciarIndicador(dispositivo);
                    break;
                case R.id.desconectarDispositivo:
                    dispositivo.desconectar();
                    break;
            }
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

    private void exibeProgressBarAtualizando(Boolean exibe) {
        int visibilidade = exibe ? View.VISIBLE : View.GONE;
        progressBarProcurandoDispositivos.setVisibility(visibilidade);
    }
}
