package com.example.shelved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;

    private Button Login;
    private Button Regisration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
//                    Intent intent = new Intent(MainActivity.this,TasksActivity.class);
//                    startActivity(intent);

                } else {
                    // User is signed out

                }

            }
        };

        ETemail = (EditText) findViewById(R.id.username);
        ETpassword = (EditText) findViewById(R.id.password);

        Login = (Button) findViewById(R.id.login);
        Regisration = (Button) findViewById(R.id.register);

        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            Intent intent = new Intent(MainActivity.this, TasksActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login)
        {
            signin(ETemail.getText().toString(),ETpassword.getText().toString());
        }else if (view.getId() == R.id.register)
        {
            registration(ETemail.getText().toString(),ETpassword.getText().toString());
        }

    }

    public void signin(String email , String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "С возвращением", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (MainActivity.this, TasksActivity.class);
                    startActivity(intent);
                }else
                    Toast.makeText(MainActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void registration (String email , String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show();
            }
        });
    }
}