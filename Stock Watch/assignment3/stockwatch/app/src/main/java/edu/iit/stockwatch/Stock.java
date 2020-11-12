package edu.iit.stockwatch;

import java.io.Serializable;

public class Stock implements Serializable,Comparable<Stock>{
    private String stockSymbol;
    private String stockName;
    private Double stockValue;
    private Double stockChange;
    private Double stockChangePercent;

    public Stock(){
    }

    public Stock(String stockSymbol, String stockName, Double stockValue, Double stockChange, Double stockChangePercent) {
        this.stockSymbol = stockSymbol;
        this.stockValue = stockValue;
        this.stockChange = stockChange;
        this.stockName = stockName;
        this.stockChangePercent = stockChangePercent;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public Double getStockValue() {
        return stockValue;
    }

    public Double getStockChange() {
        return stockChange;
    }

    public String getStockName() {
        return stockName;
    }

    public Double getStockChangePercent() {
        return stockChangePercent;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public void setStockValue(Double stockValue) {
        this.stockValue = stockValue;
    }

    public void setStockChange(Double stockChange) {
        this.stockChange = stockChange;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public void setStockChangePercent(Double stockChangePercent){
        this.stockChangePercent = stockChangePercent;
    }

    @Override
    public String toString() {
        return this.stockSymbol + " " + this.stockName + " " + this.stockValue + " "+ this.stockChange + " " + this.stockChangePercent;
    }

    public String toSaveFormat ()
    {
        return this.stockSymbol + " " + this.stockName + " " + this.stockValue + " "+ this.stockChange + " " + this.stockChangePercent;
    }

    @Override
    public int compareTo(Stock sData) {
        return this.getStockSymbol().compareTo(sData.getStockSymbol());
    }

}
