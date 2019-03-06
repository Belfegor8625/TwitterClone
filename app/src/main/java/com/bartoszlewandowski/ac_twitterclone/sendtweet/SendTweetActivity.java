/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.ac_twitterclone.sendtweet;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bartoszlewandowski.ac_twitterclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bartoszlewandowski.ac_twitterclone.consts.DatabaseConsts.*;

public class SendTweetActivity extends AppCompatActivity {

    @BindView(R.id.edtTweet)
    EditText edtTweet;
    @BindView(R.id.otherUsersTweetsListView)
    ListView otherUsersTweetsListView;
    private ProgressDialog progressDialog;
    private static final String TWEET_USERNAME = "tweetUserName";
    private static final String TWEET_VALUE = "tweetValue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_tweet);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnSendTweet)
    public void onClickBtnSendTweet() {
        ParseObject myTweet = new ParseObject(CLASS_MY_TWEET);
        myTweet.put(TWEET, edtTweet.getText().toString());
        myTweet.put(USER, ParseUser.getCurrentUser().getUsername());
        setUpProgressDialog("Loading");
        myTweet.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                validateSavingTweet(e);
                progressDialog.dismiss();
            }
        });
    }

    private void setUpProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void validateSavingTweet(ParseException e) {
        if (e == null) {
            FancyToast.makeText(this, ParseUser.getCurrentUser().getUsername() + "'s tweet (" +
                    edtTweet.getText().toString() + ") is saved", Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
        } else {
            FancyToast.makeText(this, e.getMessage(),
                    Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    @OnClick(R.id.btnOtherUsersTweets)
    public void onCLickBtnOtherUsersTweets() {
        final ArrayList<HashMap<String, String>> tweetList = new ArrayList<>();
        final SimpleAdapter adapter = new SimpleAdapter(SendTweetActivity.this,
                tweetList, android.R.layout.simple_list_item_2,
                new String[]{TWEET_USERNAME, TWEET_VALUE},
                new int[]{android.R.id.text1, android.R.id.text2});

        createDataForTweetsListView(tweetList, adapter);
    }

    private void createDataForTweetsListView(final ArrayList<HashMap<String, String>> tweetList, final SimpleAdapter adapter) {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(CLASS_MY_TWEET);
        parseQuery.whereContainedIn(USER, ParseUser.getCurrentUser().getList(FAN_OF));
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0 && e == null) {
                    for (ParseObject tweetObject : objects) {
                        HashMap<String, String> userTweet = new HashMap<>();
                        userTweet.put(TWEET_USERNAME, tweetObject.getString(USER));
                        userTweet.put(TWEET_VALUE, tweetObject.getString(TWEET));
                        tweetList.add(userTweet);
                    }
                    otherUsersTweetsListView.setAdapter(adapter);
                }
            }
        });
    }
}
