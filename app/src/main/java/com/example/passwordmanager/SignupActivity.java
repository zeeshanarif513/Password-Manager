package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText name, username, password;
    private Button signup;
    private TextView wrongInput, gotoLogin;
    List<User> users = new ArrayList<>();
    MyDatabase firebaseDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseDatabase = new MyDatabase();
        users = firebaseDatabase.getAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = (EditText) findViewById(R.id.name);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.signup);
        wrongInput = (TextView) findViewById(R.id.wrongInput);
        gotoLogin = (TextView) findViewById(R.id.gotoLogin);

        signup.setOnClickListener(this);
        gotoLogin.setOnClickListener(this);

    }

    public boolean checkEmptyFields(){
        if(name.getText().toString().equals(""))
            return false;
        else if(username.getText().toString().equals(""))
            return false;
        else if(password.getText().toString().equals(""))
            return false;
        return true;
    }
    public boolean checkUsername(){
        for(int i=0;i<users.size();i++){
            if(users.get(i).username.equals(username.getText().toString()))
                return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup:
                if(!checkEmptyFields()){
                    wrongInput.setText("Field(s) are empty");
                    wrongInput.setVisibility(View.VISIBLE);
                }
                else if(!checkUsername()){
                    wrongInput.setText("Username already exists");
                    wrongInput.setVisibility(View.VISIBLE);
                }
                else{
                    firebaseDatabase.addUser(new User(name.getText().toString(), username.getText().toString(), password.getText().toString()));
                    Toast.makeText(getApplicationContext(),"successful",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.gotoLogin:
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}