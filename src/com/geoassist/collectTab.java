package com.geoassist;

import android.app.FragmentManager;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
//import android.widget.TextView;

public class collectTab extends Fragment implements LocationListener {

	private SupportMapFragment fragment;
	private GoogleMap map;
    private double lat = 0;
    private double lng = 0;
    private LatLng myPos;
    private Marker myPosMarker;
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
//		myPos = new LatLng(0,0);
		if (fragment == null) {
		    fragment = SupportMapFragment.newInstance();
		    fm.beginTransaction().replace(R.id.mapView, fragment).commit();
		}
		map = fragment.getMap();
		
		// Enabling MyLocation Layer of Google Map
//		map.setMyLocationEnabled(true);				
				
		
		 // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
                onLocationChanged(location);
        }
	
	}

	@Override
	public void onResume() {
	super.onResume();
	if (map == null) {
	    map = fragment.getMap();
//	    Toast.makeText(getActivity(), "GOT MAP "+String.valueOf(lat) + " "+ String.valueOf(lat), Toast.LENGTH_LONG).show();
        myPosMarker = map.addMarker(new MarkerOptions().position(myPos).title("Your position"));
        myPosMarker.setDraggable(true);
        myPosMarker.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 15));
//        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null); 
	}
}


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	    lat = location.getLatitude();
	    lng = location.getLongitude();
	    myPos = new LatLng(lat,lng);
	    Toast.makeText(getActivity(), "GOT MAP "+String.valueOf(lat) + " "+ String.valueOf(lat), Toast.LENGTH_LONG).show();

		if (map != null) {
			myPosMarker.setPosition(myPos);
		}
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
