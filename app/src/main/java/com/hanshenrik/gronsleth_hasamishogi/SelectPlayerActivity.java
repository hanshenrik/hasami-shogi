package com.hanshenrik.gronsleth_hasamishogi;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class SelectPlayerActivity extends ActionBarActivity {
    public static final String EXTRA_PLAYERS = "com.hanshenrik.gronsleth_hasmishogi.players";
    private static final int GUEST_PLAYER_INDEX = 0;
    public String sep = " | "; // DEV
    private ListView player1ListView;
    private ListView player2ListView;
    private ContentResolver cr;
    private TextView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_player);

        ArrayList<String> users = new ArrayList<>();
        // TODO: fetch usernames (possibly avatar) from league table content provider
        users.add("GUEST");
        users.add("tim");
        users.add("tom");
        users.add("john");
        users.add("spa ce");

//        list = (TextView) findViewById(R.id.test_list);
//        cr = getContentResolver();
//        Cursor c = cr.query(PlayersProvider.CONTENT_URI, null, null, null, null);
//        int total = c.getCount();
//        String table = "";
//        if (c.moveToFirst()) {
//            do {
//                table += c.getPosition() + "  " +  c.getString(PlayersProvider.PLAYER_COLUMN) + sep
//                        + c.getInt(PlayersProvider.POINTS_COLUMN) + sep +
//                        c.getString(PlayersProvider.DESCRIPTION_COLUMN) + "\n";
//            } while (c.moveToNext());
//        }
//        list.setText(table + "total: " + total);

        this.player1ListView = (ListView) findViewById(R.id.player1_usernames);
        this.player2ListView = (ListView) findViewById(R.id.player2_usernames);
        Button registerNewPlayerButton = (Button) findViewById(R.id.register_new_player_button);
        Button playButton = (Button) findViewById(R.id.play_button);

        final ArrayAdapter player1ListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, users);
        final ArrayAdapter player2ListAdapter = new ArrayAdapter<>(this,
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
                startActivity(intent);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PLAY", "getCheckedItemPosition() 1: " + player1ListView.getCheckedItemPosition());
                Log.d("PLAY", "getCheckedItemPosition() 2: " + player2ListView.getCheckedItemPosition());
                int player1 = player1ListView.getCheckedItemPosition();
                int player2 = player2ListView.getCheckedItemPosition();

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
