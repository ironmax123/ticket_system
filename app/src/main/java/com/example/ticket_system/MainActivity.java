package com.example.ticket_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    Button sendButton,listButton,readButton,useButton;
    String getdata;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //発行画面
        sendButton = (Button) findViewById(R.id.send_btn);
        sendButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), ticket_send.class);
            startActivity(intent);
        });
        //リスト画面
        listButton = (Button) findViewById(R.id.list_btn);
        listButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), list_ticket.class);
            startActivity(intent);
        });
        //チッケト受け取りボタン
        readButton = (Button) findViewById(R.id.read_btn);
        readButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
        });
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        getdata= sharedPreferences.getString("tk_data", "");
        ((TextView) findViewById(R.id.Tv)).setText(String.valueOf(getdata));
        ImageView imageView = findViewById(R.id.qr_code_image);
        try {
            Bitmap bitmap = encodeAsBitmap(getdata);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // キャンセルされた場合の処理
            } else {
                // QRコードが読み取られた場合の処理
                String qrCode = result.getContents();
                ((TextView) findViewById(R.id.Tv)).setText(String.valueOf(qrCode));
                ImageView imageView = findViewById(R.id.qr_code_image);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("tk_data", qrCode);
                editor.apply();
                try {
                    Bitmap bitmap = encodeAsBitmap(qrCode);
                    imageView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private Bitmap encodeAsBitmap(String contents) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(new String(contents.getBytes("UTF-8"), "ISO-8859-1"),
                    BarcodeFormat.QR_CODE, 500, 500, null);
        } catch (IllegalArgumentException e) {
            // Unsupported format
            return null;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}