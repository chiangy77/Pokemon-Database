package com.example.matt.pokemondatabase;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class LoadFilesTask extends AsyncTask<Void, Void, Void> {

    Activity mActivity = new Activity();

    String[][] types = new String[19][2];
    String[][] type_efficiency = new String[325][3];
    String[][] moves = new String[618][10];
    String[][] move_damage = new String[4][2];
    String[][] dex = new String[785][6];
    String[][] pokemon_abilities = new String[1835][2];
    String[][] pokemon_types = new String[1180][2];
    String[][] pokemon_stats = new String[4705][2];
    String[][] pokemon_moves = new String[48761][2];
    String[][] abilities = new String[180][2];

    public LoadFilesTask (Activity activity, AsyncResponse delegate) {
        super();
        mActivity = activity;
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        void processFinish(String[][] types, String[][] moves, String[][] dex,
                           String[][] type_efficiency, String[][] move_damage,
                           String[][] pokemon_abilities, String[][] pokemon_types,
                           String[][] abilities, String[][] pokemon_stats, String[][] pokemon_moves);
    }

    public AsyncResponse delegate;

    protected Void doInBackground(Void... params) {

        try {

            CSVReader reader = new CSVReader(new InputStreamReader(mActivity.getAssets().open("types.csv")));
            List<String[]> list = reader.readAll();
            types = list.toArray(types);

            CSVReader reader_moves = new CSVReader(new InputStreamReader(mActivity.getAssets().open("moves.csv")));
            List<String[]> list_moves = reader_moves.readAll();
            moves = list_moves.toArray(moves);

            CSVReader reader_pokedex = new CSVReader(new InputStreamReader(mActivity.getAssets().open("pokemon_extra.csv")));
            List<String[]> list_pokedex = reader_pokedex.readAll();
            dex = list_pokedex.toArray(dex);

            CSVReader reader_pokemon_abilities = new CSVReader(new InputStreamReader(mActivity.getAssets().open("pokemon_abilities.csv")));
            List<String[]> list_pokemon_abilities = reader_pokemon_abilities.readAll();
            pokemon_abilities = list_pokemon_abilities.toArray(pokemon_abilities);

            CSVReader reader_type_efficiency = new CSVReader(new InputStreamReader(mActivity.getAssets().open("type_efficiency.csv")));
            List<String[]> list_type_efficiency = reader_type_efficiency.readAll();
            type_efficiency = list_type_efficiency.toArray(type_efficiency);

            CSVReader reader_move_damage = new CSVReader(new InputStreamReader(mActivity.getAssets().open("move_damage_classes.csv")));
            List<String[]> list_move_damage = reader_move_damage.readAll();
            move_damage = list_move_damage.toArray(move_damage);

            CSVReader reader_abilities = new CSVReader(new InputStreamReader(mActivity.getAssets().open("abilities.csv")));
            List<String[]> list_abilities = reader_abilities.readAll();
            abilities = list_abilities.toArray(abilities);

            CSVReader reader_pokemon_types = new CSVReader(new InputStreamReader(mActivity.getAssets().open("pokemon_types.csv")));
            List<String[]> list_pokemon_types = reader_pokemon_types.readAll();
            pokemon_types = list_pokemon_types.toArray(pokemon_types);

            CSVReader reader_pokemon_stats = new CSVReader(new InputStreamReader(mActivity.getAssets().open("pokemon_stats.csv")));
            List<String[]> list_pokemon_stats = reader_pokemon_stats.readAll();
            pokemon_stats = list_pokemon_stats.toArray(pokemon_stats);

            CSVReader reader_pokemon_moves = new CSVReader(new InputStreamReader(mActivity.getAssets().open("pokemon_moves_removed_duplicates.csv")));
            List<String[]> list_pokemon_moves = reader_pokemon_moves.readAll();
            pokemon_moves = list_pokemon_moves.toArray(pokemon_moves);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //If you need to show the progress use this method
//        protected void onProgressUpdate(Integer... progress) {
//            setYourCustomProgressPercent(progress[0]);Log.w("PokeApp", "Files Loaded" );
//        }

    //This method is triggered at the end of the process, in your case when the loading has finished
    @Override
    protected void onPostExecute(Void result) {
        //super.onPostExecute(...);
        Log.w("PokeApp", "In AsycTask");
        Main.createNameArray(dex); Log.w("PokeApp", "After creating name array");
            delegate.processFinish(types, moves, dex, type_efficiency, move_damage,
                    pokemon_abilities, pokemon_types, abilities, pokemon_stats, pokemon_moves);
    }
}
