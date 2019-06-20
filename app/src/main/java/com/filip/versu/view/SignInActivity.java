package com.filip.versu.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.viewmodel.LoginViewModel;
import com.filip.versu.view.viewmodel.callback.ILoginViewModel;

import eu.inloop.viewmodel.base.ViewModelBaseActivity;

public class SignInActivity extends ViewModelBaseActivity<ILoginViewModel.ILoginViewModelCallback, LoginViewModel> implements ILoginViewModel.ILoginViewModelCallback {

    private View loginForm;
    private View mProgressView;

    private Button loginBtn;
    private View goToRegistration;

    private EditText usernameEditText;
    private EditText passwordEditText;

    private TextView errorMsgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        this.loginForm = findViewById(R.id.login_form);

        loginBtn = findViewById(R.id.btn_login);
        this.mProgressView = findViewById(R.id.registration_progress);
        goToRegistration = findViewById(R.id.link_signup);

        this.usernameEditText = findViewById(R.id.input_username);
        this.passwordEditText = findViewById(R.id.input_password);

        this.errorMsgView = findViewById(R.id.errorMsg);

        this.goToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueToRegistration();
            }
        });

        this.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitLoginTask();
            }
        });

        setModelView(this);
        getViewModel().restoreState();
    }

    private void submitLoginTask() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = usernameEditText;
        }


        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = passwordEditText;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            UserDTO userDTO = new UserDTO();
            userDTO.username = username;
            userDTO.password = password;
            getViewModel().attemptLogin(userDTO);
        }

    }


    @Override
    public void showErrorMessage(int resId) {
        errorMsgView.setVisibility(View.VISIBLE);
        errorMsgView.setText(resId);
    }

    @Override
    public void showErrorMessage(String msg) {
        errorMsgView.setVisibility(View.VISIBLE);
        errorMsgView.setText(msg);
    }

    @Override
    public void hideErrors() {
        usernameEditText.setError(null);
        passwordEditText.setError(null);
        errorMsgView.setText("");
        errorMsgView.setVisibility(View.GONE);
    }

    @Override
    public void showProgress(final boolean show) {
        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void continueToApp() {
        Intent intent = new Intent(this, MainViewActivity.class);
        startActivity(intent);
        finish();
    }


    public void continueToRegistration() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public Class<LoginViewModel> getViewModelClass() {
        return LoginViewModel.class;
    }
}
