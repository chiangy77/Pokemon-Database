package com.example.matt.pokemondatabase;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MoveFragmentClass extends Fragment implements Serializable{


    private String fragmentTag;

    public MoveFragmentClass(){}

    public static MoveFragmentClass newInstance(String tag, ArrayList<String> moves, Map<String, String[]> moveInfo){
        MoveFragmentClass mMoveFragmentClass = new MoveFragmentClass();
        Bundle bundle = new Bundle();
        bundle.putString ("tag",tag);
        bundle.putStringArrayList("moves", moves);
        bundle.putSerializable("movesInfo", (Serializable)moveInfo);
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

        final MoveClassExpandableAdapter adapter = new MoveClassExpandableAdapter(getActivity(),
                getArguments().getStringArrayList("moves"), (Map) getArguments().getSerializable("movesInfo"));


        ExpandableListView movesView = (ExpandableListView) view.findViewById(R.id.movesList);
        movesView.setAdapter(adapter);

        movesView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selected = (String) adapter.getGroup(groupPosition);
                Toast.makeText(getActivity(), selected, Toast.LENGTH_LONG).show();
                return true;
            }
        });

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
