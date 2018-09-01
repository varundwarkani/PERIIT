package peri.periit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class StudentDisplay extends AppCompatActivity {

    TextView tvsubjects,tvbacklogs,tvfees,tvstatus;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;
    String subjects,backlogs,fees,status;
    Button btlogout,btpaid;
    String dept,year,section;
    private RequestQueue requestQueue;
    ProgressBar studentdisplayprogress;
    Button btfeedback;

    private static final String EARNINGS_API = "http://api.msg91.com/api/sendhttp.php?country=91&sender=PERIIT&route=4&mobiles=";
    private static final String ATTACH_API = "&authkey=235086AuBUHp6g5b8a8abc&message=";

    String phone,message,rollno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_display);

        btfeedback = findViewById(R.id.btfeedback);

        studentdisplayprogress = findViewById(R.id.studentdisplayprogress);
        studentdisplayprogress.setVisibility(View.VISIBLE);
        tvstatus = findViewById(R.id.tvstatus);
        tvsubjects = findViewById(R.id.tvsubjects);
        tvbacklogs = findViewById(R.id.tvbacklogs);
        tvfees = findViewById(R.id.tvfees);

        btlogout = findViewById(R.id.btlogout);
        btpaid = findViewById(R.id.btpaid);

        btlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(StudentDisplay.this)
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                Intent intent = new Intent (StudentDisplay.this, MainActivity.class);
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
                    uid = user.getUid();
                    DatabaseReference mRefregggg = FirebaseDatabase.getInstance().getReference();
                    mRefregggg.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            subjects = dataSnapshot.child("student/"+uid+"/exams").getValue().toString();
                            backlogs = dataSnapshot.child("student/"+uid+"/backlog").getValue().toString();
                            fees = dataSnapshot.child("fees").getValue().toString();
                            status = dataSnapshot.child("student/"+uid+"/status").getValue().toString();

                            dept = dataSnapshot.child("student/"+uid+"/dept").getValue().toString();
                            year = dataSnapshot.child("student/"+uid+"/year").getValue().toString();
                            section = dataSnapshot.child("student/"+uid+"/section").getValue().toString();

                            phone = dataSnapshot.child("phoneno/"+dept+"/"+year+"/"+section+"/phoneno").getValue().toString();

                            rollno = dataSnapshot.child("student/"+uid+"/rollno").getValue().toString();

                            btpaid.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AlertDialog.Builder(StudentDisplay.this)
                                            .setMessage("Are you sure you have paid?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //set pay to 2
                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                    final DatabaseReference databaseReference = database.getReference();
                                                    databaseReference.child("student/"+uid+"/status").setValue("2");
                                                    databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/status").setValue("2");

                                                    message = rollno+"%20has%20made%20payment.%20Please%20check%20and%20update.";
                                                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                                                    sendsms();
                                                    //send sms here
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                }
                            });

                            btfeedback.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AlertDialog.Builder(StudentDisplay.this)
                                            .setMessage("Are you sure you want to ask your teacher to check details?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //set pay to 2
                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                    final DatabaseReference databaseReference = database.getReference();
                                                    databaseReference.child("student/"+uid+"/status").setValue("2");
                                                    databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/status").setValue("2");

                                                    message = rollno+"%20has%20requested%20to%20check%20their%20details.%20Kindly%20verify%20it.";
                                                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                                                    sendsms();
                                                    //send sms here
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                }
                            });


                            tvsubjects.setText("Subjects: "+subjects);
                            tvbacklogs.setText("Backlogs: "+backlogs);
                            if (status.equals("0")){
                                tvstatus.setText("Not Paid");
                                tvstatus.setTextColor(Color.parseColor("#FF0000"));
                            }else if (status.equals("1")){
                                tvstatus.setText("Paid");
                                tvstatus.setTextColor(Color.parseColor("#32CD32"));
                            }else if (status.equals("2")){
                                tvstatus.setText("Pending");
                                tvstatus.setTextColor(Color.parseColor("#FF8000"));
                            }

                            double s = Double.parseDouble(subjects);
                            double b = Double.parseDouble(backlogs);
                            double fe = Double.parseDouble(fees);
                            double totalfees = (s+b)*fe;

                            String tot = String.valueOf(totalfees);
                            tvfees.setText("Total Fees: "+ tot);

                            studentdisplayprogress.setVisibility(View.GONE);
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
        String url = EARNINGS_API + phone + ATTACH_API + message;
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
                Toast.makeText(StudentDisplay.this, "Message sent.", Toast.LENGTH_SHORT).show();
                //          Toast.makeText(MainActivity.this, "Some error has occured!", Toast.LENGTH_SHORT).show();
            }
        });
        return jsonObjectRequest;
    }

    private void parsedetails(JSONObject jsonObject) {
    }
}
