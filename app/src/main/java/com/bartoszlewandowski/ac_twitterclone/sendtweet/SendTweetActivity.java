/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.ac_twitterclone.sendtweet;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.bartoszlewandowski.ac_twitterclone.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bartoszlewandowski.ac_twitterclone.consts.DatabaseConsts.*;

public class SendTweetActivity extends AppCompatActivity {

    @BindView(R.id.edtTweet)
    EditText edtTweet;
    private ProgressDialog progressDialog;

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
}
