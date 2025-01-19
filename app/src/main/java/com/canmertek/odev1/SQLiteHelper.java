package com.canmertek.odev1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RandevuDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "randevular";
    private static final String COL_ID = "id";
    private static final String COL_HASTANE = "hastane";
    private static final String COL_BIRIM = "birim";
    private static final String COL_TARIH = "tarih";
    private static final String COL_SAAT = "saat";
    private static final String COL_ADSOYAD = "adSoyad";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HASTANE + " TEXT, " +
                COL_BIRIM + " TEXT, " +
                COL_TARIH + " TEXT, " +
                COL_SAAT + " TEXT, " +
                COL_ADSOYAD + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public long addAppointment(String hastane, String birim, String tarih, String saat, String adSoyad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HASTANE, hastane);
        values.put(COL_BIRIM, birim);
        values.put(COL_TARIH, tarih);
        values.put(COL_SAAT, saat);
        values.put(COL_ADSOYAD, adSoyad);
        return db.insert(TABLE_NAME, null, values);
    }


    public Cursor getAppointments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }


    public void deleteAllAppointments() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }


    public void updateAppointment(int id, String newName, String newDate, String newTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ADSOYAD, newName);
        values.put(COL_TARIH, newDate);
        values.put(COL_SAAT, newTime);
        db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }


    public Cursor queryAppointmentsByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ADSOYAD + " LIKE ?", new String[]{"%" + name + "%"});
    }
}
