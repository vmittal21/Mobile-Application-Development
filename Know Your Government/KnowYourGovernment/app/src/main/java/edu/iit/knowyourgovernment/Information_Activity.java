package edu.iit.knowyourgovernment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Information_Activity extends AppCompatActivity {
    private static final String TAG = "Information_Activity";
    private TextView civicTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        civicTextView=findViewById(R.id.civiclink);
        civicTextView.setClickable(true);
        civicTextView.setMovementMethod(LinkMovementMethod.getInstance());
        String text="<a style=color:white href='https://developers.google.com/civic-information'> Google Civic Information API </a>";
        civicTextView.setText(Html.fromHtml(text));
    }
}
