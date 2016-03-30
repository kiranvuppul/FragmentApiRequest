package kiran.fragmentapirequest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    TextView uiUpdate;
    TextView jsonParsed;
    EditText serverText;

    private static final String TAG = "Fragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_main, container, false);
         uiUpdate = (TextView) v.findViewById(R.id.output);
         jsonParsed = (TextView) v.findViewById(R.id.jsonParsed);
        serverText = (EditText) v.findViewById(R.id.serverText);

        final Button GetServerData = (Button) v.findViewById(R.id.GetServerData);

        GetServerData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // WebServer Request URL
                String serverURL = "https://itunes.apple.com/rss/customerreviews/id=529479190/json";

                // Use AsyncTask execute Method To Prevent ANR Problem
                new FetchContent().execute(serverURL);
            }
        });
return v;
    }
 /*   public void doUpdate() {
        if(getActivity() == null || mTextView == null)
            return;
        if(mFetchedString != null){
            mTextView.setText(mFetchedString);
        }
    }
*/

    private class FetchContent extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getActivity());
        String data ="";
     /*   TextView uiUpdate = (TextView) findViewById(R.id.output);
        TextView jsonParsed = (TextView) findViewById(R.id.jsonParsed);
        int sizeData = 0;
        EditText serverText = (EditText) findViewById(R.id.serverText);
*/

        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //Start Progress Dialog (Message)

            Dialog.setMessage("Please wait..");
            Dialog.show();

            try{
                // Set Request parameter
                data +="&" + URLEncoder.encode("data", "UTF-8") + "="+serverText.getText();

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            /************ Make Post Call To Web Server ***********/
            BufferedReader reader=null;

            // Send data
            try
            {

                // Defined URL  where to send data
                URL url = new URL(urls[0]);

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + " ");
                }

                // Append Server Response To Content String
                Content = sb.toString();
            }
            catch(Exception ex)
            {
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {}
            }

            /*****************************************************/
            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if(Content !=null){

            if (Error != null) {

                uiUpdate.setText("Output : "+ Error);

            } else {

                // Show Response Json On Screen (activity)
                uiUpdate.setText( Content );
                Log.d(TAG, "content=" + Content);

                /****************** Start Parse Response JSON Data *************/

                String OutputData = "";
                JSONObject jsonResponse;

                try {

                    /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                    jsonResponse = new JSONObject(Content);

                    /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                    /*******  Returns null otherwise.  *******/
                    JSONArray jsonMainNode = jsonResponse.optJSONArray("Android");

                    /*********** Process each JSON Node ************/

                    int lengthJsonArr = jsonMainNode.length();

                    ArrayList<Record> recordArray = new ArrayList<>();


                    for(int i=0; i < lengthJsonArr; i++)
                    {
                        /****** Get Object for each JSON node.***********/
                        //JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        Record record = new Record();
                        /******* Fetch node values **********/
                        String name       = jsonMainNode.getJSONObject(i).optString("feed");
                        record.setFeed(name);
                        Log.d(TAG, "name : " + name);
                        String number     = jsonMainNode.getJSONObject(i).getString("label"); // .optString("number").toString();
                        Log.d(TAG, "number : " + number);
                        record.setLabel(number);
/*
                        String date_added = jsonMainNode.getJSONObject(i).optString("date_added", ""); // optString("date_added").toString();
                        Log.d(TAG, "date_added : " + date_added);
                        record.setTime(date_added);
                        //if(jsonMainNode.getJSONObject(i).has("test")) {
                        String test = jsonMainNode.getJSONObject(i).optString("test");
                        Log.d(TAG, "test : " + test);
                        //}
*/
                        recordArray.add(record);

                        OutputData += " Name           : "+ name +" "
                                + "Number      : "+ number +" "
                            //    + "Time                : "+ date_added +" "
                                +"------------------------------------------------- ";


                    }


                    for(int j=0; j<recordArray.size(); j++) {
                        Log.d(TAG,"recordArray name : " + recordArray.get(j).getFeed());
                        Log.d(TAG,"recordArray number : " + recordArray.get(j).getLabel());
                        //Log.d(TAG,"recordArray time : " + recordArray.get(j).getTime());
                    }



                    /****************** End Parse Response JSON Data *************/

                    //Show Parsed Output on screen (activity)
                    jsonParsed.setText( OutputData );


                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getActivity(), "no data",Toast.LENGTH_LONG).show();
                }


            }
        }

            }
    }
}