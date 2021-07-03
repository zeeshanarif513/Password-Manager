package com.example.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText username, password;
    private Button login;
    private TextView wrongInput, gotoSignup;
    private List<User> users = new ArrayList<>();
    private MyDatabase firebaseDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseDatabase = new MyDatabase();
        users = firebaseDatabase.getAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        wrongInput = (TextView) findViewById(R.id.wrongInput);
        gotoSignup = (TextView) findViewById(R.id.gotoSignUp);


        login.setOnClickListener(this);
        gotoSignup.setOnClickListener(this);
    }

    public boolean checkEmptyFields(){
        if(username.getText().toString().equals(""))
            return false;
        else if(password.getText().toString().equals(""))
            return false;
        return true;
    }

    public String verifyCredientials(){
        for(int i=0;i<users.size();i++){
            if(users.get(i).username.equals(username.getText().toString())){
                if(users.get(i).password.equals(password.getText().toString())){
                    return users.get(i).id;
                }
            }
        }
        return null;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                if(!checkEmptyFields()){
                    wrongInput.setText("Field(s) are empty");
                    wrongInput.setVisibility(View.VISIBLE);
                }
                else{
                    String id = verifyCredientials();
                    if(id != null){
                        Toast.makeText(getApplicationContext(),"successful",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
                        intent.putExtra(getResources().getString(R.string.user_id), id);
                        startActivity(intent);
                    }
                    else{
                        wrongInput.setText("Wrong username/password");
                        wrongInput.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.gotoSignUp:
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                break;
        }
    }
}