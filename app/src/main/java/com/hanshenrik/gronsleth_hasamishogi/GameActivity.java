package com.hanshenrik.gronsleth_hasamishogi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.Map;


public class GameActivity extends ActionBarActivity {
    private ImageAdapter boardAdapter; // TODO: consider changing to drawing circles instead of using images
    private boolean isSelecting; // keep track of if user indicates which piece to move or where to put selected piece down
    private View selected;
    private Board board;
    private int[] boardAsList = new int[81]; // TODO: review structure
    private int fromX, fromY, toX, toY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // get id of players
        Intent intent = getIntent();
        int[] playerIDs = intent.getIntArrayExtra(SelectPlayerActivity.EXTRA_PLAYERS);

        isSelecting = true;
        // DEV
        board = new Board(18, 17);

        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                boardAsList[i*Board.BOARD_SIZE + j] = board.get(j, i);
            }
        }

        // DEV
        String s = "| ";
        Log.d("###", "boardAsList.length: " + boardAsList.length);
        for (int i = 0; i < boardAsList.length; i++) {
            if (i % 9 == 0 && i != 0) {
                Log.d("###", s);
                s = "| ";
            }
            s += boardAsList[i] + " | ";
        }
        Log.d("###", s);

        GridView boardGrid = (GridView) findViewById(R.id.board_grid);
        boardAdapter = new ImageAdapter(this, boardAsList);
        boardGrid.setAdapter(boardAdapter);

        boardGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("BOARD GRID", "pos: " + position + " | id: " + id);
                int x = position % Board.BOARD_SIZE; // 8 % 9 = 8, 9 % 9 = 0
                int y = position / Board.BOARD_SIZE; // 8/9 = 0, 10/9 = 1 when using int (floor function)
                if (isSelecting) { // indicating which piece to move
                    view.setAlpha(0.8f);
                    selected = view;
                    int player = boardAsList[position];
                    fromX = x;
                    fromY = y;
                    Log.d("BOARD GRID", "player: " + player);
                } else { // indicating where to move selected piece
                    toX = x;
                    toY = y;
                    board.move(fromX, fromY, toX, toY);
                    // TODO: make this affect the boardAsList in a nicer way
                    for (int i = 0; i < Board.BOARD_SIZE; i++) {
                        for (int j = 0; j < Board.BOARD_SIZE; j++) {
                            boardAsList[i * Board.BOARD_SIZE + j] = board.get(j, i);
                        }
                    }
                    selected.setAlpha(1);
                    boardAdapter.notifyDataSetChanged();
                }
                isSelecting = !isSelecting;
            }
        });


        //setting preferences
//        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.pref_key_dai_version), Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("key", "test");
//        editor.commit();

        //getting preferences

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Map<String,?> map = prefs.getAll();
        for (String key : map.keySet()) {
            Log.d("MAP", key + " | " + map.get(key).toString());
        }


        // testMoves();
    }

    private void testMoves() { // DEV
        board.move(0, 1, 0, 3);
        board.move(0, 7, 0, 4);
        board.move(8, 1, 8, 3);
        board.move(1, 7, 1, 2);
        board.move(7, 1, 7, 3);
        board.move(1, 2, 0, 2);
//        Log.d("move V", board.move(0, 1, 0, 2) + ""); // +y
//        Log.d("move I", board.move(0, 0, 0, 4) + ""); // friendly piece in the way
//        Log.d("move I", board.move(0, 0, 0, 2) + ""); // friendly piece at destination
//        Log.d("move V", board.move(0, 2, 8, 2) + ""); // +x
//        Log.d("move I", board.move(0, 2, 8, 2) + ""); // empty source position
//        Log.d("move V", board.move(8, 7, 8, 6) + ""); // -y
//        Log.d("move V", board.move(8, 6, 0, 6) + ""); // -x
//        Log.d("move V", board.move(0, 0, 0, 2) + ""); //
//        Log.d("move I", board.move(0, 6, 0, 1) + ""); // opponent piece in the way
//        Log.d("move I", board.move(0, 6, 0, 2) + ""); // opponent piece at destination
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_league_table) {
//            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
//            startActivity(intent);
            return true;
        } else if (id == R.id.action_restart) {
            isSelecting = true;
            board = new Board(18, 17); // TODO: remove hardcoded values

            // TODO: optimize, extract to method as this is duplicated code
            for (int i = 0; i < Board.BOARD_SIZE; i++) {
                for (int j = 0; j < Board.BOARD_SIZE; j++) {
                    boardAsList[i*Board.BOARD_SIZE + j] = board.get(j, i);
                }
            }
            boardAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
