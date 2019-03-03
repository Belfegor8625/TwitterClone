/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.ac_twitterclone.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bartoszlewandowski.ac_twitterclone.R;
import com.bartoszlewandowski.ac_twitterclone.login.LogInActivity;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.edtSignUpEmail)
    EditText edtSignUpEmail;
    @BindView(R.id.edtSignUpPassword)
    EditText edtSignUpPassword;
    @BindView(R.id.edtSignUpUsername)
    EditText edtSignUpUsername;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        registerOnEnterListenerInPassword();

    }

    private void registerOnEnterListenerInPassword() {
        edtSignUpPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClickBtnSignUp();
                }
                return false;
            }
        });
    }

    @OnClick(R.id.btnSignUp)
    public void onClickBtnSignUp() {
        if (edtSignUpEmail.getText().toString().equals("") ||
                edtSignUpUsername.getText().toString().equals("") ||
                edtSignUpPassword.getText().toString().equals("")) {
            FancyToast.makeText(SignUpActivity.this, "Email, Username and Password is required!",
                    Toast.LENGTH_LONG, FancyToast.INFO, false).show();
        } else {
            registerNewUser();
        }
    }

    private void registerNewUser() {
        final ParseUser newUser = new ParseUser();
        setNewUserData(newUser);
        setUpProgressDialog("Signing up " + newUser.getUsername());
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (validateSignUp(e, newUser)) {
                    startTwitterUsersActivity();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void setNewUserData(ParseUser newUser) {
        newUser.setUsername(edtSignUpUsername.getText().toString());
        newUser.setPassword(edtSignUpPassword.getText().toString());
        newUser.setEmail(edtSignUpEmail.getText().toString());
    }

    private void setUpProgressDialog(String message) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private boolean validateSignUp(ParseException e, ParseUser newUser) {
        if (e == null) {
            FancyToast.makeText(SignUpActivity.this, newUser.getUsername() + " is signed up",
                    Toast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
            return false;
        } else {
            FancyToast.makeText(SignUpActivity.this, e.getMessage(),
                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return true;
        }
    }

    private void startTwitterUsersActivity() {
        // TODO:
    }


    @OnClick(R.id.btnGoToLogIn)
    public void onClickBtnGoToLogIn() {
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(intent);
    }
}
