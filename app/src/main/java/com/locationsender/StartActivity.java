package com.locationsender;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText etMobile = (EditText) findViewById(R.id.etMobile);
        ImageButton bStart = (ImageButton) findViewById(R.id.bStart);
        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = etMobile.getText().toString();
                boolean notAllNumbers = false;

                for(char c: mobile.toCharArray()){
                    if(c<'0' || c>'9'){
                        notAllNumbers = true;
                        break;
                    }
                }
                if((mobile.length() != 10) || notAllNumbers){
                    Toast.makeText(StartActivity.this, "Improper 10 digit Contact Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{

                    //Saving Contact
                    pref = getSharedPreferences(Globals.KEY_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString(Globals.KEY_CONTACT, mobile+"");
                    editor.commit();
                    //-- Saving Contact

                    Globals.MOBILE_NO = mobile;
                    Intent intent = new Intent(StartActivity.this, LocationActivity.class);
                    startActivity(intent);
                }
            }
        });

        ((TextView)findViewById(R.id.tvDesc)).animate().alpha(1).setDuration(1000);
    }

}
