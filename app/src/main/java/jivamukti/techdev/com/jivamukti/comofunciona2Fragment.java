package jivamukti.techdev.com.jivamukti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class comofunciona2Fragment extends Fragment {
    ImageView imagen;
    public comofunciona2Fragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.tuto, container, false);
        imagen = (ImageView)  v.findViewById(R.id.imgtuto);
        imagen.setBackgroundResource(R.drawable.tour2);
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
