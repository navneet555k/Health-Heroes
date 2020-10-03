package magicfence.healthfiles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText EmailET, PassET;
    private Button LoginButton;
    private TextView forgotLink;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        EmailET = (EditText) findViewById(R.id.login_email_et);
        PassET = (EditText) findViewById(R.id.login_pass_et);
        LoginButton = (Button) findViewById(R.id.login_pg_btn);
        forgotLink = (TextView) findViewById(R.id.forgot_pass_link);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailET.getText().toString();
                String password = PassET.getText().toString();

                if(!(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)))
                {
                    progressDialog.setTitle("Please wait");
                    progressDialog.setMessage("We are logging you in");
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        Intent dashIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                                        dashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(dashIntent);
                                    }
                                    else
                                    {
                                        progressDialog.hide();
                                        String msg = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, "Error occured. " + msg, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Please fill all the credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailET.getText().toString();
                if (!TextUtils.isEmpty(email))
                {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(LoginActivity.this, "Password reset link sent successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String msg = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, "Error. "+msg, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });

    }
}