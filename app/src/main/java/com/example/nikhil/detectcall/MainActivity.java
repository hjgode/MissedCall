package com.example.nikhil.detectcall;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikhil.detectcall.Receivers.MyReceiver;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {

    EditText sender, pass, receiverEmail,pin;
    TextView textView;
    Button btn;

    // Shared Preference
    public  static  SharedPreferences sharedpreferences;


    public static final String MyPREFERENCES = "MyPrefs";
    public static final String senderE = "SenderEmail";
    public static final String Passw = "Password";
    public static final String receiverE = "ReceiverEmail";
    public static final String AccessPin = "MasterPin";

    static String emailx;
    static String senderx;
    static String passx;
    private static MainActivity instance;
    Context context=this;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sender = findViewById(R.id.SenderEmail);
        pass = findViewById(R.id.Email_Password);
        receiverEmail = findViewById(R.id.ReceiverEmail);
        btn = findViewById(R.id.Btn);
        pin = findViewById(R.id.Mpin);


        textView = findViewById(R.id.tv);


        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_PHONE_STATE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        /* On Permission Granted */

                        sender.setEnabled(true);
                        pass.setEnabled(true);
                        receiverEmail.setEnabled(true);
                        btn.setEnabled(true);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response)
                    {
                        /*  Make Input Field Inaccessible*/
                        sender.setEnabled(false);
                        pass.setEnabled(false);
                        receiverEmail.setEnabled(false);
                        btn.setEnabled(false);

                        textView.setText(" Please Grant Permission.\nEither Uninstall or Grant Permission From Settings");


                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {

                        Toast.makeText(getApplicationContext(),"Need Read Phone State Permission To Detect Call",Toast.LENGTH_LONG).show();
                        textView.setText(" Please Grant Permission.\nEither Uninstall or Grant Permission From Settings");

                        sender.setEnabled(false);
                        pass.setEnabled(false);
                        receiverEmail.setEnabled(false);
                        btn.setEnabled(false);


                    }
                }).check();


        //Code For Shared Preference
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(receiverE)) {
            receiverEmail.setText(sharedpreferences.getString(receiverE, ""));

            // Getting Receiver Email Address From Shared Preference

            emailx = sharedpreferences.getString(receiverE, "");
            Log.d(Constants.TAG,"ReceiversEmail" + emailx);
        }
        if (sharedpreferences.contains(senderE)) {
            sender.setText(sharedpreferences.getString(senderE, ""));

            // Getting Sender Email Address From Shared Preference
            senderx = sharedpreferences.getString(senderE, "");

        }
        if (sharedpreferences.contains(Passw)) {
            pass.setText(sharedpreferences.getString(Passw, ""));

            // Getting Password From Shared Preference

            passx = sharedpreferences.getString(Passw, "");
        }
        if (sharedpreferences.contains(AccessPin)) {
            pin.setText(sharedpreferences.getString(AccessPin, ""));

            // Getting Access-Pin From Shared Preference

            passx = sharedpreferences.getString(Passw, "");

        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senderEmail = sender.getText().toString();
                String password = pass.getText().toString();
                String receiver = receiverEmail.getText().toString();
                String getpin = pin.getText().toString();

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(senderE, senderEmail);
                editor.putString(Passw, password);
                editor.putString(receiverE, receiver);
                editor.putString(AccessPin,getpin);

                editor.commit();

                Toast.makeText(MainActivity.this, "Details Saved In Your Mobile", Toast.LENGTH_LONG).show();


            }
        });


    }

    MyReceiver myReceiver;
    public void registerTelephonyReceiver(){
        final IntentFilter filter=new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        myReceiver=new MyReceiver();
        context.registerReceiver( myReceiver, filter);
    }
    public void unregisterTelephonyReceiver(){
        if (myReceiver!=null){
            context.unregisterReceiver(myReceiver);
        }
    }
}
