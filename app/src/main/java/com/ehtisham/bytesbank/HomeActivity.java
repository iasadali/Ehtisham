package com.ehtisham.bytesbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity
{

    Button signIn,signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        signIn=findViewById(R.id.btn_SignIn);
        signUp=findViewById(R.id.btn_SignUp);
        signIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                StartSignInActivity();
            }
        });

        signUp.setOnClickListener(
                new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                StartSignUpActivity();
            }
        });
    }


    protected void StartSignInActivity()
    {
        Intent currentIntent = new Intent(this,   SignInActivity.class);
        startActivity(currentIntent);
    }

    protected  void StartSignUpActivity()
    {
        Intent currentIntent = new Intent(this,   SignUpActivity.class);
        startActivity(currentIntent);
    }
}
