package net.beardboy.arduino;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.UUID;

public class DeviceManagementActivity extends AppCompatActivity implements View.OnClickListener {

    private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private ProgressDialog progressDialog;
    private boolean isBluetoothDeviceConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showSimpleExitDialog("Bluetooth no encontrado", "Éste dispositivo no cuenta con un modulo bluetooth");
        } else {
            Intent intent = getIntent();
            String address = intent.getStringExtra(MainActivity.KEY_DEVICE_ADDRESS);
            ConnectBluetooth connectBluetooth = new ConnectBluetooth();
            connectBluetooth.execute(address);
            Button buttonRed = (Button) findViewById(R.id.button_red);
            buttonRed.setOnClickListener(this);
            Button buttonYellow = (Button) findViewById(R.id.button_yellow);
            buttonYellow.setOnClickListener(this);
            Button buttonGreen = (Button) findViewById(R.id.button_green);
            buttonGreen.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (isBluetoothDeviceConnected && bluetoothSocket != null) {
            switch (v.getId()) {
                case R.id.button_red:
                    try {
                        bluetoothSocket.getOutputStream().write(4);
                    } catch (IOException e) {
                        //e.printStackTrace();
                        showSimpleDialog("Error", e.getMessage());
                    }
                    break;
                case R.id.button_yellow:
                    try {
                        bluetoothSocket.getOutputStream().write(3);
                    } catch (IOException e) {
                        //e.printStackTrace();
                        showSimpleDialog("Error", e.getMessage());
                    }
                    break;
                case R.id.button_green:
                    try {
                        bluetoothSocket.getOutputStream().write(2);
                    } catch (IOException e) {
                        //e.printStackTrace();
                        showSimpleDialog("Error", e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                //e.printStackTrace();
                Log.i(DeviceManagementActivity.class.getSimpleName(), e.getMessage());
            }
        }
    }

    private void showSimpleExitDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceManagementActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSimpleDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceManagementActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    class ConnectBluetooth extends AsyncTask<String, Void, Boolean> {

        private String errorMessage = "No message";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(DeviceManagementActivity.this, "Conectando con dispositivo", "Por favor espera...", true, false);

        }

        @Override
        protected Boolean doInBackground(String... params) {

            if (bluetoothSocket == null || !isBluetoothDeviceConnected) {
                String address = params[0];
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
                try {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(bluetoothDevice.getUuids()[0].getUuid());
                    bluetoothSocket.connect();
                    return bluetoothSocket.isConnected();
                } catch (IOException e) {
                    //e.printStackTrace();
                    errorMessage = e.getMessage();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            isBluetoothDeviceConnected = result;
            if (!result) {
                showSimpleExitDialog("Error al connectar dispositivo", errorMessage);
            }
        }
    }
}
