package com.vineethp.minixcarcontrol;

/**
 * Created by Vineeth Penugonda on 05-04-2017.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Vineeth Penugonda on 04-04-2017.
 */

class BluetoothConnectT extends Thread {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    public BluetoothConnectT(BluetoothDevice device)
    {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public int run(BluetoothAdapter mBluetoothAdapter) {
        // Cancel discovery because it otherwise slows down the connection.
        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            return 1;
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
                return 0;
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
                return -1;
            }
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        //  manageMyConnectedSocket(mmSocket);
    }

    public BluetoothSocket get_mmsocket()
    {
        return mmSocket;
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}



