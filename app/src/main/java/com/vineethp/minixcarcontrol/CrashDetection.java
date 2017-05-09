package com.vineethp.minixcarcontrol;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class CrashDetection extends AppCompatActivity {

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_detection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        this.menu = menu;
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_home));
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId())
        {

            case R.id.action_crash_alert:
                //  if(connection_status == 1)
                //{
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                i.setClassName(this, "com.vineethp.minixcarcontrol.MainActivity");
                startActivity(i);
                //} else {

                //Toast.makeText(this, "MiniX Car Control App is not connected to raspberrypi", Toast.LENGTH_SHORT).show();
                //}

                return true;
        }




        return super.onOptionsItemSelected(item);
    }
}
