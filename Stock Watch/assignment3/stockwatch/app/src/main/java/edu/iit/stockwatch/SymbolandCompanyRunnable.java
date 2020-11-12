package edu.iit.stockwatch;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolandCompanyRunnable implements Runnable {

    private static final String TAG = "SymbolandCompanyRunnable";
    private MainActivity mainActivity;
    ArrayList<String[]> symList = new ArrayList<>();
    private List<String[]> listSymbolCompany = new ArrayList<>();
    private static final String SYMBOL_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    //public static boolean running = false;
    private String symbol;

    SymbolandCompanyRunnable(MainActivity mainActivity, String symbol) {
        this.mainActivity = mainActivity;
        this.symbol = symbol;
    }

    @Override
    public void run() {

        Uri stockUri = Uri.parse(SYMBOL_URL);
        String urlToUse = stockUri.toString();

//        Log.d(TAG, "doInBackground: " + stockUri);
        StringBuilder wantedData = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                wantedData.append(line).append('\n');
            }

            String symbolAllValue = wantedData.toString();
            Map<String, String> symList = new HashMap<String, String>();

            try {
                JSONArray jArray = new JSONArray(symbolAllValue);
                for (int i=0; i < jArray.length(); i++ ){
                    JSONObject jStock = (JSONObject) jArray.get(i);
//                    System.out.println(jStock);
                    String symData = jStock.getString("symbol");
                    String companyName = jStock.getString("name");
//                    System.out.println(symData+" "+companyName);
//                    symList.add(new String[]{ symData,companyName});
                    symList.put(symData, companyName);
//                    System.out.println("Sym List: \n"+symList);
//                    break;
                }
            }
            catch (Exception e) {
                //Log.e(TAG, "doInBackground: ", e);
                e.printStackTrace();
            }

            if (symList.get(this.symbol) != null)
                listSymbolCompany.add(new String[]{this.symbol, symList.get(this.symbol)});
            System.out.println("hello");
            for(int i=0; i < symList.size(); i++){
                System.out.println("Symlist content: "+symList.get(i));
            }
        }

        catch (Exception e){
            //Log.e(TAG, "run: ", e);
            handleResults(null);

        }
        handleResults(wantedData.toString());
    }

    private void handleResults(final String jsonString) {

        /*final ArrayList<Stock> symbolList = parseJSON(jsonString);*/
       mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*if (stockList != null)
                    Toast.makeText(mainActivity, "Loaded " + symbolList.size() + " stock.", Toast.LENGTH_LONG).show();*/
                mainActivity.whenSymbolIsDone(listSymbolCompany);
            }
      });
   }

}

