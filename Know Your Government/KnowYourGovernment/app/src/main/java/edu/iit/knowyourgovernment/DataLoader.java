package edu.iit.knowyourgovernment;

import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//my API key = AIzaSyAZDNJwU99Q009643oAb4Np9Sw6ebIN3mo
//https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyAZDNJwU99Q009643oAb4Np9Sw6ebIN3mo&address=zip-code
//https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyAZDNJwU99Q009643oAb4Np9Sw6ebIN3mo&address=city

public class DataLoader implements Runnable {

    private static final String TAG = "DataLoader";

    private MainActivity mainActivity;
    private static final String prefix = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyAZDNJwU99Q009643oAb4Np9Sw6ebIN3mo&address=";
    public String REQUIRED_URL;
    private String input;

    DataLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    DataLoader(MainActivity mainActivity, String input) {
        this.mainActivity = mainActivity;
        this.input = input;
    }

//    DataLoader(DialogInterface.OnClickListener onClickListener, MainActivity mainActivity, String input) {
//        // System.out.println("what is my task");
//        this.mainActivity = mainActivity;
//        this.input = input;
//    }

    @Override
    public void run() {

       // System.out.println(input);
        REQUIRED_URL = prefix + input;

        Uri locateuri = Uri.parse(REQUIRED_URL);
        String urlToFinalUse = locateuri.toString();
        //  System.out.println(urlToFinalUse);
       // Log.d(TAG, "doInBackground: " + urlToFinalUse);
        StringBuilder sb = new StringBuilder();
        //String sentence;

        try {
            URL urlNew = new URL(urlToFinalUse);
            HttpURLConnection conn1 = (HttpURLConnection) urlNew.openConnection();
            conn1.setRequestMethod("GET");
            conn1.connect();
              System.out.println("connected");

            if (conn1.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //handleResults();
//                System.out.println(conn1.getResponseCode());
                return;
            }


            InputStream is1 = conn1.getInputStream();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(is1));

            String line;
            while ((line = reader1.readLine()) != null) {
                sb.append(line).append("\n");
            }
           // Log.d(TAG, "run: " + sb.toString());
            //handleResults(null);

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            // handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }


    public ArrayList<Official> parseJSON(String s) {
        ArrayList<Official> officialList = new ArrayList<>();

        try {
            if (!s.equals("httpserver.cc: Response Code 404")) {
                JSONObject jObjMain = new JSONObject(s);

              //  System.out.println("WHat should i do now?");
                JSONObject jNormalInput = jObjMain.getJSONObject("normalizedInput");

                String locationText = jNormalInput.getString("city") + ", " + jNormalInput.getString("state") + " " + jNormalInput.getString("zip");
                System.out.println(locationText);
                mainActivity.setLocationText(locationText);
                JSONArray jArrayOffices = jObjMain.getJSONArray("offices");
//                System.out.println(jArrayOffices);
                JSONArray jArrayOfficials = jObjMain.getJSONArray("officials");
//                System.out.println(jArrayOfficials);

                int length = jArrayOffices.length();
                mainActivity.clearOfficial();

                for (int i = 0; i < length; i++) {
                    JSONObject jObj = jArrayOffices.getJSONObject(i);
                    String officeName = jObj.getString("name");
                    System.out.println("DATA-outerlooop:\n"+jObj);
                    JSONArray indicesStr = jObj.getJSONArray("officialIndices");
                    ArrayList<Integer> indices = new ArrayList<>();

                    for (int j = 0; j < 1; j++) {
                        int pos = Integer.parseInt(indicesStr.getString(j));
                        Official official = new Official(officeName);
                        JSONObject jOfficial = jArrayOfficials.getJSONObject(pos);
                        System.out.println("DATA:\n"+jOfficial);
                        official.setName(jOfficial.getString("name"));

                        JSONArray jAddresses = jOfficial.getJSONArray("address");
                        JSONObject jAddress = jAddresses.getJSONObject(0);

                        String address = "";

                        if (jAddress.has("line1")) address += jAddress.getString("line1") + '\n';
                        if (jAddress.has("line2")) address += jAddress.getString("line2") + '\n';
                        if (jAddress.has("line3")) address += jAddress.getString("line3") + '\n';
                        if (jAddress.has("line4")) address += jAddress.getString("line4") + '\n';
                        if (jAddress.has("line5")) address += jAddress.getString("line5") + '\n';
                        if (jAddress.has("city")) address += jAddress.getString("city") + ", ";
                        if (jAddress.has("state")) address += jAddress.getString("state") + ' ';
                        if (jAddress.has("zip")) address += jAddress.getString("zip");

                        official.setAddress(address);
                        System.out.println("address");

                        if (jOfficial.has("party")) official.setParty(jOfficial.getString("party"));
                       // System.out.println(jOfficial.getString("party"));
                        if (jOfficial.has("phones"))
                            official.setPhone(jOfficial.getJSONArray("phones").getString(0));
                        if (jOfficial.has("urls"))
                            official.setUrl(jOfficial.getJSONArray("urls").getString(0));
                        if (jOfficial.has("emails"))
                            official.setEmail(jOfficial.getJSONArray("emails").getString(0));

                        if (jOfficial.has("channels")) {
                            Channel channel = new Channel();

                            JSONArray jChannels = jOfficial.getJSONArray("channels");
                            for (int k = 0; k < jChannels.length(); k++) {
                                JSONObject jChannel = jChannels.getJSONObject(k);
                                if (jChannel.getString("type").equals("Facebook"))
                                    channel.setFacebookId(jChannel.getString("id"));
                                if (jChannel.getString("type").equals("Twitter"))
                                    channel.setTwitterId(jChannel.getString("id"));
                                if (jChannel.getString("type").equals("YouTube"))
                                    channel.setYoutubeId(jChannel.getString("id"));
                            }
                            official.setChannel(channel);
                        }

                        if (jOfficial.has("photoUrl"))
                            official.setPhotoUrl(jOfficial.getString("photoUrl"));
                        mainActivity.addOfficial(official);
                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
           // handleResults(null);
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        return officialList;
    }

    private void handleResults(final String s) {

        final ArrayList<Official> officialList = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (officialList != null)
//                    //Toast.makeText(mainActivity, "Loaded " + stockFullList.size() + " stock.", Toast.LENGTH_LONG).show();
                    mainActivity.WhenaddDone(officialList);
            }
        });
    }
}
