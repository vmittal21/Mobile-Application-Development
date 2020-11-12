package edu.iit.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder{
    public TextView stockSymbol;
    public TextView stockValue;
    public TextView stockChange;
    public TextView stockName;
    public TextView stockChangePercent;

    public StockViewHolder(View v){
        super(v);
        stockSymbol = v.findViewById(R.id.stockSymbol);
        stockValue = v.findViewById(R.id.stockValue);
        stockChange = v.findViewById(R.id.stockChange);
        stockName = v.findViewById(R.id.stockName);
        stockChangePercent = v.findViewById(R.id.stockChangePercent);
    }
}
