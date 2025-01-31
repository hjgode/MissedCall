package com.example.nikhil.detectcall.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.nikhil.detectcall.Constants;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.example.nikhil.detectcall.MainActivity.MyPREFERENCES;
import static com.example.nikhil.detectcall.MainActivity.Passw;
import static com.example.nikhil.detectcall.MainActivity.receiverE;
import static com.example.nikhil.detectcall.MainActivity.senderE;

/**
 * Created by NIKHIL on 06-09-2018.
 */

public class CallService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1,new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String numb = "xxxxx";

        if (intent!=null) {
            Bundle bundle =  intent.getExtras();
            numb = bundle.getString("num");

            BackgroundThread thread = new BackgroundThread(numb);
            thread.start();

        }

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public class BackgroundThread extends Thread
    {
        String  number;
        public BackgroundThread(String num) {

            this.number = num;
        }

        @Override
        public void run() {

            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            Session session;

            final String senderEmail = sharedpreferences.getString(senderE, "");
            final String password = sharedpreferences.getString(Passw, "");
            final String email = sharedpreferences.getString(receiverE, "");

            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String currentTime=simpleDateFormat.format(calendar.getTime());

            String subject = "Verpasster ANRUF";
            String message = "Verpasster Anruf: " + number + " um " + currentTime;

            //Creating properties
            Properties props = new Properties();
            Log.d(Constants.TAG, "building mail...");


            //Configuring properties for gmail
            //If you are not using gmail you may need to change the values
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            //Creating a new session
            session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        //Authenticating the password
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(senderEmail, password);


                        }
                    });
            Log.d(Constants.TAG, "SendMail: Session is "+session.toString());
            try {
                //Creating MimeMessage object
                MimeMessage mm = new MimeMessage(session);

                //Setting sender address
                mm.setFrom(new InternetAddress(senderEmail));

                //Adding receiver Email Address
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

                //Adding subject
                mm.setSubject(subject);

                //Adding message
                mm.setText(message);

                //Sending email
                Transport.send(mm);

            } catch (SendFailedException e){
                Log.d(Constants.TAG, "SendFailedException: " +e.getMessage());
            } catch (MessagingException e) {
                Log.d(Constants.TAG, "MessagingException: " + e.getMessage());
            }
        }
    }
}