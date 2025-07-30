package com.kalilotx.smsviewer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    private static final int PERMISSAO_SMS = 123;
    TextView smsOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsOutput = findViewById(R.id.smsOutput);
        Button btnLerSms = findViewById(R.id.btnLerSms);

        btnLerSms.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS}, PERMISSAO_SMS);
            } else {
                lerSms();
            }
        });
    }

    private void lerSms() {
        StringBuilder smsTexto = new StringBuilder();
        Uri uriSMS = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uriSMS, null, null, null, null);

        if (cursor != null) {
            int i = 0;
            while (cursor.moveToNext() && i < 5) {
                String corpo = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                String remetente = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                smsTexto.append("De: ").append(remetente).append("\n").append(corpo).append("\n\n");
                i++;
            }
            cursor.close();
        } else {
            smsTexto.append("Não foi possível ler os SMS.");
        }

        smsOutput.setText(smsTexto.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSAO_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lerSms();
            } else {
                smsOutput.setText("Permissão negada para leitura de SMS.");
            }
        }
    }
}
