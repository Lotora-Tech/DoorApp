package com.tech.lotora.doorapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ConnectActivity extends AppCompatActivity {
    // Instantiate the RequestQueue.
    protected RequestQueue requestQueue;
    protected final String URL ="localhost:8080";

    /**
     * Keep track of the connected task to ensure we can cancel it if requested.
     */
    protected ConnectTask mConnectTask = null;

    // UI references.
    protected AutoCompleteTextView mNameView;
    protected EditText mKeyView;
    protected View mProgressView;
    protected View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // Build the queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Set up the login form.
        mNameView = (AutoCompleteTextView) findViewById(R.id.name);


        mKeyView = (EditText) findViewById(R.id.key);
        mKeyView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptConnect();
                    return true;
                }
                return false;
            }
        });

        Button connectButton = (Button) findViewById(R.id.email_sign_in_button);
        connectButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                attemptConnect();
            }
        });

        mLoginFormView = findViewById(R.id.connect_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptConnect() {
        if (mConnectTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mKeyView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String key = mKeyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid name, if the user entered one.
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_invalid_password));
            focusView = mNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(key)) {
            mKeyView.setError(getString(R.string.error_field_required));
            focusView = mKeyView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mConnectTask = new ConnectTask(this, name, key);
            mConnectTask.execute((Void) null);
        }
    }

    public void switchScreen(String mResponse) {
        TextView textArea = findViewById(R.id.text_field);
        textArea.append("\n" + mResponse);
    }
}

