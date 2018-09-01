package peri.periit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    Button btsignup;
    TextView etsignuppass,etsignupmail;
    String mail,pass,uid;
    FirebaseAuth mAuth;
    String name,dept,year,rollno,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etsignuppass = findViewById(R.id.etsignuppass);
        etsignupmail = findViewById(R.id.etsignupmail);
        btsignup = findViewById(R.id.btsignup);
        mAuth = FirebaseAuth.getInstance();

        btsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable())
                {
                    hideKeyboard();
                    mail = etsignupmail.getText().toString();
                    pass = etsignuppass.getText().toString();

                    if (!TextUtils.isEmpty(mail)&&!TextUtils.isEmpty(pass))
                    {
                        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    uid = user.getUid();

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                                    View v = null;
                                    v = LayoutInflater.from(Signup.this).inflate(R.layout.custom_registration,null,false);
                                    builder.setView(v);
                                    final EditText etCustomRegistrationNumber,etCustomRegistrationName,etCustomRegistrationrollno;
                                    final EditText etCustomRegistrationdept,etCustomRegistrationyear;
                                    Button btCustomRegistrationLogin;
                                    etCustomRegistrationName = v.findViewById(R.id.etCustomRegistrationName);
                                    etCustomRegistrationNumber = v.findViewById(R.id.etCustomRegistrationNumber);
                                    etCustomRegistrationrollno = v.findViewById(R.id.etCustomRegistrationrollno);
                                    etCustomRegistrationdept = v.findViewById(R.id.etCustomRegistrationdept);
                                    etCustomRegistrationyear = v.findViewById(R.id.etCustomRegistrationyear);

                                    btCustomRegistrationLogin = v.findViewById(R.id.btCustomRegistrationLogin);
                                    builder.setCancelable(false);
                                    btCustomRegistrationLogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            
                                            //name,dept,year,phoneno,rollno
                                            name = etCustomRegistrationName.getText().toString();
                                            dept = etCustomRegistrationdept.getText().toString();
                                            year = etCustomRegistrationyear.getText().toString();
                                            phone = etCustomRegistrationNumber.getText().toString();
                                            rollno = etCustomRegistrationrollno.getText().toString();
                                            
                                            if (name.equals("") || phone.equals("") || dept.equals("") || year.equals("") || phone.equals(""))
                                            {
                                                Toast.makeText(Signup.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                setdetails();
                                            }
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    //ask name, rollno, dept,year
                                }
                                else
                                {
                                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                    Toast.makeText(Signup.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(Signup.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Signup.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setdetails()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference();
        databaseReference.child("student/"+uid+"/mail").setValue(mail);
        databaseReference.child("student/"+uid+"/uid").setValue(uid);
        databaseReference.child("student/"+uid+"/exams").setValue("6");
        databaseReference.child("student/"+uid+"/backlog").setValue("0");

        mAuth.signOut();
        Toast.makeText(this, "Details set! logged out!", Toast.LENGTH_SHORT).show();
   //     Intent intent = new Intent (Signup.this, Login.class);
     //   startActivity(intent);
       // finish();
        
        
    }
}
