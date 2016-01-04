package com.example.matt.pokemondatabase;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MoveFragmentClass extends Fragment {


    private String fragmentTag;

    public MoveFragmentClass(){}

    public static MoveFragmentClass newInstance(String tag, String[] moves){
        MoveFragmentClass mMoveFragmentClass = new MoveFragmentClass();
        Bundle bundle = new Bundle();
        bundle.putString ("tag",tag);
        bundle.putStringArray("moves", moves);
        mMoveFragmentClass.setArguments(bundle);

        return mMoveFragmentClass;
    }

    public interface MoveFragmentListener {
        public void getSuperEffectiveMoves (String tag);
    }

    MoveFragmentListener activityCommander;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            activityCommander = (MoveFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.moves_fragment, container, false);

        fragmentTag = getArguments().getString("tag");

        MoveClassArrayAdapter movesAdapter = new MoveClassArrayAdapter(getActivity(),
                R.layout.move_layout, R.id.row_item, getArguments().getStringArray("moves"));
        ListView movesView = (ListView) view.findViewById(R.id.movesList);
        movesView.setAdapter(movesAdapter);

        Button button = (Button) view.findViewById(R.id.SEbutton);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityCommander.getSuperEffectiveMoves(fragmentTag);
                    }
                }
        );

        return view;
    }
}
