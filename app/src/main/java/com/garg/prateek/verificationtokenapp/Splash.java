package com.garg.prateek.verificationtokenapp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Splash extends Activity {

    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //  no title bar
        setContentView(R.layout.activity_splash);

        final TextView uic = (TextView) findViewById(R.id.uic_logo);
        final TextView auth = (TextView) findViewById(R.id.fullscreen_content);

        //  Animation objects which are loaded with animation resource id's
        final Animation uicEnter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_uic_enter);
        final Animation authEnter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_auth_enter);

        Thread anim_enter = new Thread() {
            public void run() {
                uic.startAnimation(uicEnter);
                auth.startAnimation(authEnter);
            }
        };

        anim_enter.start();

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);    //  pausing the splash screen for 3 seconds
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    new PrefetchData().execute();
                }
            }
        };
        timer.start();
    }

    private class PrefetchData extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                InputStream inputStream = openFileInput("config.txt");

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    username = bufferedReader.readLine();
                    if(username != null){
                        return true;
                    }
                    else {
                        inputStream.close();
                        return false;
                    }
                }
            }
            catch (FileNotFoundException e) {
                return false;
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Bundle animBundle = ActivityOptions.makeCustomAnimation(Splash.this,
                    R.anim.window_fade_in, R.anim.window_fade_out).toBundle();
            if (result) {
                Intent i = new Intent(getApplicationContext(), AuthToken.class);
                i.putExtra("Username", username);
                startActivity(i, animBundle);
                finish();
            }
            else {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("Username", username);
                startActivity(i, animBundle);
                finish();
            }
        }
    }
}