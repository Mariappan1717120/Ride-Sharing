package com.gbtrip.org.travel_share;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget extends AppCompatActivity {


    EditText foremailtxt;
    Button btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        mAuth=FirebaseAuth.getInstance();
        foremailtxt=(EditText)findViewById(R.id.foremailtxt);
        btn=(Button)findViewById(R.id.resetbtn);

    }
    public void onreset(View view){
        String useremail = foremailtxt.getText().toString().trim();

        if(useremail.equals("")){
            Toast.makeText(Forget.this, "Please Enter Email-Id", Toast.LENGTH_SHORT).show();

        }else
        {
            mAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(Forget.this, "Reset Password email sent", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(Forget.this,Third.class));
                    }
                    else{
                        Toast.makeText(Forget.this, "Error in Sending Reset Email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}
