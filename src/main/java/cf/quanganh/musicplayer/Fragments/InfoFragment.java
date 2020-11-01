package cf.quanganh.musicplayer.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cf.quanganh.musicplayer.DBHelper;
import cf.quanganh.musicplayer.MainActivity;
import cf.quanganh.musicplayer.OnSwipeTouchListener;
import cf.quanganh.musicplayer.R;
import cf.quanganh.musicplayer.objects.Song;


public class InfoFragment extends Fragment {

    ImageView imageView;
    TextView name, composer, duration;
    static String songName = "";

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance(String name) {
        InfoFragment fragment = new InfoFragment();
        songName = name;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        MainActivity.drawerToggle.setDrawerIndicatorEnabled(false);
        setHasOptionsMenu(true);

        LinearLayout ln = (LinearLayout) view.findViewById(R.id.infofragment);
        ln.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft(){
                //gotoListViewFragment();
                getActivity().onBackPressed();
            }
        });

        if (songName.equals("")) songName = "Co gi ma voi";

        imageView = (ImageView) view.findViewById(R.id.imageView);
        name = (TextView)view.findViewById(R.id.songName);
        composer = (TextView) view.findViewById(R.id.composer);
        duration = (TextView) view.findViewById(R.id.duration);

        Song song = new DBHelper(getActivity()).getSongByName(songName);

        //imageView.setImageBitmap(MainActivity.StringToBitMap(song.getImg()));
        Picasso.get().load(Uri.parse(song.getImg())).into(imageView);
        name.setText(song.getName());
        composer.setText(song.getComposer());
        duration.setText(song.getDuration());

        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
