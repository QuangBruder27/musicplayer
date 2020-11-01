package cf.quanganh.musicplayer.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

import cf.quanganh.musicplayer.CustomAdapter;
import cf.quanganh.musicplayer.DBHelper;
import cf.quanganh.musicplayer.MainActivity;
import cf.quanganh.musicplayer.OnSwipeTouchListener;
import cf.quanganh.musicplayer.R;
import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;

public class MyPlaylistFragment extends Fragment {
    ListView listView;
    static CustomAdapter adapter;
    static ArrayList<String> songNameList;
    Song selectedSong;
    public ArrayList<Song> songList = AudioPlayer.myPlayList;

    public MyPlaylistFragment(){
    }

    @SuppressLint("ValidFragment")
    public MyPlaylistFragment(ArrayList<String> list) {
        songNameList = list;
    }

    public static MyPlaylistFragment newInstance(){
        MyPlaylistFragment fragment = new MyPlaylistFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_playlist, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
        setHasOptionsMenu(true);

        RelativeLayout ln = (RelativeLayout) view.findViewById(R.id.myplaylistfragment);
        ln.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft(){
                getActivity().onBackPressed();
            }
        });

        listView = (ListView) view.findViewById(R.id.listViewMyPlaylist);
        if (songList.isEmpty()){
            setCustomListView(null);
        } else {
            setCustomListView(songList);
        }
        return view;
    }

    void setCustomListView(final ArrayList<Song> songList){
        if (songList == null) return;
        HashMap<String, Boolean> headerArray = new HashMap<String, Boolean>();
        for(int i=0;i< songList.size();i++){
            String nameofSong = songList.get(i).getName();
            headerArray.put(nameofSong, false);
            //System.out.println("char ="+c+", true");
        }
        adapter= new CustomAdapter(songList, headerArray,getActivity(), true, false);
        if (adapter.isEmpty()) {
            System.out.println("Adapter is empty");
            return;
        }
        listView.setAdapter(adapter);
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

    public static void gotoPlayFragmentWithFavouriteSong(String songName, Context context){
        AudioPlayer.playList = AudioPlayer.myPlayList;
        Fragment fragment = PlayFragment.newInstance(songName, true, true, true);
        ((PlayFragment) fragment).havetoPlayNewSong = true;
        FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main, fragment).addToBackStack(null).commit();
    }


}
