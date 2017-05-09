package com.vineethp.minixcarcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_ENABLE_BT=1;
    TextView status;
    SeekBar accelerationskb;
    BluetoothAdapter mBluetoothAdapter;
    IntentFilter bdisc;
    private BluetoothDevice bDevice;
    private final String DEVICE_ADDRESS = "B8:27:EB:A6:2A:45";
    BluetoothConnectT connect;
    BluetoothSocket mmSocket;
    private boolean bconnection_status; // Connection status
    List<String> commands = new ArrayList<String>();
    CustomTouchListener ctl;
    private Button forwardBtn;
    private Button backwardBtn;
    private Button leftBtn;
    private Button rightBtn;
    private Button brakeBtn;
    int connection_status; // Connection status
    int globalprogressCV;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView)findViewById(R.id.statustext);
        accelerationskb = (SeekBar)findViewById(R.id.accelerationseekbar);
        status.setText("Application has started!");
        bconnection_status = false;
        // Commands
        commands.add("Go=Forward");
        commands.add("Go=Backward");
        commands.add("Go=Right");
        commands.add("Go=Left");
        commands.add("Apply=Brake");
        commands.add("Acc");
        // Finding Buttons
        forwardBtn = (Button) findViewById(R.id.forwardBtn);
        backwardBtn = (Button) findViewById(R.id.backwardBtn);
        leftBtn = (Button) findViewById(R.id.leftBtn);
        rightBtn = (Button) findViewById(R.id.rightBtn);
        brakeBtn = (Button) findViewById(R.id.brakeBtn);
        accelerationskb.setProgress(0);
        disable_UI();

        Handler startbhandler = new Handler();
        startbhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startBluetooth();
            }
        }, 2000);
        accelerationskb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                globalprogressCV = progressChangedValue;
                send_message(1,5);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Current Acceleration: " + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId())
        {
            case R.id.action_settings:
                if(bconnection_status == true) {
                    if (menu.getItem(0).getIcon().getConstantState().equals(ContextCompat.getDrawable(this, R.drawable.ic_action_override_settings).getConstantState())) {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_enable_settings));
                        menu.getItem(0).setTitle("Enable Settings");
                        brakeBtn.setEnabled(false);
                    } else if (menu.getItem(0).getIcon().getConstantState().equals(ContextCompat.getDrawable(this, R.drawable.ic_action_enable_settings).getConstantState())) {

                        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_override_settings));
                        menu.getItem(0).setTitle("Override Settings");
                        brakeBtn.setEnabled(true);
                    }
                }
                return true;

            case R.id.action_crash_alert:
              if(bconnection_status == true)
                {
                            Intent i = new Intent(this, CrashDetection.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            i.setClassName(this, "com.vineethp.minixcarcontrol.CrashDetection");
                            startActivity(i);
                } else {

                    Toast.makeText(this, "MiniX Car Control App is not connected to raspberrypi", Toast.LENGTH_SHORT).show();
                }

                return true;
        }




        return super.onOptionsItemSelected(item);
    }



    public void disable_UI()
    {
        forwardBtn.setEnabled(false);
        backwardBtn.setEnabled(false);
        leftBtn.setEnabled(false);
        rightBtn.setEnabled(false);
        brakeBtn.setEnabled(false);
        accelerationskb.setEnabled(false);
    }

    public void enable_UI()
    {
        forwardBtn.setEnabled(true);
        backwardBtn.setEnabled(true);
        leftBtn.setEnabled(true);
        rightBtn.setEnabled(true);
        accelerationskb.setEnabled(true);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    public void goLeft(View v){
        if(bconnection_status == true)
        {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    send_message(0,3);
                    return true;
                }
            });
            Toast toast = Toast.makeText(getApplicationContext(), "Going Left", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void goRight(View v){
        if(bconnection_status == true) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    send_message(0,2);
                    return true;
                }
            });
            Toast toast = Toast.makeText(getApplicationContext(), "Going Right", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void goForward(View v){
        if(bconnection_status == true) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                   send_message(0,0);
                    return true;
                }
            });
                Toast toast = Toast.makeText(getApplicationContext(), "Going Forward", Toast.LENGTH_SHORT);
            toast.show();


        }
    }

    public void goBackward(View v){
        if(bconnection_status == true) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    send_message(0,1);
                    return true;
                }
            });
            Toast toast = Toast.makeText(getApplicationContext(), "Going Backward", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void applyBrake(View v) {
        if (bconnection_status == true) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    send_message(0,4);
                    return true;
                }
            });
            Toast toast = Toast.makeText(getApplicationContext(), "Brake Pressed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_ENABLE_BT)
        {
            if(resultCode == RESULT_OK)
            {
                status.setText("Bluetooth is enabled!");
                Handler qBhandler = new Handler();
                qBhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queryBluetoothdevices();
                    }
                }, 2000);

            } else if(resultCode == RESULT_CANCELED) {
                status.setText("Bluetooth is not enabled!");
            }
        }
    }

    protected void startBluetooth()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            status.setText("Bluetooth Not Found!");
        } else if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); // Calls onActivityResult
        } else if(mBluetoothAdapter.isEnabled())
        {
            status.setText("Bluetooth is already enabled!");
            Handler qBhandler = new Handler();
            qBhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    queryBluetoothdevices();
                }
            }, 2000);

        }

    }


    protected void queryBluetoothdevices()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        String deviceName = "Device Names:";
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device: pairedDevices)
            {
                if(device.getAddress().equals(DEVICE_ADDRESS)) {
                    bDevice = device;
                    // Since we found the Bluetooth device let's connect it!
                    connect = new BluetoothConnectT(bDevice);

                            connection_status = connect.run(mBluetoothAdapter);
                        }
                    if(connection_status == 1)
                    {
                        status.setText("Connected to: " + bDevice.getName());
                        bconnection_status = true;
                        enable_UI();
                    }
                    else if(connection_status == 0)
                    {
                        status.setText("Could not connect to Bluetooth Device");
                    }
                    else if(connection_status == -1)
                    {
                        status.setText("Failed to close socket!");
                    }


                }

        } else {
            status.setText("I didn't connect to any device before!");
            Handler bdischandler = new Handler();
            bdischandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    discBluetoothdevices();
                }
            }, 2000);
        }

    }

    protected void discBluetoothdevices() {
        if(mBluetoothAdapter.isDiscovering())
        {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        bdisc = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, bdisc);
        status.setText("Discovering Bluetooth Devices...");
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String discBDevice = "Found Device" + device.getName();
                Toast toast = Toast.makeText(getApplicationContext(), discBDevice, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };


    protected void send_message(int type, int cmd_index)
    {
            mmSocket = connect.get_mmsocket();
            ConnectedThread msg = new ConnectedThread(mmSocket);
        if(type == 0) { // Button = 0
            int sent_status = msg.write(commands.get(cmd_index).getBytes());
            if (sent_status == 1) {
                status.setText("Message Successfully Sent: " + commands.get(cmd_index));
            } else if (sent_status == 0) {
                status.setText("Message Failed To Send: " + commands.get(cmd_index));
            }
        } else if(type == 1)
        {
            String message = commands.get(cmd_index)+"="+String.valueOf(globalprogressCV);
            int sent_status = msg.write(message.getBytes());
            if (sent_status == 1) { // Acceleration Seekbar
                status.setText("Message Successfully Sent: " + String.valueOf(globalprogressCV));
            } else if (sent_status == 0) {
                status.setText("Message Failed To Send: " + String.valueOf(globalprogressCV));
            }
        }
    }

}
