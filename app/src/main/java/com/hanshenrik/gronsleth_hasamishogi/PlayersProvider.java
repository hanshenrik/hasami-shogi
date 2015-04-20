package com.hanshenrik.gronsleth_hasamishogi;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class PlayersProvider extends ContentProvider {
    public static final String AUTHORITY = "com.hanshenrik.gronsleth_hasamishogi";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/players");

    private SQLiteDatabase playersDB;
    private static final String PLAYERS_TABLE = "players";
    public static final String KEY_ID = "_id";
    public static final String KEY_PLAYER = "player";
    public static final String KEY_POINTS = "points";
    public static final String KEY_DESCRIPTION = "description";
    public static final int PLAYER_COLUMN = 1;
    public static final int POINTS_COLUMN = 2;
    public static final int DESCRIPTION_COLUMN = 3;

    private static final int PLAYERS = 1;
    private static final int PLAYER_ID = 2;
    private static final UriMatcher uriMatcher;

    private static final String DATABASE_NAME = "players.db";
    private static final int DATABASE_VERSION = 1;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "players", PLAYERS);
        uriMatcher.addURI(AUTHORITY, "players/#", PLAYER_ID);
    }

    private String makeNewWhere(String where, Uri uri, int matchResult) {
        if (matchResult != PLAYER_ID) {
            return where;
        } else {
            String newWhereSoFar = KEY_ID + "=" + uri.getPathSegments().get(1);
            if (TextUtils.isEmpty(where)) {
                return newWhereSoFar;
            } else {
                return newWhereSoFar + " AND (" + where + ')';
            }
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int matchResult = uriMatcher.match(uri);
        String newWhere = makeNewWhere(where, uri, matchResult);

        if (matchResult == PLAYER_ID || matchResult == PLAYERS) {
            int count = playersDB.delete(PLAYERS_TABLE, newWhere, whereArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PLAYERS:
                return "vnd.android.cursor.dir/vnd.hanshenrik.player";
            case PLAYER_ID:
                return "vnd.android.cursor.item/vnd.hanshenrik.player";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = playersDB.insert(PLAYERS_TABLE, KEY_PLAYER, values);
        if (rowID > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(newUri, null);
            return uri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        PlayersDatabaseHelper helper = new PlayersDatabaseHelper(
                this.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        this.playersDB = helper.getWritableDatabase();
//        helper.onUpgrade(playersDB, 1, 2);
        return (playersDB != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PLAYERS_TABLE);

        if (uriMatcher.match(uri) == PLAYER_ID) {
            qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
        }

        Cursor c = qb.query(playersDB, projection, selection, selectionArgs, null, null, sort);

        // register to watch for changes which are signalled by notifyChange elsewhere
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        int matchResult = uriMatcher.match(uri);
        String newWhere = makeNewWhere(where, uri, matchResult);

        if (matchResult == PLAYER_ID || matchResult == PLAYERS) {
            int count = playersDB.update(PLAYERS_TABLE, values, newWhere, whereArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private static class PlayersDatabaseHelper extends SQLiteOpenHelper {
        public PlayersDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + PLAYERS_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_PLAYER + " TEXT," +
                    KEY_POINTS + " INTEGER," + // TODO: not tested that INTEGER works
                    KEY_DESCRIPTION + " TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + PLAYERS_TABLE);
            onCreate(db);
        }
    }
}
