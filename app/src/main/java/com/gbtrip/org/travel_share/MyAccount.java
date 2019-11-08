package com.gbtrip.org.travel_share;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccount extends Fragment {
    private TextView name;
    private TextView age;

    private TextView city;
    private TextView email;
    private TextView phone;
    private ImageView profile;
    private FirebaseAuth mAuth;
    private FirebaseDatabase display;
    private FirebaseStorage mStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        final ImageView profile=(ImageView)view.findViewById(R.id.dispimage);
        final TextView name=(TextView)view.findViewById(R.id.dispname);
        final TextView age=(TextView)view.findViewById(R.id.dispage);
        final TextView city=(TextView)view.findViewById(R.id.dispcity);
        final TextView email=(TextView)view.findViewById(R.id.dispemail);
        final TextView phone=(TextView)view.findViewById(R.id.dispphone);
        mAuth= FirebaseAuth.getInstance();
        mStore=FirebaseStorage.getInstance();
        StorageReference storageReference=mStore.getReference();
        storageReference.child(mAuth.getUid()).child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(profile);
            }
        });

        display=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=display.getReference().child(mAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                database data =dataSnapshot.getValue(database.class);
                name.setText(" "+data.getUsername());
                age.setText(" "+data.getUserage());
                city.setText(" "+data.getUsercity());
                email.setText(" "+data.getUseremail());
                phone.setText(" "+data.getUserphone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    public void onViewCreated(View view,Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        getActivity().setTitle("My Account");
    }

}

