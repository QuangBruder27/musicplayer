package cf.quanganh.musicplayer.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cf.quanganh.musicplayer.ExpandableListView.CustomExpandableListAdapter;
import cf.quanganh.musicplayer.ExpandableListView.ExpandableListDataPump;
import cf.quanganh.musicplayer.MainActivity;
import cf.quanganh.musicplayer.OnSwipeTouchListener;
import cf.quanganh.musicplayer.R;
import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;


public class SingerFragment extends Fragment {


    HashMap<String, Boolean> headerArray = new HashMap<String, Boolean>();
    HashMap<String, Boolean> defaultHeaderArray = new HashMap<String, Boolean>();

    public SingerFragment(){
    }

    public static SingerFragment newInstance() {
        SingerFragment fragment = new SingerFragment();
        return fragment;
    }


    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, ArrayList<Song>> expandableListDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singer, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Set Back in Action Bar
        MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
        setHasOptionsMenu(true);

        RelativeLayout ln = (RelativeLayout) view.findViewById(R.id.singerFragment);
        ln.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft(){
                System.out.println("swipe right");
                getActivity().onBackPressed();
            }
        });

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getHashMap(AudioPlayer.originalList);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        for(int i=0; i < expandableListAdapter.getGroupCount(); i++)
            expandableListView.expandGroup(i);

        /*
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });
         */

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getContext(), expandableListTitle.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                ListViewFragment.gotoPlayFragmentWithNewSong(expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition).getName(),getActivity());

             return false;
            }
        });


        return view;
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singer, container, false);
        // Inflate the layout for this fragment


        listView = (ListView) view.findViewById(R.id.listViewSinger);

        sortByArtist(AudioPlayer.originalList);

        if (songListbyArtist.isEmpty()){
            setCustomListView(null);
        } else {
            setCustomListView(songListbyArtist);
        }


        return view;
    }

    void setCustomListView(final ArrayList<Song> songList){
        if (songList == null) return;

        for(int i=0;i< songListbyArtist.size();i++) {
            String nameofArtist = songListbyArtist.get(i).getComposer();
            headerArray.put(nameofArtist, false);
            //System.out.println("char ="+c+", true");
        }
        setHashmapforHeader(songListbyArtist, artistList);

        adapter= new CustomAdapter(songList, headerArray,getActivity(), true, true);

        if (adapter.isEmpty()) {
            System.out.println("Adapter is empty");
            return;
        }
        listView.setAdapter(adapter);
    }

     */


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


    void setHashmapforHeader(List<Song> songList, List<String> artistList){
        System.out.println("Set Hashmap for Header");
        for (String artist: artistList){
            defaultHeaderArray.put(artist,false);
        }
        for(int i=0;i< songList.size();i++) {
            String nameofArtist = songList.get(i).getComposer();
            String nameofSong = songList.get(i).getName();

            if(defaultHeaderArray.get(nameofArtist)){
                headerArray.put(nameofSong, false);
            } else {
                defaultHeaderArray.put(nameofArtist,true);
                headerArray.put(nameofSong, true);
            }
        }
    }




}
