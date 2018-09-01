package peri.periit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
    Button btfeedback,btUpload;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION_WRITE = 22;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final int GALLERY_INTENT = 2;
    StorageReference mStorageRef;
    String name;

    private static final String EARNINGS_API = "http://api.msg91.com/api/sendhttp.php?country=91&sender=PERIIT&route=4&mobiles=";
    private static final String ATTACH_API = "&authkey=235086AuBUHp6g5b8a8abc&message=";

    String phone,message,rollno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_display);

        btfeedback = findViewById(R.id.btfeedback);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        btUpload = findViewById(R.id.btUpload);
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                if (checkPermissionREAD_EXTERNAL_STORAGE(StudentDisplay.this) && checkPermissionLOCATION(StudentDisplay.this) && checkPermissionWRITE(StudentDisplay.this) ) {
                    startActivityForResult(intent, GALLERY_INTENT);
                }
            }
        });

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

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External Storage",context, android.Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        if (requestcode == GALLERY_INTENT && resultcode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageview = findViewById(R.id.imageview);
                imageview.setImageBitmap(bitmap);

                FirebaseStorage storage;
                StorageReference storageReference;

                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();

                final StorageReference ref = storageReference.child("images/"+ uid);
                ref.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(StudentDisplay.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUrl = uri;
                                        String download = String.valueOf(downloadUrl);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        final DatabaseReference databaseReference = database.getReference();
                                        databaseReference.child("student/"+uid+"/image").setValue(downloadUrl);
                                        databaseReference.child(dept+"/"+year+"/"+section+"/student/"+uid+"/image").setValue(download);
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(StudentDisplay.this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                Toast.makeText(StudentDisplay.this, "Uploaded "+String.valueOf((int)progress+"%"), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            Log.i("URI",uri.toString());


        }
    }

    public boolean checkPermissionLOCATION(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showDialogg("External Storage",context, android.Manifest.permission.ACCESS_COARSE_LOCATION);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialogg(final String msg, final Context context,
                            final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }







    public boolean checkPermissionWRITE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialoggg("External Storage",context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_LOCATION_WRITE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialoggg(final String msg, final Context context,
                            final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_LOCATION_WRITE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

}
