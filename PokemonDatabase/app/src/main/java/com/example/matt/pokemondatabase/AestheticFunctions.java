package com.example.matt.pokemondatabase;

import android.util.Log;

public class AestheticFunctions {

    public static final String[][] typeColours = new String[][]{
            {"normal", "#908D62"},{"fire", "#DD691A"}, {"water", "#486BD1"},
            {"electric", "#FFCD2A"}, {"grass", "#67AD37"}, {"ice", "#7FBFBB"},
            {"fighting", "#D92310"}, {"poison", "#7D386C"}, {"ground", "#EDBF61"},
            {"flying", "#9B81BE"}, {"bug", "#99A41E"}, {"rock", "#9E8A2F"},
            {"ghost", "#524260"}, {"dragon", "#531AEF"}, {"dark", "#523F2A"},
            {"steel", "#BCB8CB"}, {"fairy", "#DD89E8"}, {"psychic", "#F72D68"}
    };//TODO: could take this out. save some memory

    public static final String[][] typeIDColours = new String[][]{
            {"1", "#908D62"},{"10", "#DD691A"}, {"11", "#486BD1"},
            {"13", "#FFCD2A"}, {"12", "#67AD37"}, {"15", "#7FBFBB"},
            {"2", "#D92310"}, {"4", "#7D386C"}, {"5", "#EDBF61"},
            {"3", "#9B81BE"}, {"7", "#99A41E"}, {"6", "#9E8A2F"},
            {"8", "#524260"}, {"16", "#531AEF"}, {"17", "#523F2A"},
            {"9", "#BCB8CB"}, {"18", "#DD89E8"}, {"14", "#F72D68"}
    };

    public static String uncapitalise (String input) {
        char[] noCap = input.toCharArray();

        //Uncapitalise the first letter
        noCap[0] += 32;

        return String.valueOf(noCap);}

    public static String capitalise (String input) {

        char[] noCap = input.toCharArray();

        //Capitalise the first letter
        noCap[0] -= 32;

        return String.valueOf(noCap);
    }

    public static String uncapitaliseIncludeDash (String input) {

        char[] noCap = input.toCharArray();

        //Capitalise the first letter
        noCap[0] += 32;

        for (int j = 0; j < noCap.length; j++)
            if (noCap[j] == ' ') {
                noCap[j] = '-';
                noCap[j+1] += 32;
            }

        return String.valueOf(noCap);
    }

    public static String capitaliseRemoveDash (String input) {

        char[] noCap = input.toCharArray();

        //Capitalise the first letter
        noCap[0] -= 32;

        for (int j = 0; j < noCap.length; j++)
            if (noCap[j] == '-') {
                noCap[j] = ' ';
                noCap[j+1] -= 32;
            }

        return String.valueOf(noCap);
    }

    public static String returnColour (String colour) {

        for (String[] typeColour : typeColours) {
            if (colour.equals(typeColour[0]))
                return typeColour[1];
        }

        Log.w("PokeApp", "Did not find matching colour");
        return "#000000";
    }

    public static String returnColourFromID (String colour) {

        for (String[] typeIDColour : typeIDColours) {
            if (colour.equals(typeIDColour[0]))
                return typeIDColour[1];
        }

        Log.w("PokeApp", "Did not find matching colour");
        return "#000000";
    }

    public static String returnNameFromID (String colour) {

        for (int i = 0; i < typeIDColours.length; i++) {
            if (colour.equals(typeIDColours[i][0]))
                return capitalise(typeColours[i][0]);
        }

        Log.w("PokeApp", "Did not find matching type");
        return "Broken";
    }
}
