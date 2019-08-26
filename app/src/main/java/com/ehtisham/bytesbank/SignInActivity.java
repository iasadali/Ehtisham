package com.ehtisham.bytesbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class SignInActivity extends AppCompatActivity
{

    Button btnSignIn,btnSignUp;
    EditText tb_contact;
    Intent activity_intent;
    AppDatabase db;
    View snck;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        db=new AppDatabase(getApplicationContext());
        btnSignIn=findViewById(R.id.btn_SignIn);
        btnSignUp=findViewById(R.id.btn_SignUp);
        tb_contact=findViewById(R.id.tb_contact);
        btnSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                snck=v;
                if(Validate(v))
                {
                    db.TryCansignInResult(tb_contact.getText().toString());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            TrySignIn();
                        }
                    }, 2000);
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                StartSignUpActivity();
            }
        });
    }





    public void TrySignIn()
    {

        Integer response=Integer.valueOf(db.GetCanSignIn());
        if(response==400)
        {
            Log.d("ERR","Coming inside>>>>>>>>>>>>>>>>>>>");
            DoesNotExistingAccount(snck);
        }
        else
        {
            if(response==200)
            StartVerificationActivity();
            else
            {
                Toast.makeText(getApplicationContext(), "No Connectivity!", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected  void StartSignUpActivity()
    {
        activity_intent = new Intent(this,   SignUpActivity.class);
        startActivity(activity_intent);
    }


    protected  void StartVerificationActivity()
    {
        activity_intent = new Intent(this,   VerificationActivity.class);
        activity_intent.putExtra("contact",tb_contact.getText().toString());
        startActivity(activity_intent);
    }

    public Boolean Validate(View v)
    {
        if(tb_contact.getText().toString().length()!=11)
        {
            ShowSnackbar(v,"Invalid Contact No.");
            return false;
        }
        return true;
    }


    public void DoesNotExistingAccount(View view)
    {
        ShowSnackbar(view,"Account doesn't exist!\nTry Sign up?");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                StartSignUpActivity();
            }
        }, 2000);
    }


    public void ShowSnackbar(View view,String message)
    {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }



}
