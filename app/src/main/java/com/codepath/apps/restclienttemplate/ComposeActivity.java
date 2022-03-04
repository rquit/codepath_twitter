package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    TextView tvTweetLength;
    Button btnTweet;

    TwitterClient client;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvTweetLength = findViewById(R.id.tvTweetLength);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String tweetLength = String.format("%d/%d", etCompose.length(), MAX_TWEET_LENGTH);
                tvTweetLength.setText(tweetLength);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnTweet.setOnClickListener(view -> {
            String tweetContent = etCompose.getText().toString();
            if(tweetContent.isEmpty()) {
                Toast.makeText(ComposeActivity.this, "tweet cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(tweetContent.length() > MAX_TWEET_LENGTH) {
                Toast.makeText(ComposeActivity.this, "tweet cannot be over 140 characters!", Toast.LENGTH_SHORT).show();
                return;
            }
            client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "tweet successfully published");
                    try {
                        Tweet tweet = Tweet.fromJson(json.jsonObject);
                        Log.i(TAG, "tweet says:" + tweet.body);
                        Intent intent = new Intent();
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "failure to publish tweet", throwable);
                }
            });
        });
    }
}