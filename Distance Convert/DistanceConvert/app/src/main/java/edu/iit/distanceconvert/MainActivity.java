package edu.iit.distanceconvert;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity {
    EditText InputDist;
    TextView OutputDist;
    TextView ConversionHist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputDist = findViewById(R.id.DistIn);
        OutputDist = findViewById(R.id.DistOut);
        ConversionHist = findViewById(R.id.CVH); // CVH is used for Conversion History
        ConversionHist.setMovementMethod(new ScrollingMovementMethod());
        RadioClick(findViewById(R.id.radioGroup));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        TextView radioinputtext = findViewById(R.id.radioinputtext);
        TextView radiooutputtext = findViewById(R.id.radiooutputtext);

        outState.putString("Saved_Input_Distance_Value", InputDist.getText().toString());
        outState.putString("Saved_Output_Distance_Value", OutputDist.getText().toString());
        outState.putString("Saved_Conversion_History_Value", ConversionHist.getText().toString());
        outState.putString("Saved_Input_Display_Heading_Value", radioinputtext.getText().toString());
        outState.putString("Saved_Output_Display_Heading_Value", radiooutputtext.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);



        InputDist.setText(savedInstanceState.getString("Saved_Input_Distance_Value"));
        OutputDist.setText(savedInstanceState.getString("Saved_Output_Distance_Value"));
        ConversionHist.setText(savedInstanceState.getString("Saved_Conversion_History_Value"));
        ConversionHist.setMovementMethod(new ScrollingMovementMethod());

        TextView radioinputtext = findViewById(R.id.radioinputtext);
        TextView radiooutputtext = findViewById(R.id.radiooutputtext);

        radioinputtext.setText(savedInstanceState.getString("Saved_Input_Display_Heading_Value"));
        radiooutputtext.setText(savedInstanceState.getString("Saved_Output_Display_Heading_Value"));
    }



    // Radio buttons clicked operations
    public void RadioClick(View view) {
        RadioButton MilestoKilometers = findViewById(R.id.mtk);
        RadioButton KilometerstoMiles = findViewById(R.id.ktm);
        TextView radioinputtext = findViewById(R.id.radioinputtext);
        TextView radiooutputtext = findViewById(R.id.radiooutputtext);

        if (MilestoKilometers.isChecked()) {

            radioinputtext.setText("Miles Value:");
            radiooutputtext.setText("Kilometers Value:");

        } else if (KilometerstoMiles.isChecked()) {

            radioinputtext.setText("Kilometers Value:");
            radiooutputtext.setText("Miles Value:");
        }
        OutputDist.setText("");

    }

    // CONVERT button clicked operations
    public void ConvertClick(View view) {
        /*
        mtk is used for Miles to Kilometers
        ktm is used for Kilometers to Miles
        DistIn is used for Distance Input
         */
        RadioButton MilestoKilometers = findViewById(R.id.mtk);
        RadioButton KilometerstoMiles = findViewById(R.id.ktm);
        //EditText InputDist = findViewById(R.id.DistIn);
        String Input = InputDist.getText().toString().trim();
        if (!MilestoKilometers.isChecked() && !KilometerstoMiles.isChecked()){

        } else if (Input == null || Input.equals("") || Input == ""){

        } else {
            /*ip is used for input value
            op is used for output value
            history is used for current conversion history
            newCH is used for latest conversion history
             */
            double ip = Double.parseDouble(Input);
            double op = 0.0;
            String newCH = "";
            String history = "";

            if (MilestoKilometers.isChecked()) {
                op = ip * 1.60934;
                newCH = ip + " Mi " + "  ==>  " + String.format("%.1f", op) + " Km\n";

            } else if (KilometerstoMiles.isChecked()) {
                op = ip * 0.621371;
                newCH = ip + " Km " + "  ==>  " + String.format("%.1f", op) + " Mi\n";
            }

            OutputDist.setText(String.format("%.1f", op));
            history = ConversionHist.getText().toString();
            history = newCH + history;
            ConversionHist.setText(history);

        }

        InputDist.setText("");

    }

//to clear conversion history when CLEAR button is pressed
    public void ClearCVH(View view) {
        ConversionHist.setText("");
        //below are additional added items when CLEAR button is pressed to clear both input and output fields
        OutputDist.setText("");
        InputDist.setText("");

    }

}
