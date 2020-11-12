package edu.iit.stockwatch;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StockFinanceDataRunnable implements Runnable {

    private static final String TAG = "StockFinanceDataRunnable";

    private MainActivity mainActivity;
    private static final String STOCK_URL = "https://cloud.iexapis.com/stable/stock/";
    // private static final String yourAPIKey = "sk_edc53da27f07466aadae315a1f1df8f6";
    //public static boolean running = false;
    public String REQUIRED_URL;
    private String symbol;

    StockFinanceDataRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    StockFinanceDataRunnable(MainActivity mainActivity, String symbol) {
        this.mainActivity = mainActivity;
        this.symbol = symbol;
    }

    @Override
    public void run() {

        REQUIRED_URL = STOCK_URL + symbol + "/quote?token=sk_edc53da27f07466aadae315a1f1df8f6";
        Uri stockDetailUri = Uri.parse(REQUIRED_URL);
        String urlToFinalUse = stockDetailUri.toString();
        Log.d(TAG, "doInBackground: " + urlToFinalUse);
        StringBuilder wantedData = new StringBuilder();
        //String sentence;

        try {
            URL urlNew = new URL(urlToFinalUse);
            HttpURLConnection conn1 = (HttpURLConnection) urlNew.openConnection();
            conn1.setRequestMethod("GET");
            conn1.connect();

            if (conn1.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                handleResults(conn1.getResponseCode());
//                System.out.println(conn1.getResponseCode());
                return;
            }


            InputStream is1 = conn1.getInputStream();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(is1));

            String sentence;
            while ((sentence = reader1.readLine()) != null) {
                wantedData.append(sentence).append("\n");
            }
            Log.d(TAG, "run: " + wantedData.toString());
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }
        handleResults(wantedData.toString());
    }




    public ArrayList<Stock> parseJSON(String s) {
        ArrayList<Stock> stockList = new ArrayList<>();

        try {
            if (!s.equals("httpserver.cc: Response Code 404")) {
                JSONObject jobject = new JSONObject(s);
                String symbol = jobject.getString("symbol");
                String cName = jobject.getString("companyName");

                String latestPrice = jobject.getString("latestPrice");
                Double latestStockVal = Double.parseDouble(latestPrice);

                String change = jobject.getString("change");
                Double stockChange = Double.parseDouble(change);

                String stockDiffPercent = jobject.getString("changePercent");
                Double stockPercentDiff = Double.parseDouble(stockDiffPercent);

                stockList.add(new Stock(symbol, cName, latestStockVal, stockChange, stockPercentDiff * 100));
            }
            return stockList;
        } catch (JSONException e1) {
            e1.printStackTrace();
            Log.e(TAG, "doInBackground: ", e1);
        }
        return null;
    }


    private void handleResults(String s) {

        final ArrayList<Stock> stockFullList = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
           @Override
            public void run() {
                if (stockFullList != null)
                    //Toast.makeText(mainActivity, "Loaded " + stockFullList.size() + " stock.", Toast.LENGTH_LONG).show();
                    mainActivity.WhenaddDone(stockFullList);
            }
       });
    }
}
