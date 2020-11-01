package cf.quanganh.musicplayer.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cf.quanganh.musicplayer.CustomAdapter;
import cf.quanganh.musicplayer.MainActivity;
import cf.quanganh.musicplayer.R;
import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;


public class PlaylistDialogFragment extends DialogFragment {



    ListView listView;
    static CustomAdapter adapter;
    Button btnClose;
    TextView tvPlaylist;

    public PlaylistDialogFragment() {
    }

    public static PlaylistDialogFragment newInstance(){
        PlaylistDialogFragment f = new PlaylistDialogFragment();
        return f;
    }

    // TODO: Rename and change types and number of parameters
    public static PlaylistDialogFragment newInstance(String param1, String param2) {
        PlaylistDialogFragment fragment = new PlaylistDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_dialog, container, false);

        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Set Back in Action Bar
        //MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
        //setHasOptionsMenu(true);
        RelativeLayout ln = (RelativeLayout) view.findViewById(R.id.playlistDialogFragment);

        tvPlaylist = (TextView) view.findViewById(R.id.tvPlaylist);

        String strPlaylist = getResources().getString(R.string.Playlist);
        tvPlaylist.setText(strPlaylist+"("+AudioPlayer.playList.size()+")");

        btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        listView = (ListView) view.findViewById(R.id.listViewPlaylistDialog);
        if (AudioPlayer.playList.isEmpty()){
            setCustomListView(null);
        } else {
            setCustomListView(AudioPlayer.playList);
        }
        return view;
    }

    void setCustomListView(final ArrayList<Song> songList){
        System.out.println("SetCustom in DialogFragment");
        if (songList == null) return;
        HashMap<String, Boolean> headerArray = new HashMap<String, Boolean>();
        for(int i=0;i< songList.size();i++) {
            String nameofSong = songList.get(i).getName();
            headerArray.put(nameofSong, false);
        }
        adapter= new CustomAdapter(songList, headerArray,getActivity(),false, false);
        if (adapter.isEmpty()) {
            System.out.println("Adapter is empty");
            return;
        }
        listView.setAdapter(adapter);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        int stageWidth =  getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int stageHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        System.out.println("Screen Width:"+stageWidth+" x "+stageHeight);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (stageHeight*0.65));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        return dialog;
    }

}
