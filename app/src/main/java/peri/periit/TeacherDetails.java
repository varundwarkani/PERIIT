package peri.periit;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherDetails extends AppCompatActivity {

    RecyclerView rvteacher;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    ArrayList<String> rollno = new ArrayList<>();
    ArrayList<String> uid = new ArrayList<>();
    ArrayList<String> status = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    TeacherAdapter teacherAdapter;
    Button btlogout;
    String dept,year,section,teacheruid;
    ProgressBar teacherdetailsprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_details);
        rvteacher = findViewById(R.id.rvteacher);
        rvteacher.setHasFixedSize(true);
        rvteacher.setLayoutManager(new LinearLayoutManager(TeacherDetails.this));
        teacherAdapter = new TeacherAdapter(rollno,uid,status,name);
        rvteacher.setAdapter(teacherAdapter);
        teacherdetailsprogress = findViewById(R.id.teacherdisplayprogress);
        teacherdetailsprogress.setVisibility(View.VISIBLE);


        btlogout = findViewById(R.id.btlogout);
        btlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TeacherDetails.this)
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                Intent intent = new Intent(TeacherDetails.this, MainActivity.class);
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
                if (user != null) {

                    //set adapter
                    teacheruid = user.getUid();
                    DatabaseReference mRefregggg = FirebaseDatabase.getInstance().getReference();
                    mRefregggg.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dept = dataSnapshot.child("teacher/"+teacheruid+"/dept").getValue().toString();
                            year = dataSnapshot.child("teacher/"+teacheruid+"/year").getValue().toString();
                            section = dataSnapshot.child("teacher/"+teacheruid+"/section").getValue().toString();

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(dept+"/"+year+"/"+section+"/student");
                            usersRef.orderByChild("rollno").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    rollno.clear();
                                    uid.clear();
                                    name.clear();
                                    status.clear();
                                    teacherAdapter = new TeacherAdapter(rollno,uid,status,name);
                                    rvteacher.setAdapter(teacherAdapter);
                                    for (final DataSnapshot postSnapshots: dataSnapshot.getChildren())
                                    {

                                        String rollno1 = postSnapshots.child("rollno").getValue().toString();
                                        String name1 = postSnapshots.child("name").getValue().toString();
                                        String uid1 = postSnapshots.child("uid").getValue().toString();
                                        String status1 = postSnapshots.child("status").getValue().toString();
                                        rollno.add(rollno1);
                                        uid.add(uid1);
                                        status.add(status1);
                                        name.add(name1);
                                        teacherAdapter = new TeacherAdapter(rollno,uid,status,name);
                                        rvteacher.setAdapter(teacherAdapter);
                                    }

                                    teacherdetailsprogress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

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
}
