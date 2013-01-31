package com.example.ulitmatetodolist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appacitive.android.callbacks.AppacitiveSignUpCallback;
import com.appacitive.android.model.AppacitiveError;
import com.appacitive.android.model.AppacitiveUser;
import com.appacitive.android.model.AppacitiveUserDetail;

public class SignUpActivity extends Activity {

	private EditText mUserNameView;
	private EditText mEmailView;
	private EditText mFirstNameView;
	private EditText mPasswordView;
	private View mSignupView;
	private View mSignupStatusView;
	private TextView mSignUpStatusMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initSubview();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sign_up, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent upIntent = new Intent(this, LoginActivity.class);
			NavUtils.navigateUpTo(this, upIntent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initSubview() {
		mUserNameView = (EditText) findViewById(R.id.signup_username);
		mEmailView = (EditText) findViewById(R.id.signup_email);
		mFirstNameView = (EditText) findViewById(R.id.signup_firstname);
		mPasswordView = (EditText) findViewById(R.id.signup_password);
		mSignupView = findViewById(R.id.signup_form);
		mSignupStatusView = findViewById(R.id.signup_status);
		mSignUpStatusMessage = (TextView) findViewById(R.id.signup_status_message);
	}
	
	public void onSignUpButtonClick(View v) {
		String userName = mUserNameView.getText().toString().trim();
		String email = mEmailView.getText().toString().trim();
		String firstName = mFirstNameView.getText().toString().trim();
		String password = mPasswordView.getText().toString().trim();
		signUp(userName, email , firstName, password);
	}
	
	private void signUp(String userName, String email, String firstName, String password){ 
		AppacitiveUserDetail userDetail = new AppacitiveUserDetail();
		userDetail.setUserName(userName);
		userDetail.setEmail(email);
		userDetail.setFirstName(firstName);
		userDetail.setPassword(password);
		mSignUpStatusMessage.setText("Signing Up");
		showProgress(true);
		
		AppacitiveUser.createUser(userDetail, new AppacitiveSignUpCallback() {
			
			@Override
			public void onSuccess(AppacitiveUser user) {
				Intent upIntent = new Intent(SignUpActivity.this, LoginActivity.class);
				NavUtils.navigateUpTo(SignUpActivity.this, upIntent);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(getBaseContext(), "Sign up successfull", Toast.LENGTH_SHORT).show();
						showProgress(false);
					}
				});
			}
			
			@Override
			public void onFailure(final AppacitiveError error) {
				Log.w("TAG", "Error while creating the user " + error.toString());
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						showProgress(false);
						if(error.getStatusCode().equals("600")) {
							mUserNameView.setError("User already exist");
						}
					}
				});
			}
		});
		
	}

	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mSignupStatusView.setVisibility(View.VISIBLE);
			mSignupStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSignupStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mSignupView.setVisibility(View.VISIBLE);
			mSignupView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSignupStatusView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			mSignupStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mSignupView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
