package edu.iit.stockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener{

    private final List<Stock> stockList = new ArrayList<>();
    private final List<String> symbolList = new ArrayList<>();
    private SwipeRefreshLayout swipe;
    private RecyclerView recycleView;
    private StockAdapter sAdapter;
    private DatabaseHandler dbHandler;
    private int lastSelectedPosition;
    public String userInputValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycleView = findViewById(R.id.recycle);
        swipe = findViewById(R.id.swipe);
        sAdapter = new StockAdapter(stockList,this);
        recycleView.setAdapter(sAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        dbHandler = new DatabaseHandler(this);
        dbHandler.dumpdbtoLog();
        ArrayList<String[]> list = dbHandler.loadStocks();

       /* SymbolandCompanyRunnable symbolandcompanyrunnable = new SymbolandCompanyRunnable(this);
        new Thread(symbolandcompanyrunnable).start(); */

       /* StockFinanceDataRunnable stockfinancedatarunnable = new StockFinanceDataRunnable(this);
        new Thread(stockfinancedatarunnable).start(); */

        for(int j=0; j < list.size(); j++) {
            stockList.add(new Stock(list.get(j)[0], list.get(j)[1],0.0,0.0,0.0));
            symbolList.add(stockList.get(j).getStockSymbol());

        }

        Collections.sort(stockList, Collections.reverseOrder());

        if(stockList.size() == 0){
            Toast.makeText(this, "No stock added", Toast.LENGTH_SHORT).show();
        } else{
            if (internetFine()) {
                updateStock();
                sAdapter.notifyDataSetChanged();
            } else {
                errorInInternet();
            }
        }
        sAdapter.notifyDataSetChanged();

        if(internetFine()) {
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateStock();
                    swipe.setRefreshing(false);
                }
            });
        } else{
            errorInInternet();
        }
    }
    public void updateStock(){
        if (internetFine()){
            int listSize = stockList.size();
            updateStockVal(listSize);
            Collections.shuffle(stockList);
        } else{
            errorInInternet();
        }
        dbHandler.dumpdbtoLog();
        sAdapter.notifyDataSetChanged();
    }
    void updateStockVal(int listValue) {
        for (int i = 0; i < listValue; i++) {
            String symbol = stockList.get(stockList.size() - 1).getStockSymbol();
            int position = stockList.size() - 1;
            dbHandler.deleteStock(stockList.get(position).getStockSymbol());
            stockList.remove(position);
            symbolList.remove(position);
            sAdapter.notifyDataSetChanged();

            StockFinanceDataRunnable stockfinancedatarunnable = new StockFinanceDataRunnable(this, symbol);
            new Thread(stockfinancedatarunnable).start();
        }
    }

    public void WhenaddDone (ArrayList<Stock> stockValues){
        if (stockValues.size() != 0) {
            stockList.addAll(stockValues);
            Collections.sort(stockList);
            addStockVal(stockValues);
            sAdapter.notifyDataSetChanged();
        } else {
            errorNoStockPresent(stockValues.get(0).toString());
        }
    }
    void addStockVal(ArrayList<Stock> stockValue){
        if (!symbolList.contains(stockValue.get(0).getStockSymbol())) {
            dbHandler.addStock(stockValue);
            symbolList.add(0, stockValue.get(0).getStockSymbol());
        }
        sAdapter.notifyDataSetChanged();
    }
    public void errorNoStockPresent(String symbolValue){
        final AlertDialog alertDialogFDMatching = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Symbol Not Found: " + symbolValue)
                .setMessage("Data for stock symbol")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {    }})
                .show();
    }

    private boolean internetFine() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void errorInInternet () {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("No Network Connection")
                .setMessage("Stocks Cannot Be Added/Updated Without A Network Connection")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {    }})
                .show();
    }
    private void searchStockList(String stkSymbol){
        if(stkSymbol.isEmpty() || stkSymbol.equals("")){
            final AlertDialog alertDialogDuplicate = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle("You have not entered any stock symbol")
                    .setMessage("Please enter a stock symbol")
                    .setNegativeButton("OK",dialogClickListenerDelete)
                    .show();
        } else if(symbolList.contains(stkSymbol)){
            final AlertDialog alertDialogDuplicate = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle("Duplicate Stock")
                    .setMessage("Stock Symbol "+ stkSymbol +" is already displayed")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else{
            SymbolandCompanyRunnable symbolandcompanyrunnable = new SymbolandCompanyRunnable(this, stkSymbol);
            new Thread(symbolandcompanyrunnable).start();
        }
    }

    public void StockFailDownload(List<Stock> noteDataList) {
        this.stockList.clear();
        this.stockList.addAll(noteDataList);
        sAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return true;
    }

    public void whenSymbolIsDone(List<String[]> listSymbol) {
        System.out.println("list symbol: \n"+listSymbol);
        if (listSymbol.size() == 0) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Symbol Not Found");
            builder.setMessage("Data for stock symbol");
            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (listSymbol.size() == 1) {
            openSelectedOne(listSymbol.get(0)[0]);
        }
        else {
            final String[] listPossibleSymbol = new String[listSymbol.size()];
            String[] listPossibleName = new String[listSymbol.size()];

            for (int i = 0; i< listSymbol.size(); i++){
                listPossibleSymbol[i]=listSymbol.get(i)[0];
                listPossibleName[i]=listSymbol.get(i)[0] + "-" + listSymbol.get(i)[1];
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make a selection from the stock list");
            builder.setNegativeButton("Nevermind", dialogClickListenerDelete);
            builder.setItems(listPossibleName, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int w) {openSelectedOne(listPossibleSymbol[w]);}});
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        sAdapter.notifyDataSetChanged();
    }

    public void openSelectedOne(String symbol) {
        if(symbolList.contains(symbol)){
            final AlertDialog alertDialogDuplicate = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle("Duplicate Stock")
                    .setMessage("Stock Symbol "+ symbol + " is already displayed")
                    .show();
        } else {
            StockFinanceDataRunnable stockfinancedatarunnable = new StockFinanceDataRunnable(this, symbol);
            new Thread(stockfinancedatarunnable).start();
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addStock:
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View layout = li.inflate(R.layout.searchstock_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(layout);
                final EditText userInput = (EditText) layout.findViewById(R.id.searchStock);
                userInput.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                userInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                userInput.setGravity(Gravity.CENTER_HORIZONTAL);

                userInputValue = userInput.getText().toString();

                if(internetFine()) {
                    builder.setTitle("Stock Selection")
                            .setMessage("Please enter a Stock Symbol")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int w) {
                                   searchStockList(userInput.getText().toString());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int w) {
                                    Toast.makeText(getApplicationContext(),"Stock not added",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();}
                else {
                    errorInInternet();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recycleView.getChildLayoutPosition(v);
        Stock stock = stockList.get(pos);
        String symbolData;

        if (internetFine()) {
            symbolData=stock.getStockSymbol();
            String url = "http://www.marketwatch.com/investing/stock/" + symbolData;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else {
            errorInInternet();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recycleView.getChildLayoutPosition(v);
        Stock stock = stockList.get(pos);
        lastSelectedPosition = pos;

        final AlertDialog alertDialogDelete = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Delete Stock")
                .setMessage("Delete Stock Symbol " + stock.getStockSymbol()+"?")
                .setPositiveButton("Yes",dialogClickListenerDelete)
                .setNegativeButton("No",dialogClickListenerDelete)
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();
        return false;
    }

    DialogInterface.OnClickListener dialogClickListenerDelete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int w) {
            switch (w){
                case DialogInterface.BUTTON_POSITIVE:
                    int position = lastSelectedPosition;
                    dbHandler.deleteStock(stockList.get(position).getStockSymbol());
                    stockList.remove(position);
                    symbolList.remove(position);
                    sAdapter.notifyDataSetChanged();
                    sAdapter.notifyDataSetChanged();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
}