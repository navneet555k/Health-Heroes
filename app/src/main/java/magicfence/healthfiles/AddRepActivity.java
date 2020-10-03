package magicfence.healthfiles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddRepActivity extends AppCompatActivity {

    private TextView NameTV,DateTV,fileNameTV;
    private EditText DescET;
    private Button AddButton,SubmitButton;
    private FirebaseAuth mAuth;
    DatabaseReference patientsRef;
    String currentUserID,patient_id,formattedDate;
    Task<Uri> uriTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rep);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        patientsRef = FirebaseDatabase.getInstance().getReference().child("Patients");
        patient_id = getIntent().getStringExtra("uid");
        fileNameTV = (TextView) findViewById(R.id.file_name_tv);


        NameTV = (TextView) findViewById(R.id.add_rep_pname);
        DateTV = (TextView) findViewById(R.id.add_rep_pdate);
        DescET = (EditText) findViewById(R.id.add_rep_desc);
        AddButton = (Button) findViewById(R.id.upload_doc_button);
        SubmitButton = (Button) findViewById(R.id.upload_report_button);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        formattedDate = df.format(c);

        patientsRef.child(patient_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    NameTV.setText(snapshot.child("fullname").getValue().toString());
                    DateTV.setText(formattedDate);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc;
                desc = DescET.getText().toString();

                if (!TextUtils.isEmpty(desc))
                {
                    Intent uploadIntent = new Intent();
                    uploadIntent.setType("application/pdf");
                    uploadIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(uploadIntent,"PDF FILE"),12);
                }
                else
                {
                    Toast.makeText(AddRepActivity.this, "Please provide some description about the document", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==12 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            fileNameTV.setText(data.getDataString()
            .substring(data.getDataString().lastIndexOf("/") + 1));

            SubmitButton.setVisibility(View.VISIBLE);

            SubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progressDialog =  new ProgressDialog(AddRepActivity.this);
                    progressDialog.setTitle("Please wait");
                    progressDialog.setMessage("We are adding the report to the database");
                    progressDialog.show();

                    StorageReference docRef = FirebaseStorage.getInstance().getReference()
                            .child("Reports").child(patient_id).child(formattedDate + ".pdf");

                    docRef.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        }
                    });
                    DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference()
                            .child("Patients").child(patient_id).child("Reports");
                    String desc = DescET.getText().toString();

                    HashMap hashMap = new HashMap();
                    hashMap.put("desc",desc);
                    hashMap.put("report",uriTask.toString());
                    hashMap.put("doctor_id",currentUserID);
                    hashMap.put("date",formattedDate);

                    reportsRef.child(formattedDate).updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(AddRepActivity.this, "Sucess", Toast.LENGTH_SHORT).show();
                                        Intent homeIntent = new Intent(AddRepActivity.this, DashboardActivity.class);
                                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(homeIntent);
                                    }
                                    else
                                    {
                                        progressDialog.hide();
                                        String msg = task.getException().getMessage();
                                        Toast.makeText(AddRepActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            });


        }
    }
}