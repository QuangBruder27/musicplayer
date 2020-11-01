package cf.quanganh.musicplayer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import cf.quanganh.musicplayer.MainActivity;
import cf.quanganh.musicplayer.OnSwipeTouchListener;
import cf.quanganh.musicplayer.R;


public class AboutFragment extends Fragment {

    Button btn_pp;

    public AboutFragment() {
    }


    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
        setHasOptionsMenu(true);

        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.about_layout);
        rl.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft(){
                getActivity().onBackPressed();
            }
        });



        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
