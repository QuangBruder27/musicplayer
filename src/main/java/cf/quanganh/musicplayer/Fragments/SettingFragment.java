package cf.quanganh.musicplayer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Locale;

import cf.quanganh.musicplayer.Helper;
import cf.quanganh.musicplayer.MainActivity;
import cf.quanganh.musicplayer.OnSwipeTouchListener;
import cf.quanganh.musicplayer.R;

import static cf.quanganh.musicplayer.Helper.changeLanguage;


public class SettingFragment extends Fragment {

    Button btn_pp, btnabout, btnlang;

    public SettingFragment() {
    }


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
        setHasOptionsMenu(true);

        /*
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.about_layout);
        rl.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft(){
                getActivity().onBackPressed();
            }
        });


         */

        btnabout = (Button) view.findViewById(R.id.st_about);

        btnabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAboutFragment();
            }
        });

        btn_pp = (Button) view.findViewById(R.id.st_pp);
        btn_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.freeprivacypolicy.com/privacy/view/d097a16938c6e4b75b20e2c38bc90301"));
                startActivity(browserIntent);
            }
        });

        btnlang = (Button) view.findViewById(R.id.st_lang);
        btnlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lang;
                Locale current = getResources().getConfiguration().locale;
                if(current.toString().charAt(0) == 'e'){
                    lang = "vi";
                } else {
                    lang = "en";
                }
                changeLanguage(getContext(), lang);
                setLanguageinSP(lang);
                // Recreate Main Activity
                getActivity().finish();
                startActivity(getActivity().getIntent());
                getActivity().overridePendingTransition(0, 0);
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

    void gotoAboutFragment(){
        Fragment fragment = AboutFragment.newInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }


    void setLanguageinSP(String lan){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Lang", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Lang", lan).commit();
    }

}
