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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static peri.periit.MainActivity.CATPREF;

public class StudentView extends AppCompatActivity {


    String studentuid;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button btlogout,btnotify,btstatus;
    String backlogs,subjects,status,fees,name,rollno;

    TextView tvname,tvrollno,tvfees,tvstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view);

        btnotify = findViewById(R.id.btnotify);

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
                                    }
                                });
                                btnotify.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(StudentView.this, "Notification API called. Student asked to pay.", Toast.LENGTH_LONG).show();
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
                                    }
                                });
                                btnotify.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(StudentView.this, "Notification API called. Student notified of payment.", Toast.LENGTH_LONG).show();
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
                                    }
                                });
                                btnotify.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(StudentView.this, "Notification API called. Student notified of pending status.", Toast.LENGTH_LONG).show();
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
}
