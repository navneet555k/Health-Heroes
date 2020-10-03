package magicfence.healthfiles;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MyQRActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String currentUserID;
    private ImageView qrView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_q_r);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        qrView = (ImageView) findViewById(R.id.myqr_imview);

        QRGEncoder qrgEncoder = new QRGEncoder(currentUserID, null, QRGContents.Type.TEXT, 200);
        bitmap = qrgEncoder.getBitmap();

        qrView.setImageBitmap(bitmap);

    }
}