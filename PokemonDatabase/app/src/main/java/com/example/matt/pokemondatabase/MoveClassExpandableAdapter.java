package com.example.matt.pokemondatabase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;


public class MoveClassExpandableAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> moves;
    private Map<String, String[]> moveInfo;
    private Activity context;

    public MoveClassExpandableAdapter(Activity context, ArrayList<String> moves,
                                            Map<String, String[]> moveInfo) {
        this.moves = moves;
        this.moveInfo = moveInfo;
        this.context = context;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return moveInfo.get(moves.get(groupPosition));
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null)
            convertView = inflater.inflate(R.layout.move_information, null);

        final String[] curMoveInfo = (String []) getChild(groupPosition, 0);

        TextView type = (TextView) convertView.findViewById(R.id.type);
        TextView movePower = (TextView) convertView.findViewById(R.id.movePower);
        TextView pp = (TextView) convertView.findViewById(R.id.movePP);

        type.setBackgroundColor(Color.parseColor(AestheticFunctions.returnColourFromID(curMoveInfo[0])));
        type.setText(AestheticFunctions.returnNameFromID(curMoveInfo[0]));

        movePower.setText(curMoveInfo[1]);
        pp.setText(curMoveInfo[2]);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    @Override
    public int getGroupCount() {
        return moves.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return moves.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String move = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.move_layout, null);
        }

        TextView curMove = (TextView) convertView.findViewById(R.id.row_item);
        curMove.setText(move);
        String[] temp = (String[]) getChild(groupPosition, 0);
        curMove.setBackgroundColor(Color.parseColor(AestheticFunctions.returnColourFromID(temp[0])));
        return convertView;
    }
}
