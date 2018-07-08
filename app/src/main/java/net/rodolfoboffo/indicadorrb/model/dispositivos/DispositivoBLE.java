package net.rodolfoboffo.indicadorrb.model.dispositivos;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class DispositivoBLE extends AbstractServiceRelatedObject {

    public static final UUID UART_SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final  UUID UART_CHARACTERISTIC_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    public static final int RESPONSE_CONECTANDO_DISPOSITIVO = 4;
    public static final int RESPONSE_DISPOSITIVO_NAO_ENCONTRADO = 5;
    public static final int RESPONSE_DISPOSITIVO_NAO_ESTA_CONECTADO = 6;
    public static final int RESPONSE_DESCONECTANDO_DISPOSITIVO = 7;
    public static final int RESPONSE_DISPOSITIVO_JA_CONECTADO = 8;
    public static final int RESPONSE_DISPOSITIVO_NAO_ESTA_PRONTO = 9;
    public static final int RESPONSE_DADOS_ENVIADOS = 10;

    private ObservableField<String> nome;
    private ObservableField<String> endereco;
    private ObservableInt estadoBluetooth;
    private ObservableBoolean conectando;
    private ObservableBoolean servicosDescobertos;
    private ObservableBoolean pronto;
    private BluetoothDevice device;
    private BluetoothGattCallback gattCallback;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic uartCharacteristic;
    private ObservableField<Mensagem> mensagemRecebida;

    public DispositivoBLE(String nome, String endereco, IndicadorService servico) {
        super(servico);
        this.nome = new ObservableField<>(nome);
        this.endereco = new ObservableField<>(endereco);
        this.estadoBluetooth = new ObservableInt(BluetoothProfile.STATE_DISCONNECTED);
        this.conectando = new ObservableBoolean(false);
        this.servicosDescobertos = new ObservableBoolean(false);
        this.pronto = new ObservableBoolean(false);
        this.mensagemRecebida = new ObservableField<>();
        this.gattCallback = new BLEGattCallback();
        this.estadoBluetooth.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                DispositivoBLE.this.verificaPronto();
            }
        });
        this.servicosDescobertos.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                DispositivoBLE.this.verificaPronto();
            }
        });
    }

    public ObservableBoolean getConectando() {
        return conectando;
    }

    public ObservableField<Mensagem> getMensagemRecebida() {
        return mensagemRecebida;
    }

    private Boolean verificaPronto(int estadoBluetooth, Boolean servicoDescoberto) {
        if (estadoBluetooth == BluetoothProfile.STATE_CONNECTED && servicoDescoberto) {
            this.pronto.set(true);
        }
        else {
            this.pronto.set(false);
        }
        return this.pronto.get();
    }

    private Boolean verificaPronto() {
        return this.verificaPronto(this.estadoBluetooth.get(), this.servicosDescobertos.get());
    }

    public ObservableField<String> getNome() {
        return nome;
    }

    public ObservableField<String> getEndereco() {
        return endereco;
    }

    public ObservableInt getEstadoBluetooth() {
        return estadoBluetooth;
    }

    public ObservableBoolean isServicosDescobertos() {
        return servicosDescobertos;
    }

    public ObservableBoolean getPronto() {
        return pronto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DispositivoBLE that = (DispositivoBLE) o;
        if (this.nome.get() != null) {
            if (!this.nome.get().equals(that.nome.get())) return false;
        }
        if (this.endereco.get() != null) {
            if (!this.endereco.get().equals(that.endereco.get())) return false;
        }
        return true;
    }

    public int conectar() {
        if (this.service.getGerenciadorConexoes().verificarBluetoothAtivado()) {
            if (this.gatt != null) {
                return RESPONSE_DISPOSITIVO_JA_CONECTADO;
            }
            this.device = this.service.getGerenciadorConexoes().getBluetoothAdapter().getRemoteDevice(this.getEndereco().get());
            if (this.device != null) {
                this.device.connectGatt(this.service, false, this.gattCallback);
                this.conectando.set(true);
                Log.d(this.getClass().getName(), "Conectando dispositivo.");
                return RESPONSE_CONECTANDO_DISPOSITIVO;
            }
            else {
                return RESPONSE_DISPOSITIVO_NAO_ENCONTRADO;
            }
        }
        else {
            return GerenciadorDeDispositivos.RESPONSE_BLUETOOTH_NAO_ATIVADO;
        }
    }

    public int desconectar() {
        if (this.device != null) {
            if (this.gatt != null) {
                this.gatt.disconnect();
                return RESPONSE_DESCONECTANDO_DISPOSITIVO;
            }
            else {
                return RESPONSE_DISPOSITIVO_NAO_ESTA_CONECTADO;
            }
        }
        else {
            return RESPONSE_DISPOSITIVO_NAO_ENCONTRADO;
        }
    }

    public int enviarDados(byte[] data) {
        if (this.gatt != null && this.uartCharacteristic != null) {
            this.uartCharacteristic.setValue(data);
            this.gatt.writeCharacteristic(this.uartCharacteristic);
            Log.d(DispositivoBLE.this.getClass().getName(), "Dados enviados");
            return RESPONSE_DADOS_ENVIADOS;
        }
        else {
            return RESPONSE_DISPOSITIVO_NAO_ESTA_PRONTO;
        }
    }

    public int enviarDados(String data) {
        Log.d(this.getClass().getName(), String.format("Enviado String: %s", data));
        return this.enviarDados(data.getBytes());
    }

    private class BLEGattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTING) {
                Log.d(DispositivoBLE.this.getClass().getName(), "Conectando...");
                DispositivoBLE.this.gatt = null;
                DispositivoBLE.this.uartCharacteristic = null;
                DispositivoBLE.this.estadoBluetooth.set(newState);
                DispositivoBLE.this.servicosDescobertos.set(false);
            }
            else if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (gatt.discoverServices()) {
                        Log.d(DispositivoBLE.this.getClass().getName(), "Conectado!");
                        DispositivoBLE.this.gatt = gatt;
                        DispositivoBLE.this.estadoBluetooth.set(newState);
                        return;
                    }
                }
                Log.d(DispositivoBLE.this.getClass().getName(), "Algo deu errado...");
                DispositivoBLE.this.desconectar();
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(DispositivoBLE.this.getClass().getName(), "Desconectado :(");
                if (DispositivoBLE.this.gatt != null) {
                    DispositivoBLE.this.gatt.close();
                }
                DispositivoBLE.this.gatt = null;
                DispositivoBLE.this.uartCharacteristic = null;
                DispositivoBLE.this.conectando.set(false);
                DispositivoBLE.this.estadoBluetooth.set(newState);
                DispositivoBLE.this.servicosDescobertos.set(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status != BluetoothGatt.GATT_FAILURE) {
                BluetoothGattService uartService = gatt.getService(UART_SERVICE_UUID);
                if (uartService != null) {
                    BluetoothGattCharacteristic uartCharacteristic = uartService.getCharacteristic(UART_CHARACTERISTIC_UUID);
                    if (uartCharacteristic != null) {
                        DispositivoBLE.this.uartCharacteristic = uartCharacteristic;
                        Log.d(DispositivoBLE.this.getClass().getName(), "Serviços descobertos!");
                        DispositivoBLE.this.servicosDescobertos.set(true);
                        gatt.setCharacteristicNotification(DispositivoBLE.this.uartCharacteristic, true);
                        DispositivoBLE.this.conectando.set(false);
                        return;
                    }
                }
            }
            Log.d(DispositivoBLE.this.getClass().getName(), "Erro descobrindo serviços.");
            DispositivoBLE.this.conectando.set(false);
            DispositivoBLE.this.servicosDescobertos.set(false);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] received = characteristic.getValue();
            try {
                String receivedString = new String(received, "ASCII");
                Log.d(DispositivoBLE.this.getClass().getName(),"Recebido: " + receivedString);
                Mensagem mensagem = new Mensagem(receivedString);
                DispositivoBLE.this.mensagemRecebida.set(mensagem);
            } catch (UnsupportedEncodingException e) {
                Log.d(DispositivoBLE.this.getClass().getName(),"Erro na conversao de dados");
                e.printStackTrace();
            }
        }
    }
}
