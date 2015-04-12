package com.hanshenrik.gronsleth_hasamishogi;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;


public class GameActivity extends ActionBarActivity {
    private GridView boardGrid;
    private ImageAdapter boardAdapter; // TODO: consider changing to drawing circles instead of using images

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // get id of players
        Intent intent = getIntent();
        int[] players = intent.getIntArrayExtra(SelectPlayerActivity.EXTRA_PLAYERS);

        Board board = new Board(18, 17);
        int[] boardAsList = new int[81];
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

        this.boardGrid = (GridView) findViewById(R.id.board_grid);
        boardAdapter = new ImageAdapter(this, boardAsList);
        boardGrid.setAdapter(boardAdapter);

        // testMoves();
    }

    private void testMoves() { // DEV
        Board board = new Board(18, 17);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
