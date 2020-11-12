package edu.iit.knowyourgovernment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private static final String TAG = "MainActivity";
    private List<Official> officialList = new ArrayList<>();
    private OfficialAdapter officialAdapter;
    private static TextView locationTextView;
    private Locator locator;
    private static MainActivity mainActivity;

    private TextView warning;
    private ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                mainActivity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewDataDisplay);

        officialAdapter = new OfficialAdapter(officialList, this);

        locationTextView = (TextView) findViewById(R.id.locationTextView);
        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        warning = (TextView) findViewById(R.id.ma_warning);

        if (networkCheck()){
          //  System.out.println("in here");
            locator = new Locator(this);
        } else {
            warningShow("Cannot connect to network");
        }

        if (locationTextView.getText().toString().equals("No Data For Location")) warningShow("close app and reopen!");

    }

    private void warningShow(String cannot_connect_to_network) {
        warning.setVisibility(View.VISIBLE);
        warning.setText(cannot_connect_to_network);
    }

    private boolean networkCheck() {
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo !=null && netInfo.isConnectedOrConnecting())return true;
        else return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_display, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void addOfficial(Official official){
        officialList.add(official);
        System.out.println(officialList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                officialAdapter.notifyDataSetChanged();
            }
        });
    }

    public void clearOfficial(){
        officialList.clear();
    }

    public void setLocationText(String location){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                locationTextView.setText(location);
            }
        });
    }

    public void test(String s){

        System.out.println(s);
        locationTextView.setText("60616");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_menu:
             //   System.out.println("here");
                Intent infoIntent = new Intent(this, Information_Activity.class);
                startActivity(infoIntent);
                break;
            case R.id.search_menu:
                searchButton();

                break;
        }
        return true;
    }

    public void searchButton(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dia, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter a City, State or Zip code");
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText inputTextView = (EditText) view.findViewById(R.id.inputTextView);
                String input = inputTextView.getText().toString();
               // System.out.println(input);

                DataLoader dataloader = new DataLoader(mainActivity , input);
                new Thread(dataloader).start();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setLocation(double lat, double lon){

        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat,lon, 1);
//            System.out.println("Address: "+addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses == null){
            Log.d("addresses", "Null");

        }else{
            locationTextView.setText(addresses.get(0).getAddressLine(0).toString());
        }

        final String st = locationTextView.getText().toString();
        System.out.println(st);
        DataLoader dataloader = new DataLoader(this, st);
        new Thread(dataloader).start();

       // AsyncDataLoader adl = new AsyncDataLoader(this);

        //adl.execute(locationTextView.getText().toString());

    }

    public void warningClose() {
        warning.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildAdapterPosition(view);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("location", locationTextView.getText().toString());
        intent.putExtra("official", officialList.get(pos));
        startActivityForResult(intent, 1);

    }

//    //@Override
//    public void onClickAbout(View v) {
//        int pos = recyclerView.getChildLayoutPosition(v);
//        String url = "https://developers.google.com/civic-information/";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);
//        }


    public void WhenaddDone(ArrayList<Official> officialList) {
        System.out.println(officialList);
        officialAdapter.notifyDataSetChanged();
    }

}