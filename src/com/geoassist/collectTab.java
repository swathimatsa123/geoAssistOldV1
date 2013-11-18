package com.geoassist;

import android.app.FragmentManager;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
//import android.widget.TextView;

public class collectTab extends Fragment {

	private SupportMapFragment fragment;
	private GoogleMap map;
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
	}

	@Override
	public void onResume() {
	super.onResume();
	if (map == null) {
	    map = fragment.getMap();
	    Toast.makeText(getActivity(), "GOT MAP", Toast.LENGTH_LONG).show();
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
	}
}
}
