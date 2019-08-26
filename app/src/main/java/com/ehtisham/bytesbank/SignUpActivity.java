package com.ehtisham.bytesbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.SigningInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity
{

    Button btnSignIn,btnSignUp;
    EditText tb_name,tb_contact,tb_password;
    Intent activity_intent;
    AppDatabase db;
    View snck;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        db=new AppDatabase(getApplicationContext());
        tb_name=findViewById(R.id.tb_name);
        tb_contact=findViewById(R.id.tb_contact);
        tb_password=findViewById(R.id.tb_password);
        btnSignIn=findViewById(R.id.btn_SignIn);
        btnSignUp=findViewById(R.id.btn_SignUp);
        btnSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                StartSignInActivity();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                snck=v;
                if(Validate(v))
                {
                    db.TryRegisterUser(tb_name.getText().toString(),tb_contact.getText().toString(),tb_password.getText().toString());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            RegisterAccount();
                        }
                    }, 2000);
                }
            }
        });
    }






    public void RegisterAccount()
    {
        Integer response=Integer.valueOf(db.GetRegistrationResponse());
        if(response==400)
        {
            ExistingAccount(snck);
        }
        else
        {
            if(response==200)
            {
                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        StartSignInActivity();
                    }
                }, 1000);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No Connectivity!", Toast.LENGTH_LONG).show();
            }
        }

    }


    public Boolean Validate(View v)
    {
        if(tb_name.getText().toString().length()<3)
        {
ShowSnackbar(v,"Min Length of Name must be 3");
            return  false;
        }
        if(tb_contact.getText().toString().length()!=11)
        {
            ShowSnackbar(v,"Invalid Contact No.");
            return false;
        }
        if(tb_password.getText().toString().length()<8)
        {
            ShowSnackbar(v,"Min Length of Password must be 8");
            return false;
        }
        return true;
    }


    public void ShowSnackbar(View view,String message)
    {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }


    public void ExistingAccount(View view)
    {
       ShowSnackbar(view,"Account already exist!\nTry Sign in instead.");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                StartSignInActivity();
            }
        }, 2000);
    }

    protected  void StartSignInActivity()
    {
        activity_intent = new Intent(this,   SignInActivity.class);
        startActivity(activity_intent);
    }
}
