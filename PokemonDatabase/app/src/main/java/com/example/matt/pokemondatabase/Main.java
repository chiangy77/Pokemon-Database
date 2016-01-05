package com.example.matt.pokemondatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.*;


public class Main extends FragmentActivity implements LoadFilesTask.AsyncResponse, LeftFragmentClass.LeftFragmentListener,
MoveFragmentClass.MoveFragmentListener {

    //Variables to store data from CSV files
    static String[][] abilities = new String[189][2];
    static String[][] types = new String[19][2];
    static String[][] type_efficiency = new String[325][3];
    static String[][] moves = new String[618][10];
    static String[][] move_damage_type = new String [4][2];
    static String[][] pokedex = new String[785][6];
    static String[][] pokemon_abilities = new String[1835][2];
    static String[][] pokemon_stats = new String [4705][2];
    static String[][] pokemon_types = new String[1180][2];
    static String[][] pokemon_moves = new String[48761][2];

    public static String[] pokemonNames = new String[784];

    String pokeId = "";
    String prevPokeId = "";

    ArrayList<String> leftPokemonMoves, rightPokemonMoves;

    LeftFragmentClass frag;

    @Override
    public void processFinish(String[][] types, String[][] moves, String[][] pokedex,
                              String[][] type_efficiency, String[][] move_damage,
                              String[][] pokemon_abilities, String[][] pokemon_types,
                              String[][] abilities, String[][] pokemon_stats,
                              String[][] pokemon_moves) {
        Main.types = types;
        Main.moves = moves;
        Main.pokedex = pokedex;
        Main.type_efficiency = type_efficiency;
        Main.move_damage_type = move_damage;
        Main.pokemon_abilities = pokemon_abilities;
        Main.pokemon_types = pokemon_types;
        Main.abilities = abilities;
        Main.pokemon_stats = pokemon_stats;
        Main.pokemon_moves = pokemon_moves;

        Toast.makeText(this, "Read File Successfully!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void changeFragments(String tag, String data) {

        prevPokeId = pokeId;
        pokeId = changeNameToNum(data, 1);

        if (pokeId == null) {
            Toast.makeText(this, "Please enter a valid input", Toast.LENGTH_LONG).show();
            return;
        }

        Log.w("PokeApp", "Before replace " + pokeId);

        switch (tag) {
            case "left":
                leftPokemonMoves = getMoves();
                getSupportFragmentManager().beginTransaction().replace(R.id.leftContainer,
                        new MoveFragmentClass().newInstance("left", leftPokemonMoves, getMovesInfo(leftPokemonMoves)), "leftFrag").addToBackStack(null).commit();
                break;
            case "right":
                rightPokemonMoves = getMoves();
                getSupportFragmentManager().beginTransaction().replace(R.id.rightContainer,
                        new MoveFragmentClass().newInstance("right", rightPokemonMoves, getMovesInfo(rightPokemonMoves)), "rightFrag").addToBackStack(null).commit();
                break;
            default:
                Toast.makeText(this, "Moves button pressed but something went wrong :(", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void getInput(String tag, String data) {

        String pokeDexNum;

        switch (tag) {
            case "left":
                frag = (LeftFragmentClass) getSupportFragmentManager().findFragmentById(R.id.leftContainer);
                Log.w("PokeApp", "Left!");
                break;
            case "right":
                frag = (LeftFragmentClass) getSupportFragmentManager().findFragmentById(R.id.rightContainer);
                Log.w("PokeApp", "Right!");
                break;
            default:
                Log.w("PokeApp", tag);
                break;
        }

        try {
            pokeDexNum = changeNameToNum(data, 0);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.w("PokeApp", e.toString());
            Toast.makeText(this, "Please enter a valid input", Toast.LENGTH_LONG).show();
            return;
        }

        Log.w("PokeAppppp", data);

        //Displays Pokemon image
        new ImageDisplay().execute(AestheticFunctions.uncapitaliseIncludeDash(data));

        //Retrieves and sets Pokemon's name
        frag.setName(data);

        //Retrieves and sets Pokemon's type
        frag.setType(getTypes(pokeDexNum));

        //Retrieves and sets Pokemon's abilities
        frag.setAbilities(getAbilities(pokeDexNum));

        //Retrieves and sets Pokemon's stats
        frag.setStat(getStats(pokeDexNum));

        //frag.addButton(findOtherForms(pokeDexNum));
    }

    @Override
    public void getSuperEffectiveMoves(String tag) {

        String[] pokeType;
        ArrayList<String> moveFromFrag;
        String type1, type2 = "";
        String type1int, type2int, moveTypeInt;
        int y=0, position;
        double effectiveness;
        ArrayList<String> superEffMoves = new ArrayList<>();

        try {
            switch (tag) {
                case "right":
                    moveFromFrag = rightPokemonMoves;
                    frag = (LeftFragmentClass) getSupportFragmentManager().findFragmentById(R.id.leftContainer);
                    Log.w("PokeApp", "Getting type from left");
                    break;
                case "left":
                    moveFromFrag = leftPokemonMoves;
                    frag = (LeftFragmentClass) getSupportFragmentManager().findFragmentById(R.id.rightContainer);
                    Log.w("PokeApp", "Getting type from right");
                    break;
                default:
                    Log.w("PokeApp", tag);
                    Toast.makeText(this, "Something majorly wrong just happened. How did you do that? That's actually very" +
                            " impressive but also a little worrying.", Toast.LENGTH_LONG).show();
                    return;
            }
        } catch (ClassCastException e) {
            Log.w("PokeApp", e.toString());
            Toast.makeText(this, "Please return to the original screen showing the Pokemon's name", Toast.LENGTH_LONG).show();
            return;
        }

        pokeType = frag.getType();

        try {
            type1 = AestheticFunctions.uncapitalise(pokeType[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.w("PokeApp", e.toString());
            Toast.makeText(this, "Please enter an input on the other side", Toast.LENGTH_LONG).show();
            return;
        }


        if (!pokeType[1].equals("")) {
            type2 = AestheticFunctions.uncapitalise(pokeType[1]);

        }

        Log.w("PokeApp", type1);

        //Translate the type name to type id
        type1int = typeNameToID(type1);
        type2int = typeNameToID(type2);
        Log.w("PokeApp", type1int);

        for (position = 0; position < moveFromFrag.size(); position++) {
            //Get move type
            do {
                y++;
            } while (!AestheticFunctions.capitaliseRemoveDash(moves[y][1]).equals(moveFromFrag.get(position)));
            moveTypeInt = moves[y][2];

            //Retrieve how effective move is against this Pokemon
            effectiveness = effectiveness(moveTypeInt, type1int, type2int);

            String printCheck = moveFromFrag.get(position) + " does " + Double.toString(effectiveness) + "% damage to " + type1 +
                    " type Pokemon. It's super effective!" + position;

            if (effectiveness > 1) {
                Log.w("PokeApp", printCheck);
                if (!moves[y][7].equals("1")) { //Checks if move deals damage
                    superEffMoves.add(moveFromFrag.get(position));
                }
            }
            y=0;
        }

        try {
            if (tag.equals("left")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.leftContainer,
                        new MoveFragmentClass().newInstance("left", superEffMoves, getMovesInfo(superEffMoves))).addToBackStack(null).commit();
            } else if (tag.equals("right")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.rightContainer,
                        new MoveFragmentClass().newInstance("right", superEffMoves, getMovesInfo(superEffMoves))).addToBackStack(null).commit();
            } else {
                Toast.makeText(this, "Moves button pressed but something went wrong :(", Toast.LENGTH_LONG).show();
            }
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, "This Pokemon does not possess any super effective moves against the other pokemon", Toast.LENGTH_LONG).show();
        }
    }

    //AsycTask used to attain information from RESTful API calls
    private class LongRunningGetIO extends AsyncTask <String, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();

            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);
                if (n>0) out.append(new String(b, 0, n));
            }

            return out.toString();
        }

        @Override
        protected String doInBackground(String... params) {

            String apiCall = "http://pokeapi.co/api/v1/pokemon/" + params[0] + "/";
            Log.w("PokeApp", apiCall);

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet(apiCall);

            String text;

            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();
            }

            return text;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results!=null) {

                String name = "";
                int id = 0;
                String type = "";
                int hp = 0, attack = 0, def = 0, spAttack = 0, spDef = 0, speed = 0;
                String abilities = "";
                String moves = "";

                try {
                    JSONObject obj = new JSONObject(results);

                    //Get name
                    name = obj.getString("name");

                    //Get national Pokedex ID
                    id = obj.getInt("national_id");

                    //Get types
                    JSONArray tp = obj.getJSONArray("types");
                    for (int i = 0; i < tp.length(); i++) {
                        char[] temp = tp.getJSONObject(i).getString("name").toCharArray();

                        //Capitalise the first letter
                        temp[0] -= 32;

                        type += String.valueOf(temp);

                        //Add a comma between each element
                        if (i != tp.length()-1)
                            type += ", ";
                    }

                    //Get Pokemon's abilities
                    JSONArray ab = obj.getJSONArray("abilities");
                    for (int i = 0; i < ab.length(); i++) {
                        char[] temp = ab.getJSONObject(i).getString("name").toCharArray();
                        temp[0] -= 32;

                        //Replace the '-' with a space and capitalise the first letter
                        for (int j = 0; j < temp.length; j++)
                            if (temp[j] == '-') {
                                temp[j] = ' ';
                                temp[j+1] -= 32;
                            }

                        abilities += String.valueOf(temp);

                        //Add a comma between each element
                        if (i != ab.length()-1)
                            abilities += ", ";

                    }

                    //Get hp stat
                    hp = obj.getInt("hp");

                    //Get attack stat
                    attack = obj.getInt("attack");

                    //Get defence stat
                    def = obj.getInt("defense");

                    //Get special attack stat
                    spAttack = obj.getInt("sp_atk");

                    //Get special defence stat
                    spDef = obj.getInt("sp_def");

                    //Get speed stat
                    speed = obj.getInt("speed");

                    //Get Pokemon moves
                    JSONArray mvs = obj.getJSONArray("moves");
                    for (int i = 0; i < mvs.length(); i++) {
                        char[] temp = mvs.getJSONObject(i).getString("name").toCharArray();

                        //Replace the '-' with a space and capitalise the first letter
                        for (int j = 0; j < temp.length; j++) {
                            if(temp[j] == '-') {
                                temp[j] = ' ';
                                temp[j+1] -= 32;
                            }
                        }

                        //Add a comma between each element
                        moves += String.valueOf(temp);

                        if (i != mvs.length()-1)
                            moves += ", ";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String str = name + "\n" + id + "\n" + type + "\n" + abilities + "\n" + hp + "\n"
                        + attack + "\n" + def + "\n" + spAttack + "\n" + spDef + "\n" + speed
                        + "\n" + moves;

//                EditText et = (EditText) findViewById(R.id.my_edit);
//                et.setText(str);
            }
        }
    }

    //AsyncTask used to retrieve images of Pokemon
    private class ImageDisplay extends AsyncTask <String, Void, Void> {

        Bitmap  downloadBitmap;

        @Override
        protected Void doInBackground(String... params) {

            String tempURL;

            try {
                tempURL = "http://www.pokestadium.com/sprites/black-white/" + params[0] + ".png";
                URL myFileUrl = new URL(tempURL);
                Log.w("PokeApp", myFileUrl.toString());

                HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                Log.w("PokeApp", "Connected to URL");

                InputStream is = conn.getInputStream();
                downloadBitmap = BitmapFactory.decodeStream(is);
                Log.w("PokeApp", "Image stored within Bitmap");
            } catch (FileNotFoundException e) {
                try {
                    if (params[0].equals("nidoran-f"))
                        tempURL = "http://www.pokestadium.com/sprites/black-white/nidoranf.png";
                    else if (params[0].equals("nidoran-m"))
                        tempURL = "http://www.pokestadium.com/sprites/black-white/nidoranm.png";
                    else if (params[0].equals("floette-eternal"))
                        tempURL = "http://www.pokestadium.com/sprites/xy-fan/floette.png";
                    else
                        tempURL = "http://www.pokestadium.com/sprites/xy-fan/" + params[0] + ".png";

                    URL myFileUrl = new URL(tempURL);
                    Log.w("PokeApp", myFileUrl.toString());

                    HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    Log.w("PokeApp", "Connected to URL");

                    InputStream is = conn.getInputStream();
                    downloadBitmap = BitmapFactory.decodeStream(is);
                    Log.w("PokeApp", "Image stored within Bitmap");

                } catch (FileNotFoundException f) {
                    f.printStackTrace();
                }
                catch (IOException f) {
                    f.printStackTrace();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            frag.pokePNG.setImageBitmap(downloadBitmap);
        }
    } //TODO:Change website get


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new LoadFilesTask(this, this).execute();

        getSupportFragmentManager().beginTransaction().add(R.id.leftContainer, new LeftFragmentClass().newInstance("left"),"leftFrag").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.rightContainer, new LeftFragmentClass().newInstance("right"),"rightFrag").commit();

    }

    public static String typeNameToID (String typeName) {
        int z=0;

        if (typeName.equals(""))
            return "";
        else {
            do {
                z++;
            } while (!types[z][1].equalsIgnoreCase(typeName));
            return types[z][0];
        }
    }

    public static Double effectiveness(String moveType, String type1, String type2) {
        int z=0, damageFactor, counter=0;

        do {
            z++;
        } while (!type_efficiency[z][0].equals(moveType));

        while (!type_efficiency[z][1].equals(type1))
            z++;

        damageFactor = Integer.valueOf(type_efficiency[z][2]);
        counter++;
        z=0;

        if (!type2.equals("")) {
            do {
                z++;
            } while (!type_efficiency[z][0].equals(moveType));

            while (!type_efficiency[z][1].equals(type2))
                z++;

            damageFactor *= Integer.valueOf(type_efficiency[z][2]);
            counter++;
        }

        return damageFactor/Math.pow(100, counter);
    }

    private String changeNameToNum(String name, int flag){
        int i=0;

        name = AestheticFunctions.uncapitaliseIncludeDash(name);

        do {
            i++;
            if (i > pokedex.length)
                return null;
        } while (!pokedex[i][1].equalsIgnoreCase(name));

        if (flag == 0) //For general use
            return pokedex[i][0];
        else //For moves
            return pokedex[i][2];
    }

    public static void createNameArray(String[][] input){
        for (int i = 1; i < pokedex.length; i++) {
            pokemonNames[i-1] = AestheticFunctions.capitaliseRemoveDash(input[i][1]);
            //Log.w("PokeApp", pokemonNames[i-1]);
        }
        Arrays.sort(pokemonNames);
    }

    private Map<String, String[]> getMovesInfo (ArrayList<String> moveSet) {

        int counter = 0, i = 0;
        Map<String, String[]> info = new HashMap<>();

        do {
            do {
                i++;
                if (i == moves.length)
                    break;
            } while (!moveSet.get(counter).equals(AestheticFunctions.capitaliseRemoveDash(moves[i][1])));

            info.put(moveSet.get(counter), new String[]{moves[i][2], moves[i][3], moves[i][4]});
            counter++;
            i = 0;

        } while (counter < moveSet.size());

        Log.w("PokeApp", "Finished putting move info");
        return info;
    }

    private ArrayList<String> getMoves() {

        ArrayList<String> mMoves = new ArrayList<>(), moveNames = new ArrayList<>();
        int counter=0,i=0, z=0;
        int prevFlag = 0, curFlag;

        do {
            if (pokemon_moves[i][0].equals(pokeId)) {
                mMoves.add(counter, pokemon_moves[i][1]);
                counter++;
                curFlag = 1;
            } else {
                curFlag = 0;
            }

            if (curFlag == 0 && prevFlag == 1)
                break;

            prevFlag = curFlag;
            i++;
        } while (pokemon_moves[i][0] != null);

        counter = 0;

        do {
            do {
                z++;
                if (z == moves.length)
                    break;
            } while (!mMoves.get(counter).equals(moves[z][0]));

            moveNames.add(AestheticFunctions.capitaliseRemoveDash(moves[z][1]));

            counter++;
            z = 0;

        } while (counter < mMoves.size());

        Collections.sort(moveNames);

        Log.w("PokeApp", "I'm out. Elements in array: " + counter);

        return moveNames;
    }

    private String[] getStats (String id) {
        String[] stats = new String[6];
        int counter=0,i=0;
        int prevFlag = 0, curFlag;

        do {

            if (pokemon_stats[i][0].equals(id)) {
                stats[counter] = pokemon_stats[i][1];
                counter++;
                curFlag = 1;
            } else {
                curFlag = 0;
            }

            if (curFlag == 0 && prevFlag == 1)
                break;

            prevFlag = curFlag;
            i++;
        } while (pokemon_stats[i][0] != null);

        return stats;
    }

    private String[] getTypes (String id) {

        String[] typeIDs = new String[2];
        String[] typeNames = new String[2];
        int counter = 0, i = 1, z = 0;
        int prevFlag = 0, curFlag;

        do {

            if (pokemon_types[i][0].equals(id)) {
                typeIDs[counter] = pokemon_types[i][1];
                counter++;
                curFlag = 1;
            } else {
                curFlag = 0;
            }

            if (curFlag == 0 && prevFlag == 1)
                break;

            prevFlag = curFlag;
            i++;
        } while (pokemon_types[i][0] != null);

        for (int j = 0; j < 2; j++) {
            do {
                z++;
                if (z == types.length)
                    break;
            } while (!typeIDs[j].equals(types[z][0]));


            typeNames[j] = types[z][1];
            z = 0;

            if (j == 0 && typeIDs[1] == null)
                break;
        }

        return typeNames;
    }

    private String[] getAbilities (String id) {

        String[] abilityIDs = new String[3];
        String[] abilityNames = new String[3];
        int counter = 0, i = 1, z = 0;
        int prevFlag = 0, curFlag;

        do {

            if (pokemon_abilities[i][0].equals(id)) {
                abilityIDs[counter] = pokemon_abilities[i][1];
                counter++;
                curFlag = 1;
            } else {
                curFlag = 0;
            }

            if (curFlag == 0 && prevFlag == 1)
                break;

            prevFlag = curFlag;
            i++;
        } while (pokemon_abilities[i][0] != null);

        for (int j = 0; j < 3; j++) {
            do {
                z++;
                if (z == abilities.length)
                    break;
            } while (!abilityIDs[j].equals(abilities[z][0]));


            abilityNames[j] = abilities[z][1];
            z = 0;

            if ((j == 1 && abilityIDs[2] == null) || (j == 0 && abilityIDs[1] == null))
                break;

        }

        return abilityNames;
    }
}