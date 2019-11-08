package com.gbtrip.org.travel_share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class acceptcondition extends Fragment implements AdapterView.OnItemSelectedListener {
    private Button btn;
    EditText nametxt1,agetxt1;
    String genders,name,age;
    private FirebaseAuth mAuth;
    //    Intent intent1,intent2;
    String[] gender={"MALE","FEMALE"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_acceptcondition, container, false);
        //      intent1=new Intent(getContext(),rshare.class);
        //    intent2=new Intent(getContext(),raccept.class);
        nametxt1 =(EditText)view.findViewById(R.id.nametxt1);
        agetxt1 =(EditText)view.findViewById(R.id.agetxt1);

        mAuth = FirebaseAuth.getInstance();
        Spinner spin = (Spinner)view.findViewById(R.id.spinnergen1);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa=new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        btn=(Button)view.findViewById(R.id.sharecon1);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // startActivity(intent1);
                name = nametxt1.getText().toString().trim();
                age = agetxt1.getText().toString().trim();
                if (TextUtils.isEmpty(nametxt1.getText().toString().trim()) && TextUtils.isEmpty(agetxt1.getText().toString().trim())) {
                    nametxt1.setError("Required");
                    agetxt1.setError("Required");
                } else if (TextUtils.isEmpty(nametxt1.getText().toString().trim())) {
                    nametxt1.setError("Enter Username");
                } else if (TextUtils.isEmpty(agetxt1.getText().toString().trim())) {
                    agetxt1.setError("Required");
                } else {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference mregister = firebaseDatabase.getReference().child("acceptcondition").child(mAuth.getUid());
                    conditondata register=new conditondata(name,age,genders);
                    mregister.setValue(register);
                    Toast.makeText(getContext(), "successs pro", Toast.LENGTH_SHORT).show();

                    Fragment fragment = new raccept();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), gender[position], Toast.LENGTH_SHORT).show();
        genders=gender[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
