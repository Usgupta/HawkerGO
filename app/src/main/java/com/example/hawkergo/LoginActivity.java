package com.example.hawkergo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hawkergo.services.firebase.interfaces.DbEventHandler;
import com.example.hawkergo.services.firebase.repositories.AuthService;
import com.example.hawkergo.utils.Constants;
import com.example.hawkergo.utils.textValidator.TextValidatorHelper;
import com.example.hawkergo.utils.ui.DebouncedOnClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etLoginEmail;
    TextInputEditText etLoginPassword;
    TextView tvRegisterHere;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        setOnClickListeners();
    }

    private void initViews(){
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPass);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setOnClickListeners(){
        btnLogin.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View view) {
                loginUser();
            }
        });

        tvRegisterHere.setOnClickListener(view ->{
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private boolean validateEmail(){
        String email = etLoginEmail.getText().toString();
        boolean isValid = !TextValidatorHelper.isNullOrEmpty(email);
        if (!isValid){
            etLoginEmail.setError("Email cannot be empty");
            etLoginEmail.requestFocus();
        }
        return isValid;
    }
    private boolean validatePassword(){
        String password = etLoginPassword.getText().toString();
        boolean isValid = !TextValidatorHelper.isNullOrEmpty(password);
        if (!isValid){
            etLoginPassword.setError("Password cannot be empty");
            etLoginPassword.requestFocus();
        }
        return isValid;
    }

    private void loginUser(){
        Boolean[] validations = new Boolean[]{
                validateEmail(),
                validatePassword()
        };

        if(!Arrays.asList(validations).contains(false)){
            String email = etLoginEmail.getText().toString();
            String password = etLoginPassword.getText().toString();
            AuthService.loginUser(
                    email,
                    password,
                    new DbEventHandler<String>() {
                        @Override
                        public void onSuccess(String o) {
                            Toast.makeText(LoginActivity.this, "MAKAN LO!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HawkerCentreActivity.class));
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(LoginActivity.this, "Log in Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

        }
    }

}

