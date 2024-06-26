package com.example.ticket_system;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.atomic.AtomicReference;

public class list_ticket extends AppCompatActivity {
    Button sendButton,homeButton,useButton,delButton;
    String tkdata;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        sendButton = (Button) findViewById(R.id.send_btn);
        sendButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), ticket_send.class);
            startActivity(intent);
        });
        homeButton = (Button) findViewById(R.id.home_btn);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
        });
        AtomicReference<SharedPreferences> sharedPreferences = new AtomicReference<>(getSharedPreferences("MyPrefs", Context.MODE_PRIVATE));
        tkdata= sharedPreferences.get().getString("tk_data_list","");
        ((TextView) findViewById(R.id.textView2)).setText(String.valueOf(tkdata));

        useButton = (Button) findViewById(R.id.use_btn);
        useButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
        });
        //テスト時用リスト削除ボタン
        /*delButton = (Button) findViewById(R.id.del_btn);
        delButton.setOnClickListener(v -> {
            sharedPreferences.set(getSharedPreferences("MyPrefs", Context.MODE_PRIVATE));
            SharedPreferences.Editor editor = sharedPreferences.get().edit();
            editor.clear();
            editor.apply();
        });*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result.getContents() != null) {
            String qrCode = result.getContents();
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String tkDataList = sharedPreferences.getString("tk_data_list", "");
            if (tkDataList.contains(qrCode)) {
                tkDataList = tkDataList.replace(qrCode, "");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("tk_data_list", tkDataList);
                editor.apply();
                tkdata=tkDataList;
                ((TextView) findViewById(R.id.textView2)).setText(String.valueOf(tkdata));
            }else{
                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        "チケットが見つかりません",
                        Toast.LENGTH_SHORT
                );
                myToast.show();
            }
        } else {
            // キャンセルされた場合の処理
        }
    }
}