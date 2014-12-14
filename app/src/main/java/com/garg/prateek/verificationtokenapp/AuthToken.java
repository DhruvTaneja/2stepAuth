package com.garg.prateek.verificationtokenapp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class AuthToken extends Activity {

    public Bundle animBundle = ActivityOptions.makeCustomAnimation(AuthToken.this,
            R.anim.window_fade_in, R.anim.window_fade_out).toBundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_token);
        String salt = "";
        String user = "";
        String timeStamp = "";
        try {
            FileInputStream fileIn=openFileInput("config.txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[100];
            String everything="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                everything +=readstring;
            }
            InputRead.close();

            int counter =0;
            char ch[] = everything.toCharArray();
            while(counter<3){
                for (int i=0;i<ch.length;i++) {
                    if(ch[i] == ':' || i == ch.length -1) {
                        counter++;
                        if(i!=ch.length-1)
                            i++;
                    }
                    switch (counter) {
                        case 0: user = user + ch[i];
                            break;
                        case 1: salt += ch[i];
                            break;
                        case 2: timeStamp += ch[i];
                            break;
                    }
                }
            }
        }
        catch(FileNotFoundException e){
            //File not found
            System.out.println("File was not found");
            Intent i = new Intent(getApplicationContext(), Splash.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i, animBundle);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        makeToken(user, salt, timeStamp);
    }

    private void makeToken(String user, String salt, String timeDB){
        String saltHash = "";
        String userHash = "";
        String timeDBHash = "";
        String semiFinal;
        String semiFinalHash = "";
        if ((!user.equals("") && !salt.equals("")) || (!user.equals("") && !user.equals(""))){

            MessageDigest md;
            try{
                md = MessageDigest.getInstance("SHA-512");

                md.update(salt.getBytes());
                byte[] mb = md.digest();

                for (int i = 0; i < mb.length; i++) {
                    byte temp = mb[i];
                    String s = Integer.toHexString(new Byte(temp));
                    while (s.length() < 2) {
                        s = "0" + s;
                    }
                    s = s.substring(s.length() - 2);
                    saltHash += s;
                }
                md = MessageDigest.getInstance("SHA-512");

                md.update(user.getBytes());
                byte[] mbUser = md.digest();

                for (int i = 0; i < mb.length; i++) {
                    byte temp = mbUser[i];
                    String s = Integer.toHexString(new Byte(temp));
                    while (s.length() < 2) {
                        s = "0" + s;
                    }
                    s = s.substring(s.length() - 2);
                    userHash += s;
                }
                md = MessageDigest.getInstance("SHA-512");

                md.update(timeDB.getBytes());
                byte[] mbTimeDB = md.digest();

                for (int i = 0; i < mb.length; i++) {
                    byte temp = mbTimeDB[i];
                    String s = Integer.toHexString(new Byte(temp));
                    while (s.length() < 2) {
                        s = "0" + s;
                    }
                    s = s.substring(s.length() - 2);
                    timeDBHash += s;
                }
                semiFinal = userHash + timeDBHash + saltHash;
                md = MessageDigest.getInstance("SHA-512");

                md.update(semiFinal.getBytes());
                byte[] mbSemiFinal = md.digest();

                for (int i = 0; i < mb.length; i++) {
                    byte temp = mbSemiFinal[i];
                    String s = Integer.toHexString(new Byte(temp));
                    while (s.length() < 2) {
                        s = "0" + s;
                    }
                    s = s.substring(s.length() - 2);
                    semiFinalHash += s;
                }

            }catch(NoSuchAlgorithmException e){
                //Do something
            }
        }
        System.out.println("Semi final : " + semiFinalHash);
        String finalHash = semiFinalHash.substring(0,9);
                //return null;
        System.out.println("Hash: " + finalHash);
        TextView tv = (TextView) findViewById(R.id.AuthToken);
        tv.setText(finalHash);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auth_token, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(getApplicationContext(), About.class);
            startActivity(intent, animBundle);
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(getApplicationContext(), Tooltip.class);
            startActivity(intent, animBundle);
        }
        if (id == R.id.action_logout) {
            if (deleteFile("config.txt")) {
                Toast.makeText(getApplicationContext(), "You have successfully logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Splash.class);
                startActivity(intent, animBundle);
                finish();
            }
            else
                Toast.makeText(getApplicationContext(), "Log out failed", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.window_fade_in, R.anim.window_fade_out);
    }
}
