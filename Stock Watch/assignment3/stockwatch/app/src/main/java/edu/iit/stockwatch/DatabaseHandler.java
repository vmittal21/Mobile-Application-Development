package edu.iit.stockwatch;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static final String TABLE = "StockWatchTable";
    private static final String TAG = "DatabaseHandler";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "StockWatchDB";
    //Database column
    private static final String SYMBOL = "Symbol";
    private static final String COMPANY_NAME = "CompanyName";
    private MainActivity ma;

    public DatabaseHandler(MainActivity context) {
        super(context, DB_NAME,null, DB_VERSION);
        ma = context;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL( "CREATE TABLE " + TABLE + " (" + SYMBOL + " TEXT not null unique," + COMPANY_NAME + " TEXT not null)");
    }

    public void dumpdbtoLog() {
        Cursor cursor = db.rawQuery("Select * from " + TABLE,null);
        if(cursor!=null){
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++){
                String symbol = cursor.getString(0);
                String companyName = cursor.getString(1);
                Log.d(TAG,"Dump data : " + String.format("%s %-18s",SYMBOL + ":",symbol) + String.format("%s %-18s", COMPANY_NAME +":",companyName));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    public ArrayList<String[]> loadStocks() {
        ArrayList<String[]> stockList = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE,
                new String[]{SYMBOL, COMPANY_NAME},
                null,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                stockList.add(new String[] {symbol, company});
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE");
        return stockList;
    }

    public void addStock(ArrayList<Stock> stockVal) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stockVal.get(0).getStockSymbol());
        values.put(COMPANY_NAME, stockVal.get(0).getStockName());
        long key = db.insert(TABLE, null, values);
    }

    public void updateStock(Stock stock) {
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getStockSymbol());
        values.put(COMPANY_NAME, stock.getStockName());
        long key = db.update(TABLE, values, SYMBOL + " = ?", new String[]{stock.getStockSymbol()});
    }

    public void deleteStock(String symbolName) {
        int cnt = db.delete(TABLE, SYMBOL + " = ?", new String[]{symbolName});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){


    }
}
