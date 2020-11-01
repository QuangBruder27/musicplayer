package cf.quanganh.musicplayer.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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


public class SearchResultFragment extends Fragment {
    ListView listView;
    static CustomAdapter adapter;
    static ArrayList<String> songNameList;

    public SearchResultFragment(){
    }

    @SuppressLint("ValidFragment")
    public SearchResultFragment(ArrayList<String> list) {
        songNameList = list;
    }

    public static SearchResultFragment newInstance(ArrayList<String> list) {
        SearchResultFragment fragment = new SearchResultFragment(list);
        //songNameList = list;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        // Inflate the layout for this fragment

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ListViewFragment.searchView.clearFocus();

        // Set Back in Action Bar
        MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
        setHasOptionsMenu(true);

        RelativeLayout ln = (RelativeLayout) view.findViewById(R.id.searchresultfragment);
        ln.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft(){
                getActivity().onBackPressed();
            }
        });

        listView = (ListView) view.findViewById(R.id.listViewSearchResult);
        if (songNameList.isEmpty()){
            setCustomListView(null);
        } else {
            setCustomListView(getSongList(songNameList));
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
