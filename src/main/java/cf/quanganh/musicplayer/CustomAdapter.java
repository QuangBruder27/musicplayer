package cf.quanganh.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import cf.quanganh.musicplayer.Fragments.ListViewFragment;
import cf.quanganh.musicplayer.Fragments.PlayFragment;
import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;

public class CustomAdapter extends ArrayAdapter<Song>{

    private ArrayList<Song> list;
    Context mContext;
    HashMap<String, Boolean> headerArray;
    boolean havetoDisplayDetails = true;
    boolean isSingerFragment = false;


    public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
    public ArrayAdapter<String> headers;

    public void addSection(String section, Adapter adapter){
        this.headers.add(section);
        this.sections.put(section, adapter);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView nameofSong;
        TextView nameofArtist;
        TextView durationofSong;
        TextView sizeofSong;
        TextView firstLetterofSong;
        ImageView imgofSong;
        LinearLayout linearLayout;
        ImageButton btnStar;
        ImageView imgPlaying;
    }

    public CustomAdapter(ArrayList<Song> data,HashMap<String, Boolean> headerArray, Context context, boolean HavetoDisplay, boolean IsSingerFragment) {
        super(context, R.layout.row_item, data);
        this.list = data;
        this.mContext=context;
        this.headerArray= headerArray;
        this.havetoDisplayDetails = HavetoDisplay;
        this.isSingerFragment = IsSingerFragment;
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final Song dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.nameofSong = (TextView) convertView.findViewById(R.id.nameofSong);
            viewHolder.durationofSong = (TextView) convertView.findViewById(R.id.durationofSong);
            viewHolder.sizeofSong = (TextView) convertView.findViewById(R.id.sizeofSong);
            viewHolder.imgofSong = (ImageView) convertView.findViewById(R.id.imgofSong);
            viewHolder.firstLetterofSong = (TextView) convertView.findViewById(R.id.firstLetterofSong);
            viewHolder.nameofArtist = (TextView) convertView.findViewById(R.id.nameofArtist);

            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearlayout1);
            viewHolder.btnStar = (ImageButton) convertView.findViewById(R.id.imgbtn_star);
            viewHolder.imgPlaying = (ImageView) convertView.findViewById(R.id.imgviewplaying);

            //viewHolder.
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.imgPlaying.setVisibility(View.GONE);

        if(havetoDisplayDetails) {
            // ListView Fragment, History Fragment, Info Fragment, My PlaylistFragment
            viewHolder.nameofArtist.setVisibility(View.GONE);
            viewHolder.durationofSong.setText(dataModel.getDuration());
            String s = android.text.format.Formatter.formatFileSize(this.mContext, Long.parseLong(dataModel.getSize()));
            viewHolder.sizeofSong.setText(s);
            if(dataModel.isLiked()){
                viewHolder.btnStar.setImageResource(R.drawable.icon_star_selected);
            } else {
                viewHolder.btnStar.setImageResource(R.drawable.icon_star);
            }

        } else {
            // Dialog Fragment
            viewHolder.nameofArtist.setText(dataModel.getComposer());
            viewHolder.durationofSong.setVisibility(View.GONE);
            viewHolder.sizeofSong.setVisibility(View.GONE);

            //System.out.println(dataModel.toString()+"\n *** \n"+AudioPlayer.song.toString());
            if (dataModel.isEqual(AudioPlayer.song)){
                viewHolder.imgPlaying.setVisibility(View.VISIBLE);
            }
            viewHolder.btnStar.setVisibility(View.INVISIBLE);
        }

        Picasso.get().load(Uri.parse(dataModel.getImg())).into(viewHolder.imgofSong);

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.nameofSong.setText(dataModel.getName());
        //viewHolder.imgofSong.setImageBitmap(MainActivity.StringToBitMap(dataModel.getImg()));

        setHeader(dataModel.getName(),dataModel.getComposer(),viewHolder.firstLetterofSong);

        viewHolder.imgofSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("imgOfSong clicked");
                ListViewFragment.listView.performItemClick(view,position,position);
                if(PlayFragment.playlistDialogFragment != null){
                    PlayFragment.playlistDialogFragment.dismiss();
                }
            }
        });

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("LinearLayout clicked");
                ListViewFragment.listView.performItemClick(view,position,position);
                if(PlayFragment.playlistDialogFragment != null){
                    PlayFragment.playlistDialogFragment.dismiss();
                }
            }
        });

        viewHolder.imgofSong.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                System.out.println("imgOfSong onLong clicked");
                ListViewFragment.selectedSong = ListViewFragment.songList.get(position);
                ListViewFragment.showMenu(view, view.getContext());
                return true;
            }
        });

        viewHolder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onLongClick(View view) {
                System.out.println("LinearLayout onLong clicked");
                ListViewFragment.selectedSong = ListViewFragment.songList.get(position);
                ListViewFragment.showMenu(view, view.getContext());
                return true;
            }
        });

        viewHolder.btnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataModel.isLiked()){
                    viewHolder.btnStar.setImageResource(R.drawable.icon_star);
                    dataModel.setLiked(false);
                } else {
                    viewHolder.btnStar.setImageResource(R.drawable.icon_star_selected);
                    dataModel.setLiked(true);
                }
                ListViewFragment.setFavoriteSong(dataModel);
            }
        });
        return convertView;
    }

    char c;
    void setHeader(String songName,String artistName, TextView tv) {
        if(!headerArray.get(songName)){
            tv.setVisibility(View.GONE);
        } else if(!isSingerFragment) {
            c = songName.toUpperCase().charAt(0);
            tv.setText(String.valueOf(c));
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setText(artistName);
            tv.setVisibility(View.VISIBLE);
        }
    }

}