package cf.quanganh.musicplayer.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import cf.quanganh.musicplayer.CustomAdapter;
import cf.quanganh.musicplayer.DBHelper;
import cf.quanganh.musicplayer.MainActivity;
import cf.quanganh.musicplayer.OnSwipeTouchListener;
import cf.quanganh.musicplayer.R;
import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;


public class HistoryFragment extends Fragment {

    ListView listView;
    static CustomAdapter adapter;
    static ArrayList<String> songNameList;

    public HistoryFragment(){
    }

    @SuppressLint("ValidFragment")
    public HistoryFragment(ArrayList<String> list) {
        songNameList = list;
    }

    public static HistoryFragment newInstance(ArrayList<String> list) {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        // Inflate the layout for this fragment
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Set Back in Action Bar
        MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
        setHasOptionsMenu(true);

        RelativeLayout ln = (RelativeLayout) view.findViewById(R.id.historyfragment);
        ln.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft(){
               getActivity().onBackPressed();
            }
        });

        songNameList =  AudioPlayer.getPlayedList();
        Collections.reverse(songNameList);

        listView = (ListView) view.findViewById(R.id.listViewHistory);
        if (songNameList.isEmpty()){
            setCustomListView(null);
        } else {
            AudioPlayer.playedSongList = getSongList(songNameList);
            setCustomListView(AudioPlayer.playedSongList);
        }
        return view;
    }

    void setCustomListView(final ArrayList<Song> songList){
        if (songList == null) return;
        HashMap<String, Boolean> headerArray = new HashMap<String, Boolean>();
        for(int i=0;i< songList.size();i++) {
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

    ArrayList<Song> getSongList(ArrayList<String> songNameList){
        ArrayList<Song> result = new ArrayList<Song>();
        for (String songName : songNameList){
            result.add(new DBHelper(getActivity()).getSongByName(songName));
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("Select smt");
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
