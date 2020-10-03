package magicfence.healthfiles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class AddPrescActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String patient_id;
    String currentUserID;
    String urll;
    String dr_name;
    DatabaseReference patientRef;
    TextView PatientNameTV,DateTV;
    Bitmap bitmap;
    private EditText PrescriptionsET;
    private Button SignPrescButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_presc);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        patient_id = getIntent().getStringExtra("uid");
        progressDialog = new ProgressDialog(this);
        patientRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(patient_id);
        SignPrescButton = (Button) findViewById(R.id.sign_presc_button);
        PrescriptionsET = (EditText) findViewById(R.id.add_prec_prescription);


        DateTV = (TextView) findViewById(R.id.add_presc_pdate);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        final String formattedDate = df.format(c);

        PatientNameTV = (TextView) findViewById(R.id.add_presc_pname);
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String name = snapshot.child("fullname").getValue().toString();
                    PatientNameTV.setText(name);
                    DateTV.setText(formattedDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference docRef = FirebaseDatabase.getInstance().getReference().child("Doctors").child(currentUserID);
        docRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    dr_name = snapshot.child("fullname").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Toast.makeText(this, "Patient ID: " + patient_id, Toast.LENGTH_SHORT).show();

        SignPrescButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String prescriptions  = PrescriptionsET.getText().toString();

                if (!TextUtils.isEmpty(prescriptions))
                {
                    progressDialog.setTitle("Please wait");
                    progressDialog.setMessage("We are signing the prescription and adding it to the database");
                    progressDialog.show();
                    final DatabaseReference prescRef = FirebaseDatabase.getInstance().getReference()
                            .child("Patients").child(patient_id).child("Prescriptions");

                    QRGEncoder qrgEncoder = new QRGEncoder(currentUserID, null, QRGContents.Type.TEXT, 100);
                    bitmap = qrgEncoder.getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final StorageReference qrStorage = FirebaseStorage.getInstance().getReference().child("Doctors");
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                    byte[] arr = baos.toByteArray();
                    qrStorage.child("QR").child(currentUserID).putBytes(arr);
                   /* urll = qrStorage.child("QR").child(currentUserID).getDownloadUrl().getResult().toString();
                            *//*addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urll = uri.get;
                                }
                            });*/
                    HashMap hashMap = new HashMap();
                    hashMap.put("prescriptions",prescriptions);
                    hashMap.put("dr_name",dr_name);
                    hashMap.put("doctor_id",currentUserID);

                    prescRef.child(formattedDate).updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        Intent homeIntent = new Intent(AddPrescActivity.this, DashboardActivity.class);
                                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(homeIntent);
                                    }
                                    else
                                    {
                                        progressDialog.hide();
                                        String msg = task.getException().getMessage();
                                        Toast.makeText(AddPrescActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }

            }
        });

    }
}