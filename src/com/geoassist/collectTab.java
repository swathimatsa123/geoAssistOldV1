package com.geoassist;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

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
import android.widget.Toast;

public class collectTab extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
                                                    GooglePlayServicesClient.OnConnectionFailedListener,
                                                    LocationListener {

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
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.collect_tab,container, false);
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
	    if ( myCxt != null )
	    	Toast.makeText(myCxt, "GOT MAP "+String.valueOf(currentLat) + " "+ String.valueOf(currentLng), Toast.LENGTH_LONG).show();
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
		if (((prevLat != 0) && (prevLng != 0) && (currentLat != 0) &&(currentLng != 0)) &&
			 (prevLat != currentLat) && (prevLng != currentLng) )
		{	
			myPath = map.addPolyline(new PolylineOptions()
								.add(new LatLng(prevLat, prevLng), new LatLng(currentLat, currentLng))
								.width(5)
								.color(Color.BLUE));
			myPath.setVisible(true);
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
        Toast.makeText(myCxt, "Connected", Toast.LENGTH_SHORT).show();
        // If already requested, start periodic updates
        locationclient.requestLocationUpdates(mLocationRequest, (com.google.android.gms.location.LocationListener) this);

	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
