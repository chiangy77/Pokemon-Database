package com.example.matt.pokemondatabase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LeftFragmentClass extends Fragment {

    private EditText inputText;
    private TextView nameText;
    private TextView abilityText;
    private TextView type1Text;
    private TextView type2Text;
    private TextView hp;
    private TextView attack;
    private TextView defense;
    private TextView SpA;
    private TextView SpD;
    private TextView speed;
    private RelativeLayout rl;

    private View _rootView;

    public volatile ImageView pokePNG;
    private PopupWindow popupMessage;

    private String fragmentTag = null;
    private int buttonFlag = 0;

    public LeftFragmentClass(){}

    public static LeftFragmentClass newInstance(String tag){
        LeftFragmentClass mLeftFragmentClass = new LeftFragmentClass();
        Bundle bundle = new Bundle();
        bundle.putString ("tag",tag);
        mLeftFragmentClass.setArguments(bundle);

        return mLeftFragmentClass;
    }

    public interface LeftFragmentListener {
        public void getInput(String tag, String data);
        public void changeFragments(String tag, String data);
    }

    LeftFragmentListener activityCommander;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            activityCommander = (LeftFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("name", nameText.getText().toString());
//        outState.putString("ability", abilityText.getText().toString());
//        outState.putString("type1", type1Text.getText().toString());
//        outState.putString("type2", type2Text.getText().toString());
//        outState.putString("hp", hp.getText().toString());
//        outState.putString("attack", attack.getText().toString());
//        outState.putString("defence", defense.getText().toString());
//        outState.putString("spA", SpA.getText().toString());
//        outState.putString("spD", SpD.getText().toString());
//        outState.putString("speed", speed.getText().toString());
//        Log.w("PokeApp", "Saved values\n\n");
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        Log.w("PokeApp", "Checking if values should be restored");
//
//        if (savedInstanceState != null) {
//            Log.w("PokeApp", "Restoring values");
//            inputText.setText(savedInstanceState.getString("name", "Stupid"));
//            abilityText.setText(savedInstanceState.getString("ability", "Stupid"));
//            type1Text.setText(savedInstanceState.getString("type1", "Stupid"));
//            type2Text.setText(savedInstanceState.getString("type2", "Stupid"));
//            hp.setText(savedInstanceState.getString("hp", "Stupid"));
//            attack.setText(savedInstanceState.getString("attack", "Stupid"));
//            defense.setText(savedInstanceState.getString("defence", "Stupid"));
//            SpA.setText(savedInstanceState.getString("spA", "Stupid"));
//            SpD.setText(savedInstanceState.getString("spD", "Stupid"));
//            speed.setText(savedInstanceState.getString("speed", "Stupid"));
//
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (_rootView == null) {
            _rootView = inflater.inflate(R.layout.left_fragment, container, false);


            fragmentTag = getArguments().getString("tag");

            inputText = (EditText) _rootView.findViewById(R.id.user_edit);
            nameText = (TextView) _rootView.findViewById(R.id.nameEdit);
            type1Text = (TextView) _rootView.findViewById(R.id.typeEdit1);
            type2Text = (TextView) _rootView.findViewById(R.id.typeEdit2);
            abilityText = (TextView) _rootView.findViewById(R.id.abilityEdit);
            hp = (TextView) _rootView.findViewById(R.id.hpEdit);
            attack = (TextView) _rootView.findViewById(R.id.attackEdit);
            defense = (TextView) _rootView.findViewById(R.id.defEdit);
            SpA = (TextView) _rootView.findViewById(R.id.SpAEdit);
            SpD = (TextView) _rootView.findViewById(R.id.spDEdit);
            speed = (TextView) _rootView.findViewById(R.id.speedEdit);
            rl = (RelativeLayout) _rootView.findViewById(R.id.statFrag);

            pokePNG = (ImageView) _rootView.findViewById(R.id.img_box);
            Button button = (Button) _rootView.findViewById(R.id.my_button);

            button.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonClicked();
                        }
                    }
            );

            Button button1 = (Button) _rootView.findViewById(R.id.moves_button);
            button1.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activityCommander.changeFragments(fragmentTag, inputText.getText().toString());
                        }
                    }
            );

            Button resWeakButton = (Button) _rootView.findViewById(R.id.res_weak_button);
            resWeakButton.setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                Log.w("PokeApp", "DOWN");
                                createPopupVisuals(v);
                                return true;
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                Log.w("PokeApp", "UP");
                                popupMessage.dismiss();
                                return true;
                            }
                            return false;
                        }
                    }
            );

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, Main.pokemonNames);
            AutoCompleteTextView textView = (AutoCompleteTextView) _rootView.findViewById(R.id.user_edit);
            textView.setAdapter(adapter);
        } else
            ((ViewGroup)_rootView.getParent()).removeView(_rootView);

        return _rootView;
    }

    private void createPopupVisuals(View anchorView){

        int counterWeak = 111, counterRes = 111, counterImmune = 111;

        View popupView = getActivity().getLayoutInflater().inflate(R.layout.popup_layout, null);

        popupMessage = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        // If the PopupWindow should be focusable
        popupMessage.setFocusable(true);

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupMessage.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0], location[1] + anchorView.getHeight());

        Log.w("PokeApp", "Should have created layout");

        String[] pokemonTypes = getType();

        try {
            String type1 = Main.typeNameToID(pokemonTypes[0]);
            String type2 = Main.typeNameToID(pokemonTypes[1]);

            for (int i = 0; i < AestheticFunctions.typeIDColours.length; i++) {
                if (Main.effectiveness(AestheticFunctions.typeIDColours[i][0],
                        type1, type2) > 1) {
                    Log.w("PokeApp", "Weaknesses: " + AestheticFunctions.typeIDColours[i][0]);

                    RelativeLayout weakRL = (RelativeLayout) popupView.findViewById(R.id.weaknessTypes);
                    addTextViewBox(weakRL, counterWeak, i);

                    counterWeak++;

                } else if (Main.effectiveness(AestheticFunctions.typeIDColours[i][0],
                        type1, type2) == 0) {
                    Log.w("PokeApp", "Immune: " + AestheticFunctions.typeIDColours[i][0]);

                    RelativeLayout immuneRL = (RelativeLayout) popupView.findViewById(R.id.immuneTypes);
                    addTextViewBox(immuneRL, counterImmune, i);

                    counterImmune++;

                } else if (Main.effectiveness(AestheticFunctions.typeIDColours[i][0],
                        type1, type2) < 1) {
                    Log.w("PokeApp", "Resists: " + AestheticFunctions.typeIDColours[i][0]);

                    RelativeLayout resRL = (RelativeLayout) popupView.findViewById(R.id.resistanceTypes);
                    addTextViewBox(resRL, counterRes, i);

                    counterRes++;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.w("PokeApp", e.toString());
            Toast.makeText(getActivity(), "Please enter a valid input", Toast.LENGTH_LONG).show();
        }
    }

    private void addTextViewBox (RelativeLayout layout, int counter, int index) {

        TextView newTextViewBox = new TextView(getActivity());
        newTextViewBox.setId(counter);

        newTextViewBox.setBackgroundColor(Color.parseColor(AestheticFunctions.typeIDColours[index][1]));
        newTextViewBox.setText(AestheticFunctions.capitalise(AestheticFunctions.typeColours[index][0]));
        newTextViewBox.setTextColor(Color.WHITE);
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65,
                getResources().getDisplayMetrics());
        newTextViewBox.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                px, RelativeLayout.LayoutParams.WRAP_CONTENT);

        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics());

        if (counter == 119) {
            layoutParams.addRule(RelativeLayout.BELOW, counter-4);
            layoutParams.setMargins(0,px,px,0);

        } else if (counter > 119) {
            layoutParams.addRule(RelativeLayout.END_OF, counter-1);
            layoutParams.addRule(RelativeLayout.BELOW, counter-4);
            layoutParams.setMargins(0,px,px,0);

        }else if (counter == 115) {
            layoutParams.addRule(RelativeLayout.BELOW, counter-4);
            layoutParams.setMargins(0,px,px,0);

        } else if (counter > 115) {
            layoutParams.addRule(RelativeLayout.END_OF, counter-1);
            layoutParams.addRule(RelativeLayout.BELOW, counter-4);
            layoutParams.setMargins(0,px,px,0);

        } else if (counter > 111) {
            layoutParams.addRule(RelativeLayout.END_OF, counter-1);
            layoutParams.setMargins(0,0,px,0);

        } else {
            layoutParams.setMargins(0,0,px,0);
        }

        layout.addView(newTextViewBox, layoutParams);
    }

    public String[] getType() {
        String[] temp = new String[2];

        temp[0] = type1Text.getText().toString();
        temp[1] = type2Text.getText().toString();

        return temp;
    }

    public String getName() {
        return nameText.getText().toString();
    }

    public void buttonClicked(){
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(inputText.getWindowToken(), 0);

        activityCommander.getInput(fragmentTag, inputText.getText().toString());
    }

    public void setStat (String [] data) {

        hp.setText(data[0]);
        attack.setText(data[1]);
        defense.setText(data[2]);
        SpA.setText(data[3]);
        SpD.setText(data[4]);
        speed.setText(data[5]);
    }

    public void setType (String [] data) {

        //Reset second field. First will always be filled.
        type2Text.setBackgroundColor(Color.TRANSPARENT);
        type2Text.setText("");

        type1Text.setBackgroundColor(Color.parseColor(AestheticFunctions.returnColour(data[0])));
        type1Text.setText(AestheticFunctions.capitalise(data[0]));

        if (data[1] != null) {
            type2Text.setBackgroundColor(Color.parseColor(AestheticFunctions.returnColour(data[1])));
            type2Text.setText(AestheticFunctions.capitalise(data[1]));
        }
    }

    public void setName(String data){
        nameText.setText(data);
    }

    public void setAbilities(String[] data) {
        String str = "", temp;

        temp = "Data length: " + data.length;
        Log.w("PokeApp", temp);

        for (int i = 0;  i < data.length; i++) {

            str += AestheticFunctions.capitaliseRemoveDash(data[i]);

            if (i == 2)
                continue;
            else if (data[i+1] == null)
                break;

            if (i != data.length - 1)
                str += "\n";
        }

        abilityText.setText(str);
    }
}
