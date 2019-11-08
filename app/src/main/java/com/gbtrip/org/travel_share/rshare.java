package com.gbtrip.org.travel_share;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class rshare extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        ResultCallback<Status> {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private Marker currentLocationMarker;
    public LatLng latLng, latLng1;
    private Marker pickMarker,pickupmarker;
    Double checklat, checklon;
    private FirebaseAuth mAuth;
	private TextView mCustomerName, mCustomerPhone;
    private String customerId = "";
    Marker mCurrLocationMarker;
    Button btn;

    Intent intent;

    public static final int REQUEST_LOCATION_CODE = 99;
    private static final int[] COLORS = new int[]{R.color.design_default_color_primary};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_rshare, null);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intent = new Intent(getActivity(), condition.class);
        mAuth = FirebaseAuth.getInstance();
		
		//mCustomerName = (TextView) findViewById(R.id.customerName);
        //mCustomerPhone = (TextView) findViewById(R.id.customerPhone);

        btn = (Button) view.findViewById(R.id.sharebtn);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        getAssignedCustomer();
        return view;
    }

    private void getAssignedCustomer() {
        String driverId = mAuth.getCurrentUser().getUid();
        DatabaseReference assignref = FirebaseDatabase.getInstance().getReference().child(driverId).child("customerId");
        assignref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        customerId = dataSnapshot.getValue().toString();
                        getAssignedCustomerpickuplocation();
						getAssignedCustomerInfo();
                    }
					else{
                    endRide();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
	   private DatabaseReference assignedcustomerpicklocref;
    private ValueEventListener assignedCustomerPickupLocationRefListener;

    private void getAssignedCustomerpickuplocation() {
        String driverId = mAuth.getCurrentUser().getUid();
        assignedcustomerpicklocref = FirebaseDatabase.getInstance().getReference().child("accept").child(customerId).child("l");
       assignedCustomerPickupLocationRefListener = assignedcustomerpicklocref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationlat = 0;
                    double locationlong = 0;
                    btn.setText("Rider Found....");
                    if (map.get(0) != null) {
                        locationlat = Double.parseDouble(map.get(0).toString());
                    }

                    if (map.get(1) != null) {
                        locationlong = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverlatlng = new LatLng(locationlat, locationlong);

                    pickupmarker=mMap.addMarker(new MarkerOptions().position(driverlatlng).title("Pickup Location"));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } private void getAssignedCustomerInfo(){

        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        mCustomerName.setText(map.get("name").toString());
                    }
                    if(map.get("phone")!=null){
                        mCustomerPhone.setText(map.get("phone").toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void endRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        customerId="";

        if(pickupmarker != null){
            pickupmarker.remove();
        }
        if (assignedCustomerPickupLocationRefListener != null){
            assignedcustomerpicklocref.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        mCustomerName.setText("");
        mCustomerPhone.setText("");
    }

	
	

    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    
    protected synchronized void buildGoogleApiClient() {

        client = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        } else {
            return false;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (getContext() != null) {

            lastLocation = location;
            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            checklat = latLng.latitude;
            checklon = latLng.longitude;
            latLng1 = new LatLng(checklat, checklon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng1);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            String userid = mAuth.getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("Available");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("Working");

            GeoFire geoFire = new GeoFire(refAvailable);
            GeoFire geoFire1 = new GeoFire(refWorking);
            switch(customerId){
                case "":

                    geoFire1.removeLocation(userid, new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                Toast.makeText(getContext(), "There was an error saving the location to GeoFire: " + error, Toast.LENGTH_LONG).show();
                            } else {

                            }

                        }
                    });
                    geoFire.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                Toast.makeText(getContext(), "There was an error saving the location to GeoFire: " + error, Toast.LENGTH_LONG).show();
                            } else {

                            }
                        }
                    });
                    break;

                    default:

                        geoFire.removeLocation(userid, new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    Toast.makeText(getContext(), "There was an error saving the location to GeoFire: " + error, Toast.LENGTH_LONG).show();
                                } else {

                                }

                            }
                        });

                        geoFire1.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (error != null) {
                                    Toast.makeText(getContext(), "There was an error saving the location to GeoFire: " + error, Toast.LENGTH_LONG).show();
                                } else {

                                }
                            }
                        });
                        break;
            }
    }

}

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onStop() {
        super.onStop();
        String userid = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Available");
        GeoFire geoFire = new GeoFire(ref);
        ref.child(userid).removeValue();
   /*     geoFire.removeLocation(userid, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(getContext(), "There was an error saving the location to GeoFire: " + error, Toast.LENGTH_LONG).show();
                } else {

                }
            }
        });
    }*/
    }
}