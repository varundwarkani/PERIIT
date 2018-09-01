package peri.periit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentDisplay extends AppCompatActivity {

    TextView tvsubjects,tvbacklogs,tvfees,tvstatus;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;
    String subjects,backlogs,fees,status;

    Button btlogout,btpaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_display);

        tvstatus = findViewById(R.id.tvstatus);
        tvsubjects = findViewById(R.id.tvsubjects);
        tvbacklogs = findViewById(R.id.tvbacklogs);
        tvfees = findViewById(R.id.tvfees);

        btlogout = findViewById(R.id.btlogout);
        btpaid = findViewById(R.id.btpaid);

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
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

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
