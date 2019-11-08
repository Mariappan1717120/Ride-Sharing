package com.gbtrip.org.travel_share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    Button btn;
    Button btn1;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Opening Travelo...Please Wait");
        progressDialog.show();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            finish();
            progressDialog.dismiss();
            Toast.makeText(this, "Already a user logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),TRAVELO.class));
        }

        btn = (Button) findViewById(R.id.regbtn);
        btn1 = (Button) findViewById(R.id.signbtn);
        progressDialog.dismiss();
    }

    public void onClickreg(View v) {
        Intent i = new Intent(MainActivity.this, Second.class);
        startActivity(i);
    }

    public void onClicklog(View v) {
        Intent j = new Intent(MainActivity.this, Third.class);
        startActivity(j);
    }

}
