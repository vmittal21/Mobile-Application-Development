package edu.iit.vidhia2;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Task extends AsyncTask<String,Void,String> {
    private static final String TAG = "Task";
    MainActivity mainActivity;

    public Task(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String filename = strings[0];
        String fileAsString = null;
        int n;

        try {
            FileInputStream fis = mainActivity.getApplicationContext().openFileInput(filename);
            StringBuffer fileContent = new StringBuffer("");

            byte[] buffer = new byte[2048*2048];

            while((n = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }

            fileAsString = fileContent.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileAsString;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String fileAsString) {
        ArrayList<Notes> notesArrayList = new ArrayList<>();

        try{
            super.onPostExecute(fileAsString);

            Notes notes;
            InputStream stream = new ByteArrayInputStream(fileAsString.getBytes(mainActivity.getString(R.string.encoding)));
            JsonReader jsonReader = new JsonReader(new InputStreamReader(stream , mainActivity.getString(R.string.encoding)));
            jsonReader.beginArray();

            while(jsonReader.hasNext()) {
                notes = new Notes();
                jsonReader.beginObject();

                while (jsonReader.hasNext()) {
                    String textValue = jsonReader.nextName();

                    if (textValue.equals("title")) {
                        notes.setTitle(jsonReader.nextString());
                    } else if (textValue.equals("date")) {
                        notes.setDate(jsonReader.nextString());
                    } else if (textValue.equals("description")) {
                        notes.setDescription(jsonReader.nextString());
                    } else {
                        jsonReader.skipValue();
                    }
                }

                jsonReader.endObject();
                notesArrayList.add(notes);
            }
            jsonReader.endArray();
        }
        catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        catch (IOException inputOutputEception) {
            inputOutputEception.printStackTrace();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        mainActivity.fetchAsyncTaskData(notesArrayList);
    }
}
