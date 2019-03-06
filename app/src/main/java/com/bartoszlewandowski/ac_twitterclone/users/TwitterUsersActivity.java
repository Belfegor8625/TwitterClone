/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.ac_twitterclone.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.bartoszlewandowski.ac_twitterclone.R;
import com.bartoszlewandowski.ac_twitterclone.consts.DatabaseConsts;
import com.bartoszlewandowski.ac_twitterclone.sendtweet.SendTweetActivity;
import com.bartoszlewandowski.ac_twitterclone.signup.SignUpActivity;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bartoszlewandowski.ac_twitterclone.consts.DatabaseConsts.FAN_OF;

public class TwitterUsersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.listView)
    ListView listView;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> usersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);
        ButterKnife.bind(this);
        fillListView();
    }

    private void fillListView() {
        usersArray = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, usersArray);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(this);
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo(DatabaseConsts.USERNAME, ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    setListViewData(users);
                }
            }
        });
    }

    private void setListViewData(List<ParseUser> users) {
        if (users.size() > 0) {
            for (ParseUser user : users) {
                usersArray.add(user.getUsername());
            }
            listView.setAdapter(arrayAdapter);
            for (String twitterUser : usersArray) {
                if (ParseUser.getCurrentUser().getList(FAN_OF) != null) {
                    if (ParseUser.getCurrentUser().getList(FAN_OF).contains(twitterUser)) {
                        listView.setItemChecked(usersArray.indexOf(twitterUser), true);
                    }
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutUserItem) {
            logOutCurrentUser();
        }
        if (item.getItemId() == R.id.sendTweetItem) {
            startSendTweetActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutCurrentUser() {
        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                startSignUpActivity();
                finish();
            }
        });
    }

    private void startSendTweetActivity() {
        Intent intent = new Intent(TwitterUsersActivity.this, SendTweetActivity.class);
        startActivity(intent);
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(TwitterUsersActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView checkedTextView = (CheckedTextView) view;
        if (checkedTextView.isChecked()) {
            FancyToast.makeText(TwitterUsersActivity.this, usersArray.get(position) + " is now followed",
                    Toast.LENGTH_SHORT, FancyToast.INFO, false).show();
            ParseUser.getCurrentUser().add(FAN_OF, usersArray.get(position));
        } else {
            FancyToast.makeText(TwitterUsersActivity.this, usersArray.get(position) + " is not followed anymore",
                    Toast.LENGTH_SHORT, FancyToast.INFO, false).show();
            removeCurrentUserFanOf(position);
        }
        saveCurrentUsersData();
    }

    private void removeCurrentUserFanOf(int position) {
        Objects.requireNonNull(ParseUser.getCurrentUser().getList(FAN_OF)).remove(usersArray.get(position));
        List currentUserFanOfList = ParseUser.getCurrentUser().getList(FAN_OF);
        ParseUser.getCurrentUser().remove(FAN_OF);
        ParseUser.getCurrentUser().put(FAN_OF, currentUserFanOfList);
    }

    private void saveCurrentUsersData() {
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                validateSave(e);
            }
        });
    }

    private void validateSave(ParseException e) {
        if (e == null) {
            FancyToast.makeText(TwitterUsersActivity.this, "Saved",
                    Toast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
        } else {
            FancyToast.makeText(TwitterUsersActivity.this, e.getMessage(),
                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
        }
    }
}
