package com.vineethp.minixcarcontrol;

import android.bluetooth.BluetoothSocket;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vineeth Penugonda on 30-04-2017.
 */

public class CustomTouchListener implements View.OnTouchListener {

    BluetoothSocket mmSocket;
    String btn_name;
    ConnectedThread msg;
    List<String> commands = new ArrayList<String>();
    TextView status;

    public CustomTouchListener(BluetoothConnectT connect, String btnname, TextView txtstatus){
        mmSocket=connect.get_mmsocket();
        btn_name=btnname;
        msg = new ConnectedThread(mmSocket);
        status = txtstatus;
        commands.add("Go=Forward");
        commands.add("Go=Backward");
        commands.add("Go=Right");
        commands.add("Go=Left");
        commands.add("Apply=Brake");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
                if(btn_name == "forwardBtn")
                {
                    int sent_status = msg.write(commands.get(1).getBytes());
                    if (sent_status == 1) {
                        status.setText("Command Sent: " + commands.get(1));
                    } else if (sent_status == 0) {
                        status.setText("Command Failed To Send: " + commands.get(1));
                    }

                }
        return false;
    }


}


