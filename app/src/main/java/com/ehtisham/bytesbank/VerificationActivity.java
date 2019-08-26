package com.ehtisham.bytesbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class VerificationActivity extends AppCompatActivity
{

    String userContact;
    TextView details;
    AppDatabase db;
    String generatedToken;
    int token;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Bundle extras = getIntent().getExtras();
        db=new AppDatabase(getApplicationContext());
        userContact=extras.getString("contact");
        details=findViewById(R.id.details);
        details.setText(details.getText().toString()+userContact);
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(final String messageText, String contact) {
                Log.e("Message",messageText);
                if(messageText.contains("bytesbank") && messageText.contains(generatedToken.toString()))
                {
                    Toast.makeText(getApplicationContext(),"LOGGED IN\nPlease wait" , Toast.LENGTH_LONG).show();
                    db.TryLoadUserInformation(userContact);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(db.SaveUserInformation())
                            {
                                db.SetLoggedIn();
                                StartApplicationActivity();
                            }
                           finish();
                        }
                    }, 2000);
                }
            }
        });
                GenerateVerificationCode();
    }


    public void GenerateVerificationCode()
    {
        generatedToken=RandomInt();
        db.TrySendingServerRequest(userContact,"bytesbank:verify:"+generatedToken);
    }

    public String RandomInt()
    {
        Random r=new Random();
        token=r.nextInt((9999-1234)+1)+1;
        return String.valueOf(token);
    }


    protected  void StartApplicationActivity()
    {
        Intent activityIntent=new Intent(this,ApplicationActivity.class);
        this.startActivity(activityIntent);
    }



}
