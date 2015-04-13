package com.hanshenrik.gronsleth_hasamishogi;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class RegisterNewPlayerActivity extends ActionBarActivity {
    private EditText usernameInput, descriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_player);

        this.usernameInput = (EditText) findViewById(R.id.username_input);
        this.descriptionInput = (EditText) findViewById(R.id.description_input);
        Button registerButton = (Button) findViewById(R.id.register_button);

        usernameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // close keyboard, as most players won't enter description
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("REGISTER BUTTON", "clicked!");
                String username = usernameInput.getText().toString();
                String description = descriptionInput.getText().toString();

                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();

                // TODO: check if username available

                values.put(PlayersProvider.KEY_PLAYER, username);
                values.put(PlayersProvider.KEY_POINTS, 0);
                values.put(PlayersProvider.KEY_DESCRIPTION, description);
                // TODO: add avatar somehow. Address to file on device? (must ask for permission like in practical)
                cr.insert(PlayersProvider.CONTENT_URI, values);

                Intent result = new Intent();
                setResult(RESULT_OK, result);
                finish();
            }
        });
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
