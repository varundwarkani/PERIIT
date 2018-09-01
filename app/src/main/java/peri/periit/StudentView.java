package peri.periit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import static peri.periit.MainActivity.CATPREF;

public class StudentView extends AppCompatActivity {


    String studentuid;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button btlogout,btnotify,btstatus;
    String backlogs,subjects,status,fees,name,rollno;
    Button btchange;
    String exams,backlog;

    String dept,year,section;

    AlertDialog.Builder builder;
    AlertDialog dialog;

    String message,phoneno;

    TextView tvname,tvrollno,tvfees,tvstatus;
    private RequestQueue requestQueue;

    private static final String EARNINGS_API = "http://api.msg91.com/api/sendhttp.php?country=91&sender=PERIIT&route=4&mobiles=";
    private static final String ATTACH_API = "&authkey=235086AuBUHp6g5b8a8abc&message=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view);

        btnotify = findViewById(R.id.btnotify);

        btchange = findViewById(R.id.btchange);
        btchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StudentView.this, "Please wait loading data...", Toast.LENGTH_SHORT).show();
            }
        });

        tvname = findViewById(R.id.tvname);
        tvrollno = findViewById(R.id.tvrollno);
        tvfees = findViewById(R.id.tvfees);
        tvstatus = findViewById(R.id.tvstatus);

        btlogout = findViewById(R.id.btlogout);
        btstatus = findViewById(R.id.btstatus);
        btlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(StudentView.this)
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                Intent intent = new Intent (StudentView.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!= null) {
                    SharedPreferences catPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                    studentuid = catPref.getString("studentuid","none");

                    DatabaseReference mRefregggg = FirebaseDatabase.getInstance().getReference();
                    mRefregggg.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            subjects = dataSnapshot.child("student/"+studentuid+"/exams").getValue().toString();
                            name = dataSnapshot.child("student/"+studentuid+"/name").getValue().toString();
                            rollno = dataSnapshot.child("student/"+studentuid+"/rollno").getValue().toString();
                            phoneno = dataSnapshot.child("student/"+studentuid+"/phoneno").getValue().toString();

                            dept = dataSnapshot.child("student/"+studentuid+"/status").getValue().toString();
                            year = dataSnapshot.child("student/"+studentuid+"/status").getValue().toString();
                            section = dataSnapshot.child("student/"+studentuid+"/status").getValue().toString();


                            tvname.setText(name);
                            tvrollno.setText(rollno);
                            backlogs = dataSnapshot.child("student/"+studentuid+"/backlog").getValue().toString();
                            fees = dataSnapshot.child("fees").getValue().toString();
                            status = dataSnapshot.child("student/"+studentuid+"/status").getValue().toString();
                            if (status.equals("0")){
                                btstatus.setText("Pending?");
                                btstatus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        final DatabaseReference databaseReference = database.getReference();
                                        databaseReference.child("student/"+studentuid+"/status").setValue("2");
                                        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+studentuid+"/status").setValue("2");
                                        message = "Your%20payment%20status%20has%20been%20changed%20to%20pending%20state";
                                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        sendsms();
                                    }
                                });
                                btnotify.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        message = "Please%20make%20the%20exam%20fees%20payment";
                                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        sendsms();
                                    }
                                });
                                tvstatus.setText("Not Paid");
                                tvstatus.setTextColor(Color.parseColor("#FF0000"));
                            }else if (status.equals("1")){
                                btstatus.setText("Reject?");
                                btstatus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        final DatabaseReference databaseReference = database.getReference();
                                        databaseReference.child("student/"+studentuid+"/status").setValue("0");
                                        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+studentuid+"/status").setValue("0");

                                        message = "Your%20payment%20status%20has%20been%20changed%20to%20rejected";
                                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        sendsms();
                                    }
                                });
                                btnotify.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        message = "Please%20make%20the%20exam%20fees%20payment";
                                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        sendsms();
                                    }
                                });
                                tvstatus.setText("Paid");
                                tvstatus.setTextColor(Color.parseColor("#32CD32"));
                            }else if (status.equals("2")){
                                btstatus.setText("Approve?");
                                btstatus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        final DatabaseReference databaseReference = database.getReference();
                                        databaseReference.child("student/"+studentuid+"/status").setValue("1");
                                        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+studentuid+"/status").setValue("1");
                                        message = "Your%20payment%20status%20has%20been%20approved";
                                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        sendsms();
                                    }
                                });
                                btnotify.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        message = "Please%20make%20the%20exam%20fees%20payment";
                                        requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        sendsms();
                                    }
                                });
                                tvstatus.setText("Pending");
                                tvstatus.setTextColor(Color.parseColor("#FF8000"));
                            }

                            double s = Double.parseDouble(subjects);
                            double b = Double.parseDouble(backlogs);
                            double fe = Double.parseDouble(fees);
                            double totalfees = (s+b)*fe;

                            String tot = String.valueOf(totalfees);
                            tvfees.setText("Total Fees: "+ totalfees);



                            btchange.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //alert builder here. ask details and set to studentuid.
                                    builder = new AlertDialog.Builder(StudentView.this);
                                    View v1 = null;
                                    v1 = LayoutInflater.from(StudentView.this).inflate(R.layout.custom_details,null,false);
                                    builder.setView(v1);
                                    final EditText etCustombacklog,etCustomexams;
                                    Button btCustomDetails;
                                    etCustomexams = v1.findViewById(R.id.etCustomexams);
                                    etCustombacklog = v1.findViewById(R.id.etCustombacklog);

                                    etCustomexams.setText(subjects);
                                    etCustombacklog.setText(backlogs);

                                    btCustomDetails = v1.findViewById(R.id.btCustomDetails);
                                    builder.setCancelable(false);
                                    btCustomDetails.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            exams = etCustomexams.getText().toString();
                                            backlog = etCustombacklog.getText().toString();

                                            if (exams.equals("") || backlog.equals("") )
                                            {
                                                Toast.makeText(StudentView.this, "Enter both the details", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                //set here
                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                final DatabaseReference databaseReference = database.getReference();
                                                databaseReference.child("student/"+studentuid+"/exams").setValue(exams);
                                                databaseReference.child("student/"+studentuid+"/backlog").setValue(backlog);
                                                databaseReference.child("student/"+studentuid+"/status").setValue("0");

                                                databaseReference.child(dept+"/"+year+"/"+section+"/student/"+studentuid+"/backlog").setValue(backlog);
                                                databaseReference.child(dept+"/"+year+"/"+section+"/student/"+studentuid+"/exams").setValue(exams);
                                                databaseReference.child(dept+"/"+year+"/"+section+"/student/"+studentuid+"/status").setValue("0");

                                                Toast.makeText(StudentView.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                                                message = "Your%20fees%20amount%20has%20been%20changed%20.%20Please%20check%20and%20pay";
                                                requestQueue = Volley.newRequestQueue(getApplicationContext());
                                                sendsms();
                                                dialog.cancel();
                                            }
                                        }
                                    });
                                    dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }


    private void sendsms() {
        //Adding the method to the queue by calling the method getDataFromServer
        requestQueue.add(getData(0));
    }

    private JsonObjectRequest getData(int requestCount) {
        //Initializing ProgressBar

        Log.i("REUQEST_COUNT_VAL",String.valueOf(requestCount));
        String url = EARNINGS_API + phoneno + ATTACH_API + message;
        Log.i("MYURL",url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,
                new com.android.volley.Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("JSON_RES",response.toString());
                        parsedetails(response);
                    }
                }, new com.android.volley.Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentView.this, "Message sent.", Toast.LENGTH_SHORT).show();
                //          Toast.makeText(MainActivity.this, "Some error has occured!", Toast.LENGTH_SHORT).show();
            }
        });
        return jsonObjectRequest;
    }

    private void parsedetails(JSONObject jsonObject) {
    }
}
