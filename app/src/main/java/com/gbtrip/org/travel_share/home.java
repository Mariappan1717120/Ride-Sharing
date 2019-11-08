package com.gbtrip.org.travel_share;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class home extends Fragment {
    private Button btn,btn1;
    Intent intent1,intent2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        intent1=new Intent(getContext(),rshare.class);
        intent2=new Intent(getContext(),raccept.class);

        btn=(Button)view.findViewById(R.id.homeshare);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               // startActivity(intent1);
                Fragment fragment = new sharecondition();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        btn1=(Button)view.findViewById(R.id.homeaccept);
        btn1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
          //      startActivity(intent2);

                Fragment fragment = new acceptcondition();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

}
