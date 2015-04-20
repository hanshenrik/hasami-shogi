package com.hanshenrik.gronsleth_hasamishogi;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;


public class LeagueTableActivity extends ActionBarActivity {
    private ArrayList<Player> players;
    private TextView descriptionTextView;
    private ImageView avatarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_table);

        players = new ArrayList<>();
        ListView leagueTableListView = (ListView) findViewById(R.id.league_table_list);
        descriptionTextView = (TextView) findViewById(R.id.description_view);
        avatarView = (ImageView) findViewById(R.id.avatar_view);

        fetchPlayerInfo();

        ArrayAdapter leagueTableAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, players);
        leagueTableListView.setAdapter(leagueTableAdapter);

        leagueTableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = players.get(position).description;
                String avatarURL = players.get(position).avatarURL;
                descriptionTextView.setText(description);
                new DownloadImageTask(avatarView).execute(avatarURL);
            }
        });
    }

    private void fetchPlayerInfo() {
        String orderBy = PlayersProvider.KEY_POINTS + " DESC";
        Cursor cursor = getContentResolver().query(PlayersProvider.CONTENT_URI, null, null, null, orderBy);
        int idIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_ID);
        int nameIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_PLAYER);
        int descriptionIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_DESCRIPTION);
        int pointsIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_POINTS);
        int avatarURLIdx = cursor.getColumnIndexOrThrow(PlayersProvider.KEY_IMAGE_URL);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIdx);
                String name = cursor.getString(nameIdx);
                String description = cursor.getString(descriptionIdx);
                int points = cursor.getInt(pointsIdx);
                String avatarURL = cursor.getString(avatarURLIdx);
                players.add(new Player(id, name, description, points, avatarURL));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
