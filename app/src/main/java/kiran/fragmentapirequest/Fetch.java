package kiran.fragmentapirequest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by saikiran on 3/30/2016.
 */
public class Fetch { String url = "https://itunes.apple.com/rss/customerreviews/id=529479190/json";
    private static final String TAG = "Fragment";

    private static final String JSON_ID = "label";
    private static final String JSON_NAME = "name";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }


    public String retriveJsonData() {
        StringBuilder ret = new StringBuilder();
        try {
            Log.d(TAG, "retriveJsonData: ");
            String jsonString = (new String(getUrlBytes(url)));
            //Parse to json array
            JSONArray array = (JSONArray) new JSONTokener(jsonString).nextValue();
            for(int i = 0; i < array.length(); i++){
                JSONObject json = array.getJSONObject(i);
                ret.append(json.getString(JSON_NAME));
                ret.append(json.getInt(JSON_ID));
                ret.append(json.getString("feed"));
                ret.append(System.getProperty("line.separator"));
            }

            System.out.println();
        } catch (Exception e){
        }
        Log.d(TAG, "ret: " + ret);
        return ret.toString();

    }
}
