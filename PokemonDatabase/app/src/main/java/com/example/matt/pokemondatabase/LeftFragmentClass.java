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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    private Button altFormButton;
    public volatile ImageView pokePNG;
    private PopupWindow popupMessage;
    private Button resWeakButton;

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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left_fragment, container, false);

        fragmentTag = getArguments().getString("tag");

        inputText = (EditText) view.findViewById(R.id.user_edit);
        nameText = (TextView) view.findViewById(R.id.nameEdit);
        type1Text = (TextView) view.findViewById(R.id.typeEdit1);
        type2Text = (TextView) view.findViewById(R.id.typeEdit2);
        abilityText = (TextView) view.findViewById(R.id.abilityEdit);
        hp = (TextView) view.findViewById(R.id.hpEdit);
        attack = (TextView) view.findViewById(R.id.attackEdit);
        defense = (TextView) view.findViewById(R.id.defEdit);
        SpA = (TextView) view.findViewById(R.id.SpAEdit);
        SpD = (TextView) view.findViewById(R.id.spDEdit);
        speed = (TextView) view.findViewById(R.id.speedEdit);
        rl = (RelativeLayout) view.findViewById(R.id.statFrag);

        pokePNG = (ImageView) view.findViewById(R.id.img_box);
        Button button = (Button) view.findViewById(R.id.my_button);

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonClicked();
                    }
                }
        );

        Button button1 = (Button) view.findViewById(R.id.moves_button);
        button1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityCommander.changeFragments(fragmentTag, inputText.getText().toString());
                    }
                }
        );

        resWeakButton = (Button) view.findViewById(R.id.res_weak_button);
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
        AutoCompleteTextView textView = (AutoCompleteTextView) view.findViewById(R.id.user_edit);
        textView.setAdapter(adapter);

        return view;
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
            return;
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

    public void addButton (Integer buttonToCreate) {

        if (buttonToCreate > 0) {
            if (buttonFlag == 0) {
                buttonFlag = 1;

                Log.w("PokeApp", "Creating new button");

                altFormButton = new Button(getActivity());
                altFormButton.setText("Alternate Forms");
                altFormButton.setTransformationMethod(null);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rl.addView(altFormButton, lp);
            }
        } else {
            if (buttonFlag == 1) {
                buttonFlag = 0;

                rl.removeView(altFormButton);
            }
        }
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
        String str = "", temp = "";

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
