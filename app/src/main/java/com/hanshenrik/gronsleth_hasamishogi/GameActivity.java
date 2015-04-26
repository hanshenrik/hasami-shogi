package com.hanshenrik.gronsleth_hasamishogi;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.Map;


public class GameActivity extends ActionBarActivity {
    public static final int SETTINGS_REQUEST = 2;
    private static final int DEFAULT_NUMBER_OF_PIECES = 9;
    private static final int DAI_NUMBER_OF_PIECES = 18;
    private ImageAdapter boardAdapter;
    private boolean isSelecting;    // keep track of if user indicates which piece to move or where
                                    // to put selected piece down
    private View selected;
    private Board board;
    private int[] boardAsList = new int[81]; // to displaying in LinearLayout 9x9
    private int fromX, fromY, toX, toY;

    // settings preferences
    private boolean isDaiVersion, isFiveInARowRule;
    private int numberOfPieces, capturesToWin;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            if (requestCode == SETTINGS_REQUEST) {
                updatePreferences();
                // setup new game with given preferences only if no moves have been made
                if (!board.isTouched) {
                    setupGame();
                }
                boardAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // get id of players
        Intent intent = getIntent();
        final int[] playerIDs = intent.getIntArrayExtra(SelectPlayerActivity.EXTRA_PLAYERS);

        updatePreferences();
        setupGame();

        GridView boardGrid = (GridView) findViewById(R.id.board_grid);
        boardAdapter = new ImageAdapter(this, boardAsList);
        boardGrid.setAdapter(boardAdapter);

        boardGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int x = position % Board.BOARD_SIZE; // 8 % 9 = 8, 9 % 9 = 0
                int y = position / Board.BOARD_SIZE; // 8/9 = 0, 10/9 = 1 when using int (floor function)
                if (isSelecting) { // indicating which piece to move
                    view.setAlpha(0.7f);
                    selected = view;
                    fromX = x;
                    fromY = y;
                } else { // indicating where to move selected piece
                    toX = x;
                    toY = y;
                    String message = board.move(fromX, fromY, toX, toY);
                    if (message != null) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                    // TODO: make this affect the boardAsList in a nicer way
                    for (int i = 0; i < Board.BOARD_SIZE; i++) {
                        for (int j = 0; j < Board.BOARD_SIZE; j++) {
                            boardAsList[i * Board.BOARD_SIZE + j] = board.get(j, i);
                        }
                    }
                    boardAdapter.notifyDataSetChanged();
                    selected.setAlpha(1);

                    // display dialog if winner exists
                    if (board.winner != 0) {
                        int winnerID = playerIDs[board.winner-1];
                        // update winner's points, if both players registered
                        if (playerIDs[0] != SelectPlayerActivity.GUEST_PLAYER_INDEX &&
                            playerIDs[1] != SelectPlayerActivity.GUEST_PLAYER_INDEX) {
                            Uri uri = Uri.parse(PlayersProvider.CONTENT_URI + "/" + winnerID);
                            ContentResolver cr = getContentResolver();
                            ContentValues values = new ContentValues();

                            Cursor cursor = cr.query(uri, null, null, null, null);
                            int pointsIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_POINTS);

                            if (cursor.moveToFirst()) {
                                int points = cursor.getInt(pointsIdx);
                                values.put(PlayersProvider.KEY_POINTS, points + 1);
                                cr.update(uri, values, null, null);
                                cursor.close();
                            }
                        }
                        new AlertDialog.Builder(GameActivity.this)
                                .setTitle("Game Over!")
                                .setMessage("Player " + board.winner + " won! Play again?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        setupGame();
                                        boardAdapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                    }
                }
                isSelecting = !isSelecting;
            }
        });

        // testMoves();
    }

    private void setupGame() {
        isSelecting = true;
        board = new Board(numberOfPieces, capturesToWin); // TODO: add isFiveInARowRule to Board constructor
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                boardAsList[i*Board.BOARD_SIZE + j] = board.get(j, i);
            }
        }
    }

    private void updatePreferences() {
        // get preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Map<String, ?> prefsMap = prefs.getAll();

        isDaiVersion = (Boolean) prefsMap.get(getString(R.string.pref_key_dai_version));
        isFiveInARowRule = (Boolean) prefsMap.get(getString(R.string.pref_key_five_in_a_row_rule));
        capturesToWin = Integer.parseInt( (String) prefsMap.get(getString(R.string.pref_key_captures_to_win)));

        numberOfPieces = isDaiVersion ? DAI_NUMBER_OF_PIECES : DEFAULT_NUMBER_OF_PIECES;
    }

    private void testMoves() {
        Log.d("move V", board.move(0, 1, 0, 2) + ""); // +y
        Log.d("move I", board.move(0, 0, 0, 4) + ""); // friendly piece in the way
        Log.d("move I", board.move(0, 0, 0, 2) + ""); // friendly piece at destination
        Log.d("move V", board.move(0, 2, 8, 2) + ""); // +x
        Log.d("move I", board.move(0, 2, 8, 2) + ""); // empty source position
        Log.d("move V", board.move(8, 7, 8, 6) + ""); // -y
        Log.d("move V", board.move(8, 6, 0, 6) + ""); // -x
        Log.d("move V", board.move(0, 0, 0, 2) + ""); //
        Log.d("move I", board.move(0, 6, 0, 1) + ""); // opponent piece in the way
        Log.d("move I", board.move(0, 6, 0, 2) + ""); // opponent piece at destination
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
            startActivityForResult(intent, SETTINGS_REQUEST);
            return true;
        } else if (id == R.id.action_league_table) {
            Intent intent = new Intent(getApplicationContext(), LeagueTableActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_restart) {
            updatePreferences();
            setupGame();
            boardAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
