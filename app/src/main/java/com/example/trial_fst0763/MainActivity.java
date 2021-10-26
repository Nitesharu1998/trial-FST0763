package com.example.trial_fst0763;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fst_t0763.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    TextInputEditText phone,pass;
    Button loginButton;
    TextView register;
    CheckBox remember;
    String strphone,strpass;
    public static final String PREFERENCE= "preference";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone = findViewById(R.id.textPhone);
        pass = findViewById(R.id.textPassword);
        loginButton = findViewById(R.id.process_Login);
        register = findViewById(R.id.toRegister);
        remember=findViewById(R.id.rememberchkbox);
        DBHelper helper=new DBHelper(this);

        SharedPreferences sharedPreferences =getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        String shared= sharedPreferences.getString("phone",null);
        if (shared!=null){

            startActivity(new Intent(MainActivity.this,homePage.class));
            finish();

        }




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String sample=phone.getText().toString();
                if (checkphone()| checkpass()) {

                    Log.i("logindata", strphone + "  " + strpass);
                    Boolean checkuser = helper.checkuser(strphone, strpass);
                    if (checkuser) {
                        Intent intent = new Intent(MainActivity.this, homePage.class);
                        intent.putExtra("phone", strphone);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "No user found, Please Register", Toast.LENGTH_SHORT).show();
                    }

                    if (remember.isChecked()) {
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                        SharedPreferences.Editor ed = sharedPreferences.edit();
                        ed.putString("phone", strphone);
                        ed.apply();
                        ed.commit();

                        Intent intent = new Intent(MainActivity.this, homePage.class);
                        intent.putExtra("phone", strphone);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(MainActivity.this, homePage.class);
                        intent.putExtra("phone", strphone);
                        startActivity(intent);

                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Please enter proper credentials", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }

    public void toreg(View view) {
        Intent intent=new Intent(MainActivity.this,registerUser.class);
        startActivity(intent);

    }


    public boolean checkphone() {
        strphone=phone.getText().toString();

        Pattern pattern = Pattern.compile("^[+]?[0-9]{10,13}$");
        Matcher match = pattern.matcher(strphone);

        if (match.find() && match.group().equals(strphone)&& strphone!=null) {
            phone.setError(null);
            return true;
        } else
            return false;
    }

    public boolean checkpass() {
        strpass = pass.getText().toString();
        if (strpass.length() >= 8 && strpass!=null) {
            pass.setError(null);
            return true;
        } else {
            pass.setError("password is too short");
            return false;
        }
    }

}
