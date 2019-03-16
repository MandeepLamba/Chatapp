package com.exploredigi.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText name,email,pass;
    Button register;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ragister);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        name = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.pass);
        register = (Button) findViewById(R.id.register_button);
        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_string = name.getText().toString().trim();
                String email_string = email.getText().toString();
                String pass_string = pass.getText().toString();
                if (TextUtils.isEmpty(name_string)||TextUtils.isEmpty(email_string)||TextUtils.isEmpty(pass_string)){
                    Toast.makeText(RegisterActivity.this, "You have to fill all field", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(pass_string.length() < 6){
                        Toast.makeText(RegisterActivity.this, "Password length must > 6", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        register(name_string,email_string,pass_string);
                    }
                }
            }
        });
    }

    private void register(final String name, String email, String pass){
        auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            String user_id = user.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);

                            HashMap<String ,String> hashMap = new HashMap<>();
                            hashMap.put("id",user_id);
                            hashMap.put("username",name);
                            hashMap.put("imageURL","default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        }
                        else{

                            Toast.makeText(RegisterActivity.this, "You can't register with this email and password"+task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
