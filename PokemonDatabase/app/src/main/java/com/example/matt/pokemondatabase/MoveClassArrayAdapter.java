package com.example.matt.pokemondatabase;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MoveClassArrayAdapter extends ArrayAdapter<String> {

    private String[] movesFromFrag;
    private Context context;

    public MoveClassArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.movesFromFrag = objects;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int i = 0;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.move_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.row_item);

        do{
            i++;
        } while (Main.moves[i][1].equals(movesFromFrag[position]) == false);

        rowView.setBackgroundColor(Color.parseColor(AestheticFunctions.returnColourFromID(Main.moves[i][2])));
        Log.w("PokeApp", movesFromFrag[position]);

        textView.setText(AestheticFunctions.capitaliseRemoveDash(movesFromFrag[position]));
        return rowView;
    }
}
