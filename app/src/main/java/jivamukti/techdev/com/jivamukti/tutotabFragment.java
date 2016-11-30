package jivamukti.techdev.com.jivamukti;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class tutotabFragment extends Fragment {
    ImageView imagen;
    public tutotabFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.tuto, container, false);
        imagen = (ImageView)  v.findViewById(R.id.imgtuto);
        imagen.setBackgroundResource(R.drawable.tuto1);
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
