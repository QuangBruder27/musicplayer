package cf.quanganh.musicplayer.ExpandableListView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cf.quanganh.musicplayer.R;
import cf.quanganh.musicplayer.objects.Song;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, ArrayList<Song>> expandableListDetail;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, ArrayList<Song>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Song expandedListitem = (Song) getChild(listPosition, expandedListPosition);
        System.out.println("listPosition "+listPosition+" expandedListText"+expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        ImageView expandedListImageOfSong = (ImageView) convertView.findViewById(R.id.explist_imgofSong);
        Picasso.get().load(Uri.parse(expandedListitem.getImg())).into(expandedListImageOfSong);

        TextView expandedListNameofSong = (TextView) convertView.findViewById(R.id.explist_nameofSong);
        expandedListNameofSong.setText(expandedListitem.getName());

        TextView expandedListDurationOfSong = (TextView) convertView.findViewById(R.id.explist_durationofSong);
        expandedListDurationOfSong.setText(expandedListitem.getDuration());

        TextView expandedListSizeOfSong = (TextView) convertView.findViewById(R.id.explist_sizeofSong);
        expandedListSizeOfSong.setText(android.text.format.Formatter.formatFileSize(this.context, Long.parseLong(expandedListitem.getSize())));

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        // Title
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTextColor(Color.parseColor("#ff669900"));
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setTextSize(20);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}