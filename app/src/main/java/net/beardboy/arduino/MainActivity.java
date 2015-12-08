package net.beardboy.arduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_ENABLE_BLUETOOTH = 1000;
    public static final String KEY_DEVICE_ADDRESS = "KEY_DEVICE_ADDRESS";

    private ListView listViewDevices;
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private List<String> bluetoothDevicesNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listViewDevices = (ListView) findViewById(R.id.listView_devices);

        Button buttonSearchDevices = (Button) findViewById(R.id.button_search_devices);
        buttonSearchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) {
                    showSimpleDialog("Bluetooth no encontrado", "Éste dispositivo no cuenta con un modulo bluetooth");
                } else {
                    if (bluetoothAdapter.isEnabled()) {
                        setUpDevicesList();
                    } else {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
                    }
                }
            }
        });
    }

    private void setUpDevicesList() {

        Set<BluetoothDevice> bondedDevice = bluetoothAdapter.getBondedDevices();

        if (bondedDevice.size() == 0) {
            showSimpleDialog("Emparejar dispositivo", "No se encontraron dispositivos emparejados");
        } else {
            for (BluetoothDevice bluetoothDevice : bondedDevice) {
                bluetoothDevices.add(bluetoothDevice);
                bluetoothDevicesNames.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, bluetoothDevicesNames);
            listViewDevices.setAdapter(adapter);
            listViewDevices.setOnItemClickListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE_BLUETOOTH:
                    setUpDevicesList();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice bluetoothDevice = bluetoothDevices.get(position);
        Intent intent = new Intent(this, DeviceManagementActivity.class);
        intent.putExtra(KEY_DEVICE_ADDRESS, bluetoothDevice.getAddress());
        startActivity(intent);
    }

    private void showSimpleDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
