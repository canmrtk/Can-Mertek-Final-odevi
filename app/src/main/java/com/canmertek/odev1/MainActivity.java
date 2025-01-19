package com.canmertek.odev1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteHelper dbHelper;
    ArrayList<String> randevularListesi;
    ArrayAdapter<String> adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Dişçi Randevum"); //bar ismini değiştirdim

        dbHelper = new SQLiteHelper(this);
        listView = findViewById(R.id.listView);

        Button onlineRandevuButton = findViewById(R.id.onlineRandevuButton);
        Button telefonButton = findViewById(R.id.telefonButton);

        onlineRandevuButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RandevuActivity.class);
            startActivity(intent);
        });

        telefonButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:021221221"));
            startActivity(intent);
        });


        randevularListesi = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, randevularListesi);
        listView.setAdapter(adapter);
    }

    //menü
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.list) {
            loadRandevular();
            Toast.makeText(this, "Randevular listelendi.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.delete) {
            showDeleteConfirmationDialog();
            return true;
        } else if (item.getItemId() == R.id.update) {
            showUpdateDialog();
            return true;
        } else if (item.getItemId() == R.id.query) {
            showQueryDialog();
            return true;
        } else if (item.getItemId() == R.id.exit) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    //veriyi çeker listview de gösterir
    private void loadRandevular() {
        randevularListesi.clear();
        Cursor cursor = dbHelper.getAppointments();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Hiç randevu bulunamadı.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String adSoyad = cursor.getString(5);
                String tarihSaat = cursor.getString(3) + " - " + cursor.getString(4);
                randevularListesi.add(adSoyad + " (" + tarihSaat + ")");
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tüm Randevuları Sil?");
        builder.setMessage("Tüm randevuları silmek istediğinize emin misiniz?");
        builder.setPositiveButton("Evet", (dialog, which) -> {
            dbHelper.deleteAllAppointments();
            randevularListesi.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Tüm randevular silindi.", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hayır", null);
        builder.show();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Saat Güncelleme");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Ad Soyad");
        layout.addView(nameInput);

        final EditText newSaatInput = new EditText(this);
        newSaatInput.setHint("Yeni Saat (10:00 - 17:00 arası)");
        layout.addView(newSaatInput);

        builder.setView(layout);

        builder.setPositiveButton("Güncelle", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String newSaat = newSaatInput.getText().toString().trim();

            if (name.isEmpty() || newSaat.isEmpty()) {
                Toast.makeText(this, "Lütfen Ad Soyad ve Yeni Saat bilgilerini giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Saat doğrulama
            if (!isValidSaat(newSaat)) {
                Toast.makeText(this, "Geçerli bir saat giriniz (10:00 - 17:00 arası).", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = dbHelper.queryAppointmentsByName(name);
            if (cursor != null && cursor.moveToFirst()) {

                int idColumnIndex = cursor.getColumnIndex("id");
                int tarihColumnIndex = cursor.getColumnIndex("tarih");

                if (idColumnIndex != -1 && tarihColumnIndex != -1) {
                    int id = cursor.getInt(idColumnIndex);
                    String currentTarih = cursor.getString(tarihColumnIndex);


                    dbHelper.updateAppointment(id, name, currentTarih, newSaat);
                    loadRandevular();
                    Toast.makeText(this, "Saat başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Veritabanı sütunlarında bir hata oluştu.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Ad Soyad ile eşleşen bir randevu bulunamadı.", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) {
                cursor.close();
            }
        });

        builder.setNegativeButton("İptal", null);
        builder.show();
    }


    private boolean isValidSaat(String saat) {
        String[] validHours = {"10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};
        for (String validHour : validHours) {
            if (validHour.equals(saat)) {
                return true;
            }
        }
        return false;
    }

    private void showQueryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Randevu Sorgula");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        final EditText queryInput = new EditText(this);
        queryInput.setHint("Ad Soyad Girin");
        layout.addView(queryInput);

        builder.setView(layout);

        builder.setPositiveButton("Sorgula", (dialog, which) -> {
            String name = queryInput.getText().toString();
            Cursor cursor = dbHelper.queryAppointmentsByName(name);

            randevularListesi.clear();

            if (cursor.getCount() == 0) {
                Toast.makeText(this, "Sonuç bulunamadı.", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    String adSoyad = cursor.getString(5);
                    String tarihSaat = cursor.getString(3) + " - " + cursor.getString(4);
                    randevularListesi.add(adSoyad + " (" + tarihSaat + ")");
                }
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("İptal", null);
        builder.show();
    }
}
