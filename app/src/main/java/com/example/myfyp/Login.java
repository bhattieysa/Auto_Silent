package com.example.myfyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity implements View.OnClickListener {

    //variables
    TextView signup, login;
    TextInputEditText email, password;
    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutPassword;
    String emailString, passwordString;
    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    ConstraintLayout constraintLayout;
    ProgressDialog progressDialog;
    UserInfo userInfo;
    UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        userSession = new UserSession(this);
        userInfo = new UserInfo(Login.this);


        //check if user is alrady logged in or not
        if (userSession.isUserLoggedin()) {

            startActivity(new Intent(Login.this,FirstActivity.class));
        }


        initViews();
        initListeners();
        initObjects();
    }


    // This method is to initialize views

    private void initViews() {

        email = findViewById(R.id.email1);
        password = findViewById(R.id.password1);
        signup = findViewById(R.id.textView2);
        login = findViewById(R.id.appCompatButtonLogin);
        textInputLayoutEmail = findViewById(R.id.emailedt);
        textInputLayoutPassword = findViewById(R.id.passwordedt);
        constraintLayout = findViewById(R.id.nestedScrollView);
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Loading...Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

    }


    // This method is to initialize listeners

    private void initListeners() {
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
    }



    // This method is to initialize objects to be used

    private void initObjects() {
        databaseHelper = new DatabaseHelper(Login.this);
        inputValidation = new InputValidation(Login.this);
    }


    //This implemented method is to listen the click on views
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.appCompatButtonLogin:
                verifyFromSQLite();
                break;
            case R.id.textView2:
                // Navigate to SignupActivity
                startActivity(new Intent(getApplicationContext(), Signup.class));
                break;
        }
    }


    // This method is to validate the input text fields and verify login credentials from SQLite

    private void verifyFromSQLite() {
        progressDialog.show();
        if (!inputValidation.isInputEditTextFilled(email, textInputLayoutEmail, "Enter Valid Email")) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(email, textInputLayoutEmail, "Enter Valid Email")) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(password, textInputLayoutPassword, "Enter Valid Email")) {
            return;
        }
        if (databaseHelper.checkUser(email.getText().toString().trim()
                , password.getText().toString().trim())) {
            progressDialog.dismiss();
            userSession.setLoggedin(true);
            UserInfo info = new UserInfo(getApplicationContext());
            //setting user email and password in Shared PREFERENCES for sesion
            info.setEmail(email.getText().toString().trim());
            info.setPass(password.getText().toString().trim());

            //moving to next activity when succesfully logged inn
            Intent accountsIntent = new Intent(Login.this, FirstActivity.class);
            accountsIntent.putExtra("EMAIL", email.getText().toString().trim());
            emptyInputEditText();
            startActivity(accountsIntent);
        } else {
            // Snack Bar to show success message that record is wrong
            Snackbar.make(constraintLayout, "Wrong Email or Password", Snackbar.LENGTH_LONG).show();
        }
    }


    // This method is to empty all input edit text
    private void emptyInputEditText() {
        email.setText(null);
        password.setText(null);
    }
}