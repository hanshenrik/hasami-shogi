package com.hanshenrik.gronsleth_hasamishogi;

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


public class SelectPlayerActivity extends ActionBarActivity {
    private ListView player1ListView;
    private ListView player2ListView;
    public String sep = " | ";

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
        this.player1ListView = (ListView) findViewById(R.id.player1_usernames);
        this.player2ListView = (ListView) findViewById(R.id.player2_usernames);

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
    }

    private void syncUsernameLists(ListView thisListView, ListView otherListView, int position) {
        if (!thisListView.getChildAt(position).isEnabled()) {
            // TODO: don't draw selected box
            Log.d("SYNC", "not enabled");
            return;
        }
        String username = thisListView.getItemAtPosition(position).toString();
        if (position != 0) {
            Log.d("SYNC", username + sep + position + sep + otherListView.getChildAt(position));
            for (int i = 0; i < thisListView.getChildCount(); i++) {
                otherListView.getChildAt(i).setEnabled(true);
            }
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
