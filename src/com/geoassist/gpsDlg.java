package com.geoassist;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class gpsDlg extends DialogFragment implements OnEditorActionListener {
//public class ipDlg {
	EditText mLatTxt;
	EditText mLngTxt;
	public collectTab frg;
	public gpsDlg(){
	}
	public interface gpsDlgListener {
		void onGpsGetDialog(String lat, String lng) ;    
	}


   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gps_editdlg, container);
        getDialog().setTitle("Change Coordinate");

        mLatTxt = (EditText) view.findViewById(R.id.latTxt);
        mLatTxt.setOnEditorActionListener(this);
        mLngTxt = (EditText) view.findViewById(R.id.lngTxt);
        mLngTxt.setOnEditorActionListener(this);
        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        

        return view;
    }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	     if (EditorInfo.IME_ACTION_DONE == actionId) {
	    	    frg.onGpsGetDialog(mLatTxt.getText().toString(), mLngTxt.getText().toString());
	            this.dismiss();
	            return true;
	        }
	        return false;
	}

}
