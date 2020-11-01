package cf.quanganh.musicplayer;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cf.quanganh.musicplayer.Fragments.ListViewFragment;
import cf.quanganh.musicplayer.Fragments.PlayFragment;
import cf.quanganh.musicplayer.objects.AudioPlayer;
import cf.quanganh.musicplayer.objects.Song;

public class CustomAdapterOnline extends ArrayAdapter<Track> {



    private List<Track> list;
    Context mContext;
    Application app;


    // View lookup cache
    private static class ViewHolder {
        TextView nameofSong;
        TextView nameofArtist;
        TextView durationofSong;
        ImageView imgofSong;
        LinearLayout linearLayout;
    }

    public CustomAdapterOnline(Context context, List<Track> listTr, Application app){
        super(context,R.layout.row_online_item, listTr);
        this.mContext = context;
        this.list = listTr;
        this.app = app;
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        final Track dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final CustomAdapterOnline.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;
        if (convertView == null) {
            viewHolder = new CustomAdapterOnline.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_online_item, parent, false);
            viewHolder.nameofSong = (TextView) convertView.findViewById(R.id.nameofTrack);
            viewHolder.durationofSong = (TextView) convertView.findViewById(R.id.durationofTrack);
            viewHolder.imgofSong = (ImageView) convertView.findViewById(R.id.imgofTrack);
            viewHolder.nameofArtist = (TextView) convertView.findViewById(R.id.nameofArtistTrack);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearlayout2);
            //viewHolder.
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CustomAdapterOnline.ViewHolder) convertView.getTag();
            result=convertView;
        }
            // ListView Fragment, History Fragment, Info Fragment, My PlaylistFragment
            //viewHolder.nameofArtist.setVisibility(View.GONE);
            //viewHolder.durationofSong.setText(dataModel.getDuration());


        //Picasso.get().load(Uri.parse(dataModel.getImg())).into(viewHolder.imgofSong);

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.nameofSong.setText(dataModel.getTitle());
        viewHolder.nameofArtist.setText(dataModel.getArtist().getName());
        viewHolder.durationofSong.setText(Helper.milliSecondsToTimer(dataModel.getDuration()*1000));

        Picasso.get().load(dataModel.getAlbum().getCoverUrl()).into(viewHolder.imgofSong);

        //viewHolder.imgofSong.setImageBitmap(MainActivity.StringToBitMap(dataModel.getImg()));


        viewHolder.imgofSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("imgOfSong clicked");
                try {
                    OnlineActivity.trackPlayer(dataModel, app);
                } catch (DeezerError deezerError) {
                    deezerError.printStackTrace();
                } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
                    tooManyPlayersExceptions.printStackTrace();
                }
            }
        });

        /*
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

        */

        return convertView;
    }
}
