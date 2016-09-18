package com.touchbd.parash.httpmethods;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.provider.Settings.Secure;

 // --dd
public class httpMethodsActivity extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private TextView jdata;
    final String pak_id = "1.1";
    // final String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_httpmethods);

        Button btnHit = (Button) findViewById(R.id.btnHit);
        jdata = (TextView) findViewById(R.id.josnItem);

        final String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTask().execute("http://touchbd.com/2fa/otp.php?user_id=121212&otp=ACSE122&android_id=" + android_id + "&apk_id=" + pak_id);
            }

        });


        Button btnNewReg = (Button) findViewById(R.id.btn_new_reg);
        btnNewReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newphone = new Intent(httpMethodsActivity.this, NewPhone.class);
                startActivity(newphone);

            }

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


//    public void show(Menu menu){
//        Intent newphone = new Intent(this,NewPhone.class);
//        startActivity(newphone);
//
//        Toast toast = Toast.makeText(this, "User Authentication Sending....", Toast.LENGTH_LONG);
//        toast.show();
//    }


    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }


                // ========== for single data ===========
                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("api");

                JSONObject finalObject = parentArray.getJSONObject(0);

                String user_id = finalObject.getString("user_id");
                String otp = finalObject.getString("otp");
                String android_id = finalObject.getString("android_id");
                String apk_id = finalObject.getString("apk_id");
                return "user_id:" + user_id + " \n otp:" + otp + "\n unique_id:" + android_id + "\n apk_id:" + apk_id;
                // ========== for single data ===========

                //================== for multiple data ===============
//            String finalJson = buffer.toString();
//            JSONObject parentObject = new JSONObject(finalJson);
//            JSONArray parentArray = parentObject.getJSONArray("movies");
//
//            StringBuffer finalBufferdata = new StringBuffer();
//
//            for(int i =0; i<parentArray.length();i++ ) {
//                JSONObject finalObject = parentArray.getJSONObject(i);
//
//                String movieName = finalObject.getString("movie");
//                int movieYear = finalObject.getInt("year");
//                finalBufferdata.append(movieName+"--"+movieYear+"\n");
//            }
//            return finalBufferdata.toString();


                //================== for multiple data ===============


                //return buffer.toString();  // full data
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }


                connection.disconnect();
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            jdata.setText(result);
        }
    }


    //========================== QR =====================
    public void scanQR(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(httpMethodsActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String encrypted = intent.getStringExtra("SCAN_RESULT");  // 1001|111|7f90468dcf511840


                String contents = null;
                String decrypted = null;
                MCrypt mcrypt = new MCrypt();
                try {
                    //encrypted = MCrypt.bytesToHex(mcrypt.encrypt(contents));
                    contents = new String( mcrypt.decrypt(encrypted) );

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] separated = null;
                String userId = null;
                String userOTP = null;
                String userAndroidId = null;


                try {
                    separated = contents.split("\\|");
                     userId = separated[0];
                     userOTP = separated[1];
                     userAndroidId = separated[2];

                }catch (Exception e){
                    jdata.setText("Invalid QR / User / Device. ! ");
                }

                final String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID); // get device ANDROID_ID

                if(android_id.equals(userAndroidId)) {
                    Toast toast = Toast.makeText(this, "User Authentication Sending....", Toast.LENGTH_LONG);
                    toast.show();

                    new JSONTask().execute("http://touchbd.com/2fa/otp.php?user_id=" + userId + "&otp=" + userOTP + "&android_id=" + android_id + "&apk_id=" + pak_id);
                }
                else{
                    jdata.setText("Invalid User / Device ! ");
                }



            }
        }
    }


    //========================== QR =====================

}
