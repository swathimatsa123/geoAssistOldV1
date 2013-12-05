package com.geoassist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import com.geoassist.gpsDlg.gpsDlgListener;
//import android.location.LocationListener;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.graphics.PorterDuff;

public class collectTab extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
                                                    GooglePlayServicesClient.OnConnectionFailedListener,
                                                    LocationListener,
                                                    OnClickListener,
                                                    OnItemSelectedListener,
                                                    gpsDlgListener{

	private SupportMapFragment fragment;
	private GoogleMap map;
    private double currentLat = 0;
    private double currentLng = 0;
    private double prevLat = 0;
    private double prevLng = 0;
    private LatLng myPos =new LatLng(currentLat,currentLng);
    private Marker myPosMarker;
    LocationRequest mLocationRequest;
    private LocationClient locationclient;
    private Context  myCxt;
    private Polyline myPath = null;
    
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL =MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private View rootView = null;
    private Button gpsEditBtn = null;
    private Button picBtn = null;
    private Spinner mapTypeSpn = null;
    private	Boolean ignoreGps = false; 
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.collect_tab,container, false);
		return rootView;
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		android.support.v4.app.FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.mapView);
		if (fragment == null) {
		    fragment = SupportMapFragment.newInstance();
		    fm.beginTransaction().replace(R.id.mapView, fragment).commit();
		}
		map = fragment.getMap();
		
// Enabling MyLocation Layer of Google Map
//		map.setMyLocationEnabled(true);				
		myCxt = getActivity();

		int resp =GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if(resp == ConnectionResult.SUCCESS){
			locationclient = new LocationClient(myCxt,this, this);
			locationclient.connect();
		}
		else{
			Toast.makeText(myCxt, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
		}				
		 mLocationRequest = LocationRequest.create();
	     mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	     mLocationRequest.setInterval(UPDATE_INTERVAL);
	     mLocationRequest.setFastestInterval(FASTEST_INTERVAL);	
	     this.gpsEditBtn = (Button)rootView.findViewById(R.id.lcEdit);
	     this.gpsEditBtn.setOnClickListener( this);			
	     this.gpsEditBtn.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);
	     
	     this.picBtn = (Button)rootView.findViewById(R.id.picBtn);
	     this.picBtn.setOnClickListener( this);			
	     this.picBtn.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);
	     
//	     this.mapTypeSpn = (Spinner)rootView.findViewById(R.id.mapType);
//	     this.mapTypeSpn.setOnClickListener( this);			
//	     this.mapTypeSpn.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);
//	  // Create an ArrayAdapter using the string array and a default spinner layout
//	     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//	             R.array.planets_array, android.R.layout.simple_spinner_item);
//	     // Specify the layout to use when the list of choices appears
//	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	     // Apply the adapter to the spinner
//	     spinner.setAdapter(adapter);    
	     this.mapTypeSpn =  (Spinner) rootView.findViewById(R.id.mapType);
	     this.mapTypeSpn.setOnItemSelectedListener(this);
	     List<String> list = new ArrayList<String>();
	 	 list.add("Road");
	 	 list.add("Sattelite");
	 	 list.add("Hybrid");
	 	 list.add("Terrain");
	 	 ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
	 	 dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 	 this.mapTypeSpn.setAdapter(dataAdapter);
	 	 this.mapTypeSpn.setBackgroundColor(Color.RED);
	}

	@Override
	public void onResume() {
		super.onResume();
	}


	@Override
	public void onLocationChanged(Location location) {
		prevLat = currentLat;
		prevLng = currentLng;
		currentLat = location.getLatitude();
		currentLng = location.getLongitude();
	    myPos = new LatLng(currentLat,currentLng);
	    if (ignoreGps != true){
		    if (map == null) {
			    map = fragment.getMap();
		        myPosMarker = map.addMarker(new MarkerOptions().position(myPos).title("Your position"));
		        myPosMarker.setDraggable(true);
		        myPosMarker.showInfoWindow();
		        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));
			}
			else{
				myPosMarker.setPosition(myPos);
			}
			if (((prevLat != 0) && 
				 (prevLng != 0) && 
				 (currentLat != 0) &&
				 (currentLng != 0)) &&
				 (prevLat != currentLat) && 
				 (prevLng != currentLng) )
			{	
				myPath = map.addPolyline(new PolylineOptions()
									.add(new LatLng(prevLat, prevLng), new LatLng(currentLat, currentLng))
									.width(5)
									.color(Color.BLUE));
				myPath.setVisible(true);
			}
	    }
	}


	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
//        Toast.makeText(myCxt, "Connected", Toast.LENGTH_SHORT).show();
        // If already requested, start periodic updates
        locationclient.requestLocationUpdates(mLocationRequest, (com.google.android.gms.location.LocationListener) this);

	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
        case R.id.lcEdit:
//        	Toast.makeText(myCxt, "GPS EditBtn clicked", Toast.LENGTH_LONG).show();
        	showGpsDialog();
        	break;
        case R.id.mapType:
        	Toast.makeText(myCxt, "Map Type Btn clicked", Toast.LENGTH_LONG).show();
        	break;
        case R.id.picBtn:
//        	MainActivity  ma = (MainActivity)getActivity();
        	Toast.makeText(myCxt, "Pic Btn clicked", Toast.LENGTH_LONG).show();
//        	ma.invokeCamera();
            break;
		}
		
	}
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		// Spinner code here
		int mapType = 0;
		Toast.makeText(parent.getContext(), 
				"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString()+ " "+String.valueOf(id),
				Toast.LENGTH_SHORT).show();
		if (map != null) {
			switch ((int)id){
			case 0:
				mapType = GoogleMap.MAP_TYPE_NORMAL;
				break;
			case 1:
				mapType = GoogleMap.MAP_TYPE_SATELLITE;
				break;			
			case 2:
				mapType = GoogleMap.MAP_TYPE_HYBRID;
				break;			
			case 3:
				mapType = GoogleMap.MAP_TYPE_TERRAIN;
				break;			
			}
			map.setMapType(mapType);
		}

	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
    private void showGpsDialog() {
        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
        gpsDlg gpsDialog = new gpsDlg();
        gpsDialog.frg = this;
        gpsDialog.show(fm, "fragment_edit_name");
    }

    @Override
    public void onGpsGetDialog(String lat, String lng) {
    	ignoreGps =true;
    	myPos =new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
    	myPosMarker.remove();
    	if (map == null) {
		    map = fragment.getMap();
    	}
	    myPosMarker = map.addMarker(new MarkerOptions().position(myPos).title("Your position"));
	    myPosMarker.setDraggable(true);
	    myPosMarker.showInfoWindow();
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));
//		}
//		else{
//			LatLng currentLoc  = myPosMarker.getPosition();
//			myPosMarker.setPosition(myPos);
//			LatLng newLoc  = myPosMarker.getPosition();
//			
//			Toast.makeText(getActivity(), "Old GPS Loc: " + currentLoc.toString()+" NewLoc : "+ newLoc.toString(), Toast.LENGTH_SHORT).show();
//	    	
//		}

    }
}
