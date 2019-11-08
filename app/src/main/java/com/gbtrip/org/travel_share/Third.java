package com.gbtrip.org.travel_share;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Third extends AppCompatActivity {

    private ProgressDialog progressDialog;
    Button btn;
    TextView forgettxt;
    EditText emailtxt, passwordtxt;
    String useremail,userpassword;
    private FirebaseAuth mAuth;
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    } @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null){
            finish();
            Toast.makeText(this, "Already a user logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),TRAVELO.class));
        }


        emailtxt=(EditText)findViewById(R.id.emailtxt);
        passwordtxt=(EditText)findViewById(R.id.passwordtxt);
        btn=(Button)findViewById(R.id.logbtn);
        forgettxt=(TextView)findViewById(R.id.forgettxt);
    }

    public void onClicklog(View v) {
        log();
    }

    private void log() {

         useremail=emailtxt.getText().toString().trim();
         userpassword=passwordtxt.getText().toString().trim();

        if (TextUtils.isEmpty(emailtxt.getText().toString().trim()) && TextUtils.isEmpty(passwordtxt.getText().toString().trim())) {
            emailtxt.setError("Required");
            passwordtxt.setError("Required");
        } else if (!emailValidator(emailtxt.getText().toString())) {
            emailtxt.setError("Please Enter Valid Email Address");
        }else if(TextUtils.isEmpty(passwordtxt.getText().toString().trim()))
        {
            passwordtxt.setError("Required");
        }
        else {

            mAuth.signInWithEmailAndPassword(useremail,userpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                finish();
                                startActivity(new Intent(Third.this,TRAVELO.class));
                                Toast.makeText(Third.this, "logged IN", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(Third.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    public void onforgetpassword(View v){
        Intent i =new Intent(Third.this,Forget.class);
        startActivity(i);
    }
    public static boolean emailValidator(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}
