package com.hanshenrik.gronsleth_hasamishogi;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class LeagueTableActivity extends ActionBarActivity {
    private String sep = " | ";
    private ArrayList<Player> players;
    private ArrayAdapter leagueTableAdapter;
    private ListView leagueTableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_table);

        players = new ArrayList<>();
        String orderBy = PlayersProvider.KEY_POINTS + " DESC";
        Cursor cursor = getContentResolver().query(PlayersProvider.CONTENT_URI, null, null, null, orderBy);
        int idIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_ID);
        int nameIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_PLAYER);
        int descriptionIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_DESCRIPTION);
        int pointsIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_POINTS);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIdx);
                String name = cursor.getString(nameIdx);
                String description = cursor.getString(descriptionIdx);
                int points = cursor.getInt(pointsIdx);
                players.add(new Player(id, name, description, points));
            } while (cursor.moveToNext());
        }
        cursor.close();

        leagueTableListView = (ListView) findViewById(R.id.league_table_list);
        leagueTableAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, players);
        leagueTableListView.setAdapter(leagueTableAdapter);

        leagueTableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("###", position + "");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_league_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
