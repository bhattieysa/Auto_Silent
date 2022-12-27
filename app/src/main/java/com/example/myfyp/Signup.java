package com.example.myfyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Signup extends AppCompatActivity implements View.OnClickListener {


    //VARIABLES
    private final AppCompatActivity activity = Signup.this;
    ConstraintLayout constraintLayout;
    TextInputLayout layoutName,layoutEmail,layoutPassword,layoutConfirmPassword;
    TextInputEditText nameet,emailet,passet,conpasset;
    TextView register,login;
    InputValidation inputValidation;
    DatabaseHelper databaseHelper;
    UserModel user;
    ProgressDialog progressDialog;
    UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        userSession = new UserSession(Signup.this);

       // METHODS
        initViews();
        initListeners();
        initObjects();

    }


    // This method is to initialize views

    private void initViews() {
        constraintLayout = findViewById(R.id.nestedScrollView);
        layoutName = findViewById(R.id.nameedt);
        layoutEmail = findViewById(R.id.emailedt);
        layoutPassword = findViewById(R.id.passwordedt);
        layoutConfirmPassword = findViewById(R.id.conpasswordedt);
        nameet = findViewById(R.id.name1);
        emailet = findViewById(R.id.email1);
        passet = findViewById(R.id.password1);
        conpasset = findViewById(R.id.conpassword1);
        register = findViewById(R.id.appCompatButtonLogin);
        login = findViewById(R.id.textView2);
        progressDialog = new ProgressDialog(Signup.this);
        progressDialog.setMessage("Loading...Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    // This method is to initialize listeners

    private void initListeners() {
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    // This method is to initialize objects to be used

    private void initObjects() {
        inputValidation = new InputValidation(activity);
        databaseHelper = new DatabaseHelper(activity);
        user = new UserModel();
    }

    // This implemented method is to listen the click on view

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                postDataToSQLite();
                break;
            case R.id.textView2:
                startActivity(new Intent(Signup.this, Login.class));
                finish();
                break;
        }
    }

    // This method is to validate the input text fields and post data to SQLite

    private void postDataToSQLite() {
        progressDialog.show();
        if (!inputValidation.isInputEditTextFilled(nameet, layoutName, "Enter Full Name")) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(emailet, layoutEmail, "Enter Valid Email")) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(emailet, layoutEmail, "Enter Valid Email")) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(passet, layoutPassword, "Enter Password")) {
            return;
        }
        if (!inputValidation.isInputEditTextMatches(passet, conpasset,
                layoutConfirmPassword, "Password Does Not Matches")) {
            return;
        }
        if (!databaseHelper.checkUser(emailet.getText().toString().trim())) {
            user.setName(nameet.getText().toString().trim());
            user.setEmail(emailet.getText().toString().trim());
            user.setPassword(passet.getText().toString().trim());
            databaseHelper.addUser(user);
            // Snack Bar to show success message that record saved successfully
            progressDialog.dismiss();
            Snackbar.make(constraintLayout, "Registration Successful", Snackbar.LENGTH_LONG).show();
            emptyInputEditText();
            UserInfo info = new UserInfo(getApplicationContext());
            userSession.setLoggedin(true);
            info.setEmail(emailet.getText().toString().trim());
            info.setUsername(nameet.getText().toString().trim());
            info.setPass(passet.getText().toString().trim());
            Intent accountsIntent = new Intent(Signup.this, FirstActivity.class);
            startActivity(accountsIntent);
        } else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(constraintLayout, "Email Already Exists", Snackbar.LENGTH_LONG).show();
        }
    }

    // This method is to empty all input edit text

    private void emptyInputEditText() {
        nameet.setText(null);
        emailet.setText(null);
        passet.setText(null);
        conpasset.setText(null);
    }
}