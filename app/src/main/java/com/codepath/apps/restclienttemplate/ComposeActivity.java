package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    //instance variables
    TwitterClient client;
    private EditText etTweet;
    private Button postTweet;
    private TextView tweetCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApplication.getRestClient(this);
        etTweet = (EditText) findViewById(R.id.etTweet);
        postTweet= findViewById(R.id.tweetButton);

        postTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = etTweet.getText().toString();
                sendTweet(t);
            }
        });

        //adding tweet counter
        tweetCount = (TextView) findViewById(R.id.tvCount);

        //displays the text count
        final TextWatcher txwatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tweetCount.setText(String.valueOf(s.length()) + "/280" );
            }

            public void afterTextChanged(Editable s) {
            }
        };

        etTweet.addTextChangedListener(txwatcher);

    }

    private void sendTweet(String message) {
        client.sendTweet(message, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    //parsing response
                    JSONObject  responseJson = new JSONObject(new String(responseBody));
                    Tweet resultTweet = Tweet.fromJSON(responseJson);

                    //return result to calling activity
                    Intent resultData = new Intent();
                    resultData.putExtra("tweet",resultTweet);
                    setResult(RESULT_OK, resultData);

                    finish();
                } catch (JSONException e) {
                    Log.e("ComposeActivity", "Error parsing response", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("ComposeActivity", "compose activity", error);
            }
        });
    }


}