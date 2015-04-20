package com.hanshenrik.gronsleth_hasamishogi;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;


public class RegisterNewPlayerActivity extends ActionBarActivity {
    private EditText usernameInput, descriptionInput, imageURLInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_player);

        this.usernameInput = (EditText) findViewById(R.id.username_input);
        this.descriptionInput = (EditText) findViewById(R.id.description_input);
        this.imageURLInput = (EditText) findViewById(R.id.image_url_input);
        Button registerButton = (Button) findViewById(R.id.register_button);

        usernameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                descriptionInput.requestFocus();
                return true;
            }
        });

        descriptionInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                imageURLInput.requestFocus();
                return true;
            }
        });

        imageURLInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                new DownloadImageTask((ImageView) findViewById(R.id.image)).execute(v.getText().toString());
                return false;
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String imageUrl = imageURLInput.getText().toString();

                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();

                // TODO: check if username available

                values.put(PlayersProvider.KEY_PLAYER, username);
                values.put(PlayersProvider.KEY_POINTS, 0);
                values.put(PlayersProvider.KEY_DESCRIPTION, description);
                values.put(PlayersProvider.KEY_IMAGE_URL, imageUrl);
                cr.insert(PlayersProvider.CONTENT_URI, values);

                Intent result = new Intent();
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_new_player, menu);
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
