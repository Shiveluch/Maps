package com.example.maps.classes;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maps.R;

import java.util.ArrayList;

public class ExpandAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Division> division;
    private LayoutInflater inflater;

    public ExpandAdapter(Context context,ArrayList<Division> division)
    {
        this.context=context;
        this.division=division;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return division.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return division.get(groupPosition).teams.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            convertView=inflater.inflate(R.layout.sidecom_div,null);
        }
        Division div=(Division)getGroup(groupPosition);
        TextView div_name=convertView.findViewById(R.id.sidecom_divname);
        ImageView chevron=convertView.findViewById(R.id.div_chevron);
        String side_name=div.div_name;
        div_name.setText(side_name);

        switch (side_name)
        {
            case "Белые":
                chevron.setImageResource(R.drawable.white_chev);
                break;
            case "Синие":
                chevron.setImageResource(R.drawable.blue_chev);
                break;
            case "Зеленые":
                chevron.setImageResource(R.drawable.green_chev);
                break;
            case "Красные":
                chevron.setImageResource(R.drawable.red_chev);
                break;
            case "Желтые":
                chevron.setImageResource(R.drawable.yellow_chev);
                break;

        }

convertView.setBackgroundColor(Color.TRANSPARENT);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            convertView=inflater.inflate(R.layout.sidecom_team,null);
        }
        String child= (String) getChild(groupPosition,childPosition);
        TextView teamname=convertView.findViewById(R.id.sidecom_teamname);
        TextView team_id=convertView.findViewById(R.id.sidecom_team_id);
        ImageView team_chevron=convertView.findViewById(R.id.team_chevron);

        String[] separated = child.split(":");
        teamname.setText(separated[0]);
        team_id.setText(separated[1]);

        String divname=(String)getGroup(groupPosition).toString();
        switch (divname)
        {
            case "Белые":
                team_chevron.setImageResource(R.drawable.white_chev);
                break;
            case "Синие":
                team_chevron.setImageResource(R.drawable.blue_chev);
                break;
            case "Зеленые":
                team_chevron.setImageResource(R.drawable.green_chev);
                break;
            case "Красные":
                team_chevron.setImageResource(R.drawable.red_chev);
                break;
            case "Желтые":
                team_chevron.setImageResource(R.drawable.yellow_chev);
                break;

        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
