package com.example.feedbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {

    EditText emailEdt,passwordEdt;
    Button login;
    TextView nothaveExistAccount,recoverPass;

    private FirebaseAuth mAuth;

    ProgressDialog progressDi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        emailEdt=(EditText)findViewById(R.id.emailEdittxt);
        passwordEdt=(EditText)findViewById(R.id.passEdittxt);
        nothaveExistAccount=(TextView)findViewById(R.id.nothaveExistAccount);
        recoverPass=findViewById(R.id.recoverPass);
        login=(Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailEdt.getText().toString();
                String password=passwordEdt.getText().toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEdt.setError("Incorrect Email");
                    emailEdt.setFocusable(true);
                }
                else{
                    userLogin(email,password);
                }

            }
        });

        nothaveExistAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this,Register.class));
                finish();
            }
        });
        recoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassDialog();
            }
        });
        progressDi=new ProgressDialog(this);

    }

    private void recoverPassDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(this);

        final EditText emailEdt=new EditText(this);
        emailEdt.setHint("Email");
        emailEdt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEdt.setMinEms(16);

        linearLayout.addView(emailEdt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email=emailEdt.getText().toString().trim();
                startRecovery(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void startRecovery(String email) {
        progressDi.setMessage("Sending mail..");
        progressDi.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDi.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(LogIn.this,"Email sent",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LogIn.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDi.dismiss();

                Toast.makeText(LogIn.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void userLogin(String email,String password){
        progressDi.setMessage("Logging..");
        progressDi.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            progressDi.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LogIn.this,Profile.class));
                            finish();
                        } else {
                            progressDi.dismiss();
                            Toast.makeText(LogIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDi.dismiss();
                Toast.makeText(LogIn.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
