package com.hanshenrik.gronsleth_hasamishogi;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class SelectPlayerActivity extends ActionBarActivity {
    public static final String EXTRA_PLAYERS = "com.hanshenrik.gronsleth_hasmishogi.players";
    public static final int REGISTER_NEW_PLAYER_REQUEST = 1;
    public static final int GUEST_PLAYER_INDEX = 0;
    public String sep = " | "; // DEV
    private ListView player1ListView, player2ListView;
    private ArrayAdapter player1ListAdapter, player2ListAdapter;
    ArrayList<Player> users; // TODO: review if Player structure is necessary, can probably just query PlayersProvider if we need info

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REGISTER_NEW_PLAYER_REQUEST) {
                Log.d("###", "From REGISTER NEW");
                updateUserList(); // TODO: review if this can be done more efficiently
                player1ListAdapter.notifyDataSetChanged();
                player2ListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_player);

        this.users = new ArrayList<>();
        updateUserList();

        this.player1ListView = (ListView) findViewById(R.id.player1_usernames);
        this.player2ListView = (ListView) findViewById(R.id.player2_usernames);
        Button registerNewPlayerButton = (Button) findViewById(R.id.register_new_player_button);
        Button playButton = (Button) findViewById(R.id.play_button);

        this.player1ListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, users);
        this.player2ListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, users);
        player1ListView.setAdapter(player1ListAdapter);
        player2ListView.setAdapter(player2ListAdapter);

        player1ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                syncUsernameLists(player1ListView, player2ListView, position);
            }
        });

        player2ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                syncUsernameLists(player2ListView, player1ListView, position);
            }
        });

        registerNewPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("REGISTER BUTTON", "clicked!");
                Intent intent = new Intent(getApplicationContext(), RegisterNewPlayerActivity.class);
                startActivityForResult(intent, REGISTER_NEW_PLAYER_REQUEST);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PLAY", "getCheckedItemPosition() 1: " + player1ListView.getCheckedItemPosition());
                Log.d("PLAY", "getCheckedItemPosition() 2: " + player2ListView.getCheckedItemPosition());
                // TODO: NB! index in list is not same as ID in DB. Need to fetch that ID., and send it to GameActivity
                int player1 = users.get(player1ListView.getCheckedItemPosition()).id;
                int player2 = users.get(player2ListView.getCheckedItemPosition()).id;

                // TODO: if statements can probably be written nicer
                if (player1 == -1 || player2 == -1) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Select a player for both players.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if (player1 == player2 && player1 != GUEST_PLAYER_INDEX) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Cannot play against yourself (unless you play as GUEST).", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    intent.putExtra(EXTRA_PLAYERS, new int[]{player1, player2});
                    startActivity(intent);
                }
            }
        });

        // set GUEST as selected by default
        player1ListView.setItemChecked(0, true);
        player2ListView.setItemChecked(0, true);

        // DEV click play button
        //playButton.performClick();
    }

    private void updateUserList() {
        Log.d("###", "In updateUserList");
        this.users.clear();
        // get players from PlayersProvider
        Cursor cursor = getContentResolver().query(PlayersProvider.CONTENT_URI, null, null, null, null);

        // TODO: put in detail view of player as DELETE button
        //getContentResolver().delete(Uri.parse(PlayersProvider.CONTENT_URI + "/7"), null, null);
        // NB! /# is ID in DB

        // TODO: only need name and id
        int idIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_ID);
        int nameIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_PLAYER);
        int descriptionIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_DESCRIPTION);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIdx);
                Log.d("###", "id: " +id);
                String name = cursor.getString(nameIdx);
                String description = cursor.getString(descriptionIdx);
                users.add(new Player(id, name, description, 0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        users.add(0, new Player(0, "GUEST", "", 0)); // TODO: avoid creating this every time
    }

    private void syncUsernameLists(ListView thisListView, ListView otherListView, int position) {
        if (!thisListView.getChildAt(position).isEnabled()) {
            // TODO: don't draw selected box
            Log.d("SYNC", "not enabled");
            return;
        }
        String username = thisListView.getItemAtPosition(position).toString();
        for (int i = 0; i < thisListView.getChildCount(); i++) {
            otherListView.getChildAt(i).setEnabled(true);
        }
        if (position != 0) {
            Log.d("SYNC", username + sep + position + sep + otherListView.getChildAt(position));
            otherListView.getChildAt(position).setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == R.id.action_league_table) {
            Intent intent = new Intent(getApplicationContext(), LeagueTableActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
