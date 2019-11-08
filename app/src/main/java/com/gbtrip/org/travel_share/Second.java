package com.gbtrip.org.travel_share;


import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Second extends AppCompatActivity {
    Button btn2;
    ImageView profileimg;
    EditText usertxt,txtage,txtcity,txtemail,txtphone,txtpassword;
    private ProgressDialog progressDialog;
    DatabaseReference mregister;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStore;
    private StorageReference storageReference;
    private static int PICK_IMAGE = 123;
    Uri imagepath;
    String username,userage,usercity,useremail,userphone,userpassword;
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit.",Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_second);
        profileimg=(ImageView)findViewById(R.id.profileimg);
        mAuth=FirebaseAuth.getInstance();
        Decision_tree_accuaracy dec = new Decision_tree_accuaracy();
        dec.Accuracy();
        if(mAuth.getCurrentUser()!=null){
            finish();
            Toast.makeText(this, "Already a user logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),TRAVELO.class));
        }
        mStore=FirebaseStorage.getInstance();
        storageReference=mStore.getReference();
        progressDialog=new ProgressDialog(this);
        usertxt=(EditText)findViewById(R.id.usertxt);
        txtage=(EditText)findViewById(R.id.txtage);
        txtcity=(EditText)findViewById(R.id.txtcity);
        txtemail=(EditText)findViewById(R.id.txtemail);
        txtphone=(EditText)findViewById(R.id.txtphone);
        txtpassword=(EditText)findViewById(R.id.txtpassword);
        btn2 = (Button) findViewById(R.id.registerbtn);
    }
    public void onClickregister(View v)
    {
        reg();
    }
    private void reg()
    {
        username = usertxt.getText().toString().trim();
        userage=txtage.getText().toString().trim();
        usercity=txtcity.getText().toString().trim();
        useremail=txtemail.getText().toString().trim();
        userphone=txtphone.getText().toString().trim();
        userpassword=txtpassword.getText().toString().trim();

        if(TextUtils.isEmpty(usertxt.getText().toString().trim()) && TextUtils.isEmpty(txtage.getText().toString().trim()) &&
                TextUtils.isEmpty(txtcity.getText().toString().trim()) && TextUtils.isEmpty(txtemail.getText().toString().trim())
                && TextUtils.isEmpty(txtpassword.getText().toString().trim()) && TextUtils.isEmpty(txtphone.getText().toString().trim())) {
            usertxt.setError("Required");
            txtage.setError("Required");
            txtcity.setError("Required");
            txtemail.setError("Required");
            txtphone.setError("Required");
            txtpassword.setError("Required");
            Toast.makeText(this, "Please Enter the Details", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(usertxt.getText().toString().trim())){
            usertxt.setError("Enter Username");
        }else if(TextUtils.isEmpty(txtage.getText().toString().trim())){
            txtage.setError("Required");
        } else if(!ageValidator(txtage.getText().toString())){
            txtage.setError("Age must be Between 0 to 100");
        }else if(TextUtils.isEmpty(txtcity.getText().toString().trim())){
            txtcity.setError("Required");
        }else if(TextUtils.isEmpty(txtemail.getText().toString().trim())){
            txtemail.setError("Required");
        }else if(!emailValidator(txtemail.getText().toString())){
            txtemail.setError("Please Enter Valid Email Address");
        }else if(TextUtils.isEmpty(txtphone.getText().toString().trim())){
            txtphone.setError("Required");
        }else if(!phValidator(txtphone.getText().toString())){
            txtphone.setError("Please Enter a Valid Mobile Number");
        }else if(TextUtils.isEmpty(txtpassword.getText().toString().trim()))
        {
            txtpassword.setError("Required");
        }
        else if(txtpassword.length()<8){
            txtpassword.setError("Password must be minimum 8 characters");
        }
        else{
            progressDialog.setMessage("Registering....Please Wait");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(useremail,userpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful())
                            {
                                userdata();
                                startActivity(new Intent(getApplicationContext(),TRAVELO.class));
                                Clickme();
                                Toast.makeText(Second.this, "REGISTRATION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Second.this, "Please Try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void onprofile(View v){
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select image"),PICK_IMAGE);
    }
    @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode==RESULT_OK && data.getData()!=null){
            imagepath=data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
                profileimg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void Clickme() {
        NotificationCompat.Builder builder =(NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("REGISTRATION SUCCESSFUL!!!!")
                .setContentText("Your Registration on Travelo Application is Successful");
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }
    public static boolean emailValidator(String email)
    {
        return(!TextUtils.isEmpty(email)&& Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    private static boolean phValidator(String phone){
        Pattern pattern;
        Matcher matcher;
        final String PHONE_PATTERN="^[0-9]{2}[0-9]{8}$";
        pattern=Pattern.compile(PHONE_PATTERN);
        matcher=pattern.matcher(phone);
        return matcher.matches();
    }

    private static boolean ageValidator(String age){
        Pattern pattern;
        Matcher matcher;
        final String PHONE_PATTERN="^[0-9]{2}$";
        pattern=Pattern.compile(PHONE_PATTERN);
        matcher=pattern.matcher(age);
        return matcher.matches();
    }
    private void userdata(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mregister = firebaseDatabase.getReference().child(mAuth.getUid());
        database register=new database(username,userage,usercity,useremail,userphone);
        mregister.setValue(register);
        Toast.makeText(this, "successs pro", Toast.LENGTH_SHORT).show();

        StorageReference imageReference= storageReference.child(mAuth.getUid()).child("Images").child("Profile Pictures");
        UploadTask uploadTask=imageReference.putFile(imagepath);
        uploadTask.addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Second.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Second.this, "Upload Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
