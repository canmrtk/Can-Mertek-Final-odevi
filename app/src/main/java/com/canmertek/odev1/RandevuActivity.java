package com.canmertek.odev1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class RandevuActivity extends AppCompatActivity {

    SQLiteHelper dbHelper;
    Spinner hastaneSpinner, birimSpinner;
    EditText tarihEditText, saatEditText, adSoyadEditText;
    Button kaydetButton;

    private String[] saatSecenekleri = {
            "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30",
            "16:00", "16:30", "17:00"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randevu);

        getSupportActionBar().setTitle("Dişçi Randevum");

        dbHelper = new SQLiteHelper(this);

        hastaneSpinner = findViewById(R.id.hastaneSpinner);
        birimSpinner = findViewById(R.id.birimSpinner);
        tarihEditText = findViewById(R.id.tarihEditText);
        saatEditText = findViewById(R.id.saatEditText);
        adSoyadEditText = findViewById(R.id.adSoyadEditText);
        kaydetButton = findViewById(R.id.kaydetButton);


        String[] hastaneListesi = {"Hastane A", "Hastane B", "Hastane C"};
        ArrayAdapter<String> hastaneAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hastaneListesi);
        hastaneSpinner.setAdapter(hastaneAdapter);


        String[] birimListesi = {"Diş Polikliniği", "Ortodonti", "Çene Cerrahisi"};
        ArrayAdapter<String> birimAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, birimListesi);
        birimSpinner.setAdapter(birimAdapter);


        tarihEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RandevuActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tarihEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        saatEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(RandevuActivity.this);
                builder.setTitle("Saat Seçiniz");

                builder.setItems(saatSecenekleri, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        saatEditText.setText(saatSecenekleri[which]);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        kaydetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hastane = hastaneSpinner.getSelectedItem().toString();
                String birim = birimSpinner.getSelectedItem().toString();
                String tarih = tarihEditText.getText().toString();
                String saat = saatEditText.getText().toString();
                String adSoyad = adSoyadEditText.getText().toString();

                if (hastane.isEmpty() || birim.isEmpty() || tarih.isEmpty() || saat.isEmpty() || adSoyad.isEmpty()) {
                    Toast.makeText(RandevuActivity.this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addAppointment(hastane, birim, tarih, saat, adSoyad);
                    Toast.makeText(RandevuActivity.this, "Randevunuz Oluştu: " + adSoyad + " - " + tarih + " " + saat, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
}
