package com.example.ticket_system;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.fonts.SystemFonts;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class ticket_send extends AppCompatActivity {
    Button homeButton,listButton,pub,addbtn;
    String tk_data="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        pub = (Button) findViewById(R.id.pub_btn);
        addbtn = (Button) findViewById(R.id.add_btn);
        addbtn.setVisibility(View.INVISIBLE);
        
        //画面推移の処理
        homeButton = (Button) findViewById(R.id.home_btn);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
        });

        listButton = (Button) findViewById(R.id.list_btn);
        listButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), list_ticket.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        //発行ボタンの処理
        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tk_title = (EditText) findViewById(R.id.et);
                EditText tk_limit = (EditText) findViewById(R.id.date_et);
                String tittle_string = tk_title.getText().toString();
                String limit_string = tk_limit.getText().toString();
                if(tittle_string.isEmpty()){
                    Toast myToast = Toast.makeText(
                            getApplicationContext(),
                            "タイトルが入力されていません",
                            Toast.LENGTH_SHORT
                    );
                    myToast.show();
                }else if(limit_string.isEmpty()){
                    Toast myToast = Toast.makeText(
                            getApplicationContext(),
                            "有効期限が入力されていません",
                            Toast.LENGTH_SHORT
                    );
                    myToast.show();
                }else{
                    String tic_ran_sys = RandomStringUtils.randomAlphanumeric(16);
                    ImageView imageView = findViewById(R.id.qr_code_image);
                    tk_data =tittle_string+"\n"+"期限:["+limit_string+"]"+"\n"+"id:["+tic_ran_sys+"]";
                    try {
                        Bitmap bitmap = encodeAsBitmap(tk_data);
                        imageView.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    addbtn.setVisibility(View.VISIBLE);
                }
            }
        });

        //保存ボタンの処理
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // 既存の値を取得
                String savedData = sharedPreferences.getString("tk_data_list", "");
                // 新しいデータを追加して保存
                String newData = savedData + "\n" + tk_data;
                editor.putString("tk_data_list", newData);
                editor.apply();

                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        "保存完了！",
                        Toast.LENGTH_SHORT
                );
                myToast.show();
                addbtn.setVisibility(View.INVISIBLE);
            }
        });
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