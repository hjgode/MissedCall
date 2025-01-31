package com.example.nikhil.detectcall.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.nikhil.detectcall.Constants;
import com.example.nikhil.detectcall.MainActivity;
import com.example.nikhil.detectcall.Services.CallService;


/**
 * Created by NIKHIL on 30-08-2018.
 */

public class MyReceiver extends BroadcastReceiver {

    private  static int wasRinging =0;
    private  static int wasPicked =0;
    private  static int isIdle =0;
    private  static String mobileNumber;


    @Override
    public void onReceive(Context context, Intent intent) {

        final MainActivity mains = new MainActivity();

        Bundle extras = intent.getExtras();

        if (extras!=null)
        {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.w("MY_DEBUG_TAG", state);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                wasRinging = 1;
                Log.d(Constants.TAG,"wasRinging: " + Integer.toString(wasRinging));
                Log.d(Constants.TAG,"wasPicked: "+Integer.toString(wasPicked));
                Log.d(Constants.TAG,"isIdle: "+Integer.toString(isIdle));

                Bundle bundle = intent.getExtras();
                mobileNumber= bundle.getString("incoming_number");

            }
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                wasPicked = 1;
            }

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {
                isIdle =1;

            }
        }

        if (wasRinging==1 && wasPicked ==0 && isIdle ==1)
        {

           Toast.makeText(context,"You didn't pick the call",Toast.LENGTH_LONG).show();


                    Log.d(Constants.TAG,"SendingMail: " +"MailSent");

            Intent i= new Intent(context, CallService.class);
            i.putExtra("num",mobileNumber);

//HGO            context.startService(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(i);
            } else {
                context.startService(i);
            }
            wasRinging=0;
            wasPicked=0;
            isIdle=0;
        }
        else
        if (wasRinging==1 && wasPicked ==1 && isIdle ==1)
        {

            Toast.makeText(context,"Picked call",Toast.LENGTH_LONG).show();
            wasRinging=0;
            wasPicked=0;
            isIdle=0;

        }

    }

}
