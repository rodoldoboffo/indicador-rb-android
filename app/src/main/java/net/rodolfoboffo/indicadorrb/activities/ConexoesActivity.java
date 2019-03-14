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
import android.widget.TextView;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.adapter.ArrayConexoesAdapter;
import net.rodolfoboffo.indicadorrb.model.dispositivos.DispositivoBLE;
import net.rodolfoboffo.indicadorrb.model.dispositivos.GerenciadorDeDispositivos;

public class ConexoesActivity extends AbstractBaseActivity implements View.OnCreateContextMenuListener{
    private ProgressBar progressBarProcurandoConexoes;
    private ListView listViewConexoes;
    private TextView textViewSemConexoes;
    private ArrayConexoesAdapter adapterConexoes;
    private ObservableList.OnListChangedCallback<ObservableList<DispositivoBLE>> observableListaConexoesCallback;
    private Observable.OnPropertyChangedCallback oservableConexoesCallback;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_conexoes;
    }

    @Override
    protected int getOptionsMenu() {
        return R.menu.conexoes_menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.progressBarProcurandoConexoes = this.findViewById(R.id.progressProcurandoDispositivos);
        this.listViewConexoes = this.findViewById(R.id.listViewDispositivos);
        this.textViewSemConexoes = this.findViewById(R.id.textViewSemConexao);
        this.adapterConexoes = new ArrayConexoesAdapter(this);
        this.listViewConexoes.setEmptyView(this.textViewSemConexoes);
        this.listViewConexoes.setAdapter(this.adapterConexoes);
        this.listViewConexoes.setOnCreateContextMenuListener(this);
        this.observableListaConexoesCallback = new ObservableList.OnListChangedCallback<ObservableList<DispositivoBLE>>() {
            @Override
            public void onChanged(ObservableList<DispositivoBLE> sender) {
                Log.d(ConexoesActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                ConexoesActivity.this.adapterConexoes.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                Log.d(ConexoesActivity.this.getClass().getSimpleName(), "Lista de dispositivos foi alterada.");
                ConexoesActivity.this.adapterConexoes.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeInserted(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                if (ConexoesActivity.this.service != null) {
                    Log.d(ConexoesActivity.this.getClass().getSimpleName(), "Dispositivo inserido na lista de dispositivos.");
                    DispositivoBLE dispositivo = ConexoesActivity.this.service.getGerenciadorConexoes().getListaConexoes().get(positionStart);
                    ConexoesActivity.this.inicializaObservadorDispositivo(dispositivo);
                    ConexoesActivity.this.adapterConexoes.notifyDataSetChanged();
                }
            }

            @Override
            public void onItemRangeMoved(ObservableList<DispositivoBLE> sender, int fromPosition, int toPosition, int itemCount) {
                Log.d(ConexoesActivity.this.getClass().getSimpleName(), "Dispositivo movido na lista de dispositivos.");
                ConexoesActivity.this.adapterConexoes.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<DispositivoBLE> sender, int positionStart, int itemCount) {
                Log.d(ConexoesActivity.this.getClass().getSimpleName(), "Dispositivo removido na lista de dispositivos.");
                ConexoesActivity.this.adapterConexoes.notifyDataSetChanged();
            }
        };
        this.oservableConexoesCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.d(ConexoesActivity.this.getClass().getSimpleName(), "Estado do dispositivo foi alterado.");
                ConexoesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ConexoesActivity.this.adapterConexoes.notifyDataSetChanged();
                    }
                });
            }
        };
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (this.service != null) {
            this.adapterConexoes.setDispositivos(this.service.getGerenciadorConexoes().getListaConexoes());
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
            for (DispositivoBLE dispositivo : this.service.getGerenciadorConexoes().getListaConexoes()) {
                this.inicializaObservadorDispositivo(dispositivo);
            }
        }
    }

    private void inicializaObservadorDispositivo(DispositivoBLE dispositivo) {
        dispositivo.getPronto().addOnPropertyChangedCallback(ConexoesActivity.this.oservableConexoesCallback);
        dispositivo.getConectando().addOnPropertyChangedCallback(ConexoesActivity.this.oservableConexoesCallback);
    }

    private void inicializaObservadorProgressBar() {
        if (this.service != null) {
            this.service.getGerenciadorConexoes().isAtualizando().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    ConexoesActivity.this.exibeProgressBarAtualizando(((ObservableBoolean) sender).get());
                }
            });
            ConexoesActivity.this.exibeProgressBarAtualizando(this.service.getGerenciadorConexoes().isAtualizando().get());
        }
    }

    private void inicializaObservadorListaDispositivos() {
        if (this.service != null) {
            this.service.getGerenciadorConexoes().getListaConexoes().addOnListChangedCallback(ConexoesActivity.this.observableListaConexoesCallback);
            ConexoesActivity.this.adapterConexoes.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.vibrarCurto();
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.context_menu_conexoes, menu);
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = contextMenuInfo.position;
        if (this.service != null) {
            DispositivoBLE dispositivoBLE = this.service.getGerenciadorConexoes().getListaConexoes().get(position);
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
        this.vibrarCurto();
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = contextMenuInfo.position;
        if (this.service != null) {
            DispositivoBLE dispositivo = this.service.getGerenciadorConexoes().getListaConexoes().get(position);
            switch (item.getItemId()) {
                case R.id.conectarDispositivo:
                    if (this.service.getCondicionadorSinais().get() != null) {
                        this.service.getCondicionadorSinais().get().finalizar();
                    }
                    this.service.criaCondicionadorSinais(dispositivo);
                    int response = this.service.getCondicionadorSinais().get().inicializar();
                    this.trataRespostaDispositivo(response);
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
        this.vibrarCurto();
        switch (item.getItemId()) {
            case R.id.scanDispositivos:
                return this.verificarDispositivosButtonClick();
            case R.id.limparDispositivos:
                return this.limparDispositivosButtonClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean verificarDispositivosButtonClick() {
        this.vibrarCurto();
        if (this.service != null) {
            int response = this.service.getGerenciadorConexoes().atualizarListaDispositivos();
            this.trataRespostaDispositivo(response);
        }
        return true;
    }

    private Boolean limparDispositivosButtonClick() {
        this.vibrarCurto();
        if (this.service != null) {
            this.service.getGerenciadorConexoes().limpaListaDispositivos();
        }
        return true;
    }

    private void exibeProgressBarAtualizando(Boolean exibe) {
        int visibilidade = exibe ? View.VISIBLE : View.GONE;
        progressBarProcurandoConexoes.setVisibility(visibilidade);
    }
}
