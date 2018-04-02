package my.com.engpeng.engpeng.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;

import my.com.engpeng.engpeng.loader.AppLoader;
import my.com.engpeng.engpeng.utilities.UIUtils;

/**
 * Created by Admin on 28/2/2018.
 */

public class BluetoothConnection {

    public static final int BLUETOOTH_REQUEST_CODE = 8001;

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;


    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;

    private BluetoothConnection.BluetoothConnectionListener bcListener;

    public interface BluetoothConnectionListener {
        void afterPrintDone();
    }

    public Set<BluetoothDevice> getPairedBluetoothDevicesList(Context context) {
        try {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) context).startActivityForResult(enableBluetooth, BLUETOOTH_REQUEST_CODE);
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                return pairedDevices;
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public void initPrint(Context context, String address, String text, BluetoothConnectionListener bcListener) {
        this.bcListener = bcListener;

        byte[] buffer = text.getBytes();
        byte[] PrintHeader = {(byte) 0xAA, 0x55, 2, 0};
        PrintHeader[3] = (byte) buffer.length;
        boolean isPrinted = print(context, address);

        if (isPrinted) {
            try {
                outputStream.write(buffer);
                outputStream.close();
                socket.close();
                bcListener.afterPrintDone();
            } catch (Exception ex) {
                UIUtils.showToastMessage(context, ex.toString());
            }
        } else {
            UIUtils.showToastMessage(context, "Please try again.");
        }
    }

    private boolean print(Context context, String address) {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bluetoothDevice = null;

        try {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) context).startActivityForResult(enableBluetooth, BLUETOOTH_REQUEST_CODE);
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getAddress().equals(address)) ;
                    {
                        bluetoothDevice = device;
                        break;
                    }
                }

                Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                socket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                beginListenForData();

            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    private void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {
                            int bytesAvailable = inputStream.available();

                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.d("e", data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
