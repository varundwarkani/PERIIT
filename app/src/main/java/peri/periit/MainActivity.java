package peri.periit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    RelativeLayout rlstudent,rlteacher,rlperi;
    Button btStudent,btTeacher;
    Button btsignup,btlogin;
    TextView etsignuppass,etsignupmail;
    String mail,pass,uid,type;
    FirebaseAuth mAuth;
    String name,dept,year,rollno,phone;
    public static final String CATPREF = "catpref";
    SharedPreferences categoriesPref;
    SharedPreferences.Editor editor;
    String email,password,section;
    TextView etsignuppasst,etsignupmailt;
    Button btsignupt,btlogint;
    ProgressBar mainprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mainprogress = findViewById(R.id.mainprogress);
        mainprogress.setVisibility(View.GONE);

        rlstudent = findViewById(R.id.rlstudent);
        rlteacher = findViewById(R.id.rlteacher);
        rlperi = findViewById(R.id.rlperi);
        btStudent = findViewById(R.id.btStudent);
        btTeacher = findViewById(R.id.btTeacher);

        etsignuppasst = findViewById(R.id.etsignuppasst);
        etsignupmailt = findViewById(R.id.etsignupmailt);
        btsignupt = findViewById(R.id.btsignupt);
        btlogint = findViewById(R.id.btlogint);


        btlogint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                email = etsignupmailt.getText().toString();
                password = etsignuppasst.getText().toString();

                if (!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password))
                {
                    mainprogress.setVisibility(View.VISIBLE);
                    Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                uid = user.getUid();
                                categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                                editor = categoriesPref.edit();
                                editor.putString("uid",uid);
                                editor.putString("type","teacher");
                                editor.commit();

                                mainprogress.setVisibility(View.GONE);
                                Intent intent = new Intent (MainActivity.this, TeacherDetails.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                mainprogress.setVisibility(View.GONE);
                                FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                Toast.makeText(MainActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else
                {
                    mainprogress.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlperi.setVisibility(View.GONE);
                rlteacher.setVisibility(View.GONE);
                rlstudent.setVisibility(View.VISIBLE);
            }
        });

        btlogin = findViewById(R.id.btlogin);
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainprogress.setVisibility(View.VISIBLE);
                hideKeyboard();
                mail = etsignupmail.getText().toString();
                pass = etsignuppass.getText().toString();

                if (!TextUtils.isEmpty(mail)&&!TextUtils.isEmpty(pass))
                {
                    Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                uid = user.getUid();
                                categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                                editor = categoriesPref.edit();
                                editor.putString("uid",uid);
                                editor.putString("type","student");
                                editor.commit();

                                mainprogress.setVisibility(View.GONE);
                                Intent intent = new Intent (MainActivity.this, StudentDisplay.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                mainprogress.setVisibility(View.GONE);
                                FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                Toast.makeText(MainActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else
                {
                    mainprogress.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlperi.setVisibility(View.GONE);
                rlteacher.setVisibility(View.VISIBLE);
                rlstudent.setVisibility(View.GONE);
            }
        });

        etsignuppass = findViewById(R.id.etsignuppass);
        etsignupmail = findViewById(R.id.etsignupmail);
        btsignup = findViewById(R.id.btsignup);
        mAuth = FirebaseAuth.getInstance();

        btsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable())
                {
                    mainprogress.setVisibility(View.VISIBLE);
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

                                    categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                                    editor = categoriesPref.edit();
                                    editor.putString("uid",uid);
                                    editor.putString("type","student");
                                    editor.commit();

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    View v = null;
                                    v = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_registration,null,false);
                                    builder.setView(v);
                                    final EditText etCustomRegistrationNumber,etCustomRegistrationName,etCustomRegistrationrollno;
                                    Button btCustomRegistrationLogin;
                                    etCustomRegistrationName = v.findViewById(R.id.etCustomRegistrationName);
                                    etCustomRegistrationNumber = v.findViewById(R.id.etCustomRegistrationNumber);
                                    etCustomRegistrationrollno = v.findViewById(R.id.etCustomRegistrationrollno);

                                    final Spinner etCustomRegistrationdept,etCustomRegistrationsection,etCustomRegistrationyear;
                                    etCustomRegistrationdept = v.findViewById(R.id.etCustomRegistrationdept);
                                    etCustomRegistrationsection = v.findViewById(R.id.etCustomRegistrationsection);
                                    etCustomRegistrationyear = v.findViewById(R.id.etCustomRegistrationyear);

                                    btCustomRegistrationLogin = v.findViewById(R.id.btCustomRegistrationLogin);
                                    builder.setCancelable(false);
                                    btCustomRegistrationLogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            //name,dept,year,phoneno,rollno
                                            name = etCustomRegistrationName.getText().toString();
                                            dept = etCustomRegistrationdept.getSelectedItem().toString();
                                            year = etCustomRegistrationyear.getSelectedItem().toString();
                                            section = etCustomRegistrationsection.getSelectedItem().toString();
                                            phone = etCustomRegistrationNumber.getText().toString();
                                            rollno = etCustomRegistrationrollno.getText().toString();

                                            int r = etCustomRegistrationrollno.getText().length();
                                            int p = etCustomRegistrationNumber.getText().length();
                                            if (p<10)
                                            {
                                                Toast.makeText(MainActivity.this, "Please enter 10 digit number", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                if (r<12)
                                                {
                                                    Toast.makeText(MainActivity.this, "Please enter 12 digit roll no.", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    if (name.equals("") || phone.equals("") || dept.equals("") || year.equals("") || phone.equals(""))
                                                    {
                                                        mainprogress.setVisibility(View.GONE);
                                                        Toast.makeText(MainActivity.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        setdetails();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    //ask name, rollno, dept,year
                                }
                                else
                                {
                                    mainprogress.setVisibility(View.GONE);
                                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                    Toast.makeText(MainActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                    else
                    {
                        mainprogress.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    mainprogress.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });





        btsignupt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable())
                {
                    mainprogress.setVisibility(View.VISIBLE);
                    hideKeyboard();
                    email = etsignupmailt.getText().toString();
                    password = etsignuppasst.getText().toString();

                    if (!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password))
                    {
                        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    uid = user.getUid();

                                    categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                                    editor = categoriesPref.edit();
                                    editor.putString("uid",uid);
                                    editor.putString("type","teacher");
                                    editor.commit();

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    View v = null;
                                    v = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_registration_teacher,null,false);
                                    builder.setView(v);
                                    final EditText etCustomRegistrationNumber,etCustomRegistrationName,etCustomRegistrationrollno;

                                    final Spinner etCustomRegistrationdept,etCustomRegistrationsection,etCustomRegistrationyear;
                                    etCustomRegistrationdept = v.findViewById(R.id.etCustomRegistrationdept);
                                    etCustomRegistrationsection = v.findViewById(R.id.etCustomRegistrationsection);
                                    etCustomRegistrationyear = v.findViewById(R.id.etCustomRegistrationyear);

                                    Button btCustomRegistrationLogin;
                                    etCustomRegistrationName = v.findViewById(R.id.etCustomRegistrationName);
                                    etCustomRegistrationNumber = v.findViewById(R.id.etCustomRegistrationNumber);
                                    etCustomRegistrationrollno = v.findViewById(R.id.etCustomRegistrationrollno);

                                    btCustomRegistrationLogin = v.findViewById(R.id.btCustomRegistrationLogin);
                                    builder.setCancelable(false);
                                    btCustomRegistrationLogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            //name,dept,year,phoneno,rollno
                                            dept = etCustomRegistrationdept.getSelectedItem().toString();
                                            year = etCustomRegistrationyear.getSelectedItem().toString();
                                            section = etCustomRegistrationsection.getSelectedItem().toString();
                                            name = etCustomRegistrationName.getText().toString();
                                            phone = etCustomRegistrationNumber.getText().toString();
                                            rollno = etCustomRegistrationrollno.getText().toString();

                                            int r = etCustomRegistrationrollno.getText().length();
                                            int p = etCustomRegistrationNumber.getText().length();
                                            if (p<10)
                                            {
                                                Toast.makeText(MainActivity.this, "Please enter 10 digit number", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                if (r<12)
                                                {
                                                    Toast.makeText(MainActivity.this, "Please enter 12 digit ID no.", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    if (name.equals("") || phone.equals("") || dept.equals("") || phone.equals("")) {
                                                        mainprogress.setVisibility(View.GONE);
                                                        Toast.makeText(MainActivity.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        setdetailss();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    //ask name, rollno, dept,year
                                }
                                else
                                {
                                    mainprogress.setVisibility(View.GONE);
                                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                    Toast.makeText(MainActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                    else
                    {
                        mainprogress.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    mainprogress.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
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
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/mail").setValue(mail);
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/uid").setValue(uid);
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/exams").setValue("6");
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/backlog").setValue("0");
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/status").setValue("0");
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/year").setValue(year);
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/dept").setValue(dept);
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/rollno").setValue(rollno);
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/name").setValue(name);
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/phoneno").setValue(phone);
        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/section").setValue(section);


        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/image").setValue("https://firebasestorage.googleapis.com/v0/b/periit-efa70.appspot.com/o/fees.jpg?alt=media&token=133e92ab-af84-4e54-b3e0-9882982697c6");
        databaseReference.child("student/"+uid+"/image").setValue("https://firebasestorage.googleapis.com/v0/b/periit-efa70.appspot.com/o/fees.jpg?alt=media&token=133e92ab-af84-4e54-b3e0-9882982697c6");

        databaseReference.child("student/"+uid+"/mail").setValue(mail);
        databaseReference.child("student/"+uid+"/uid").setValue(uid);
        databaseReference.child("student/"+uid+"/exams").setValue("6");
        databaseReference.child("student/"+uid+"/backlog").setValue("0");
        databaseReference.child("student/"+uid+"/status").setValue("0");
        databaseReference.child("student/"+uid+"/year").setValue(year);
        databaseReference.child("student/"+uid+"/dept").setValue(dept);
        databaseReference.child("student/"+uid+"/rollno").setValue(rollno);
        databaseReference.child("student/"+uid+"/name").setValue(name);
        databaseReference.child("student/"+uid+"/phoneno").setValue(phone);
        databaseReference.child("student/"+uid+"/year").setValue(year);
        databaseReference.child("student/"+uid+"/section").setValue(section);

  //      mAuth.signOut();
    //    Toast.makeText(this, "Details set! logged out!", Toast.LENGTH_SHORT).show();

        categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
        editor = categoriesPref.edit();
        editor.putString("uid",uid);
        editor.putString("type","student");
        editor.commit();

        mainprogress.setVisibility(View.GONE);
        Intent intent = new Intent (MainActivity.this, StudentDisplay.class);
        startActivity(intent);
        finish();
    }


    public void setdetailss()
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference();
        databaseReference.child(dept+"/"+year+"/"+section+"/teacher/"+uid+"/mail").setValue(email);
        databaseReference.child(dept+"/"+year+"/"+section+"/teacher/"+uid+"/uid").setValue(uid);
        databaseReference.child(dept+"/"+year+"/"+section+"/teacher/"+uid+"/phoneno").setValue(phone);
        databaseReference.child(dept+"/"+year+"/"+section+"/teacher/"+uid+"/idno").setValue(rollno);
        databaseReference.child(dept+"/"+year+"/"+section+"/teacher/"+uid+"/name").setValue(name);
        databaseReference.child(dept+"/"+year+"/"+section+"/teacher/"+uid+"/dept").setValue(dept);
        databaseReference.child(dept+"/"+year+"/"+section+"/teacher/"+uid+"/section").setValue(section);
        databaseReference.child("phoneno/"+dept+"/"+year+"/"+section+"/phoneno").setValue(phone);

        databaseReference.child("teacher/"+uid+"/mail").setValue(email);
        databaseReference.child("teacher/"+uid+"/uid").setValue(uid);
        databaseReference.child("teacher/"+uid+"/phoneno").setValue(phone);
        databaseReference.child("teacher/"+uid+"/idno").setValue(rollno);
        databaseReference.child("teacher/"+uid+"/name").setValue(name);
        databaseReference.child("teacher/"+uid+"/dept").setValue(dept);
        databaseReference.child("teacher/"+uid+"/year").setValue(year);
        databaseReference.child("teacher/"+uid+"/section").setValue(section);

        //      mAuth.signOut();
        //    Toast.makeText(this, "Details set! logged out!", Toast.LENGTH_SHORT).show();

        categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
        editor = categoriesPref.edit();
        editor.putString("uid",uid);
        editor.putString("type","teacher");
        editor.commit();

        mainprogress.setVisibility(View.GONE);
        Intent intent = new Intent (MainActivity.this, TeacherDetails.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveTaskToBack(true);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}