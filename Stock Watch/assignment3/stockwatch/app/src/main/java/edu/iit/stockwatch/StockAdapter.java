package edu.iit.stockwatch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder>{
    private List<Stock> stockList;
    private MainActivity mact;

    public StockAdapter(List<Stock> stockls, MainActivity mainAct) {
        this.stockList = stockls;
        mact =mainAct;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_layout,viewGroup,false);
        itemView.setOnClickListener(mact);
        itemView.setOnLongClickListener((View.OnLongClickListener) mact);
        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder stockViewHolder, int position) {
        Stock stockData = stockList.get(position);
        stockViewHolder.stockSymbol.setText(stockData.getStockSymbol());
        stockViewHolder.stockValue.setText(String.format("%.2f",stockData.getStockValue()));
        stockViewHolder.stockChange.setText(String.format("%.2f",stockData.getStockChange()));
        String stockDiff = (String.format("%.2f",stockData.getStockChangePercent()));
        stockViewHolder.stockChangePercent.setText("(" + stockDiff + "%)");
        stockViewHolder.stockName.setText(stockData.getStockName());

        if(stockData.getStockChange() < 0){
            stockViewHolder.stockSymbol.setTextColor(Color.parseColor("#FF0040"));
            stockViewHolder.stockName.setTextColor(Color.parseColor("#FF0040"));
            stockViewHolder.stockValue.setTextColor(Color.parseColor("#FF0040"));
            stockViewHolder.stockChange.setText('\u25BC'+stockData.getStockChange().toString());
            stockViewHolder.stockChange.setTextColor(Color.parseColor("#FF0040"));
            stockViewHolder.stockChangePercent.setTextColor(Color.parseColor("#FF0040"));
        }
        else {
            stockViewHolder.stockSymbol.setTextColor(Color.parseColor("#00FF00"));
            stockViewHolder.stockName.setTextColor(Color.parseColor("#00FF00"));
            stockViewHolder.stockValue.setTextColor(Color.parseColor("#00FF00"));
            stockViewHolder.stockChange.setText('\u25B2'+stockData.getStockChange().toString());
            stockViewHolder.stockChange.setTextColor(Color.parseColor("#00FF00"));
            stockViewHolder.stockChangePercent.setTextColor(Color.parseColor("#00FF00"));
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}
