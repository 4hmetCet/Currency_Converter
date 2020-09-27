package com.example.tutorial.currencyconverter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView tryText;
    TextView usdText;
    EditText inpDeci;
    EditText inpText;
    TextView inputText;
    SharedPreferences sharedPreferences;
    String tl;
    Double budgetTl;
    Double budgetEur;
    TextView budgetText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tryText=findViewById(R.id.tryText);
        usdText=findViewById(R.id.usdText);
        inpDeci=findViewById(R.id.inpDecimal);
        inpText=findViewById(R.id.inpText);
        inputText=findViewById(R.id.inputText);
        budgetText=findViewById(R.id.budgetText);
        findViewById(R.id.Buybutton).setVisibility(View.INVISIBLE);
        findViewById(R.id.Sellbutton).setVisibility(View.INVISIBLE);

        sharedPreferences=this.getSharedPreferences("com.example.ahmet.currencyconverter", Context.MODE_PRIVATE);
        budgetText.setText("TRY:"+sharedPreferences.getFloat("budgetTl",1000)+" EUR:"+sharedPreferences.getFloat("budgetEur",0));

    }

    public void getRates (View view){

        data downloadData= new data();
        try {
            String url="http://data.fixer.io/api/latest?access_key=02746921e996641d00ffca4b918242cf&format=1";
            downloadData.execute(url);
            findViewById(R.id.Buybutton).setVisibility(View.VISIBLE);
            findViewById(R.id.Sellbutton).setVisibility(View.VISIBLE);
        }catch (Exception ex){

        }
    }
    public void buy (View view){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("BUYING");
        alert.setMessage("Are You Sure To Buy "+inpDeci.getText().toString()+" Euros?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(!inpDeci.getText().toString().matches("")) {
                        Double input = Double.valueOf(inpDeci.getText().toString());
                        budgetTl=sharedPreferences.getFloat("budgetTl",1000)-Double.parseDouble(tl)*input;
                        budgetEur=sharedPreferences.getFloat("budgetEur",0)+input;
                        if(budgetTl>=0){
                            sharedPreferences.edit().putFloat("budgetEur",Float.parseFloat(budgetEur.toString())).apply();
                            sharedPreferences.edit().putFloat("budgetTl",Float.parseFloat(budgetTl.toString())).apply();
                            budgetText.setText("TRY:"+sharedPreferences.getFloat("budgetTl",1000)+" EUR:"+sharedPreferences.getFloat("budgetEur",0));
                            inpDeci.setText("");
                            Toast.makeText(MainActivity.this,"It's successful",Toast.LENGTH_LONG).show();
                            findViewById(R.id.Buybutton).setVisibility(View.INVISIBLE);
                            findViewById(R.id.Sellbutton).setVisibility(View.INVISIBLE);
                        }else{
                            Toast.makeText(MainActivity.this,"TRY budget is not enough to buy "+input+" Euros",Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception ex){

                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,"It's canceled",Toast.LENGTH_LONG).show();
            }
        });
        alert.show();
    }
    public void sell (View view){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("SELLING");
        alert.setMessage("Are You Sure To Sell "+inpDeci.getText().toString()+" Euros?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(!inpDeci.getText().toString().matches("")) {
                        Double input = Double.valueOf(inpDeci.getText().toString());
                        budgetTl = sharedPreferences.getFloat("budgetTl", 1000) + Double.parseDouble(tl) * input;
                        budgetEur = sharedPreferences.getFloat("budgetEur", 0) - input;
                        if (budgetEur >= 0) {
                            sharedPreferences.edit().putFloat("budgetEur", Float.parseFloat(budgetEur.toString())).apply();
                            sharedPreferences.edit().putFloat("budgetTl", Float.parseFloat(budgetTl.toString())).apply();
                            budgetText.setText("TRY:" + sharedPreferences.getFloat("budgetTl", 1000) + " EUR:" + sharedPreferences.getFloat("budgetEur", 0));
                            inpDeci.setText("");
                            Toast.makeText(MainActivity.this,"It's successful",Toast.LENGTH_LONG).show();
                            findViewById(R.id.Buybutton).setVisibility(View.INVISIBLE);
                            findViewById(R.id.Sellbutton).setVisibility(View.INVISIBLE);
                        } else {
                            Toast.makeText(MainActivity.this,"EUR budget is not enough to sell "+input+" Euros",Toast.LENGTH_LONG).show();
                        }
                    }

                }catch (Exception ex){
                    Toast.makeText(MainActivity.this,"Exception occurred",Toast.LENGTH_LONG).show();

                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,"It's canceled",Toast.LENGTH_LONG).show();
            }
        });
        alert.show();
    }
    public void restart (View view){
        try {
            if(sharedPreferences.getFloat("budgetTl", 1000)<1000 || sharedPreferences.getFloat("budgetEur", 0)>0) {
                sharedPreferences.edit().remove("budgetEur").apply();
                sharedPreferences.edit().remove("budgetTl").apply();
                budgetText.setText("TRY:" + sharedPreferences.getFloat("budgetTl", 1000) + " EUR:" + sharedPreferences.getFloat("budgetEur", 0));
            }
        }
        catch (Exception ex){
            Toast.makeText(MainActivity.this,"Exception occurred",Toast.LENGTH_LONG).show();
        }
    }



    private class data extends AsyncTask<String,Void, String>{
        //async task çünkü bu indirme işlemlerine bağlı olarak app in kitlenmemesi gerekiyor
        //bence bu sınıf multi thread calisiyor böylece ana thread calismaya devam ediyor.

        @Override
        protected String doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection httpURLConnection;
            try{
                url= new URL(strings[0]);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                InputStream inputStream=httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                int data= inputStreamReader.read();
                while (data>0){
                    char character=(char) data;
                    result+=character;
                    data= inputStreamReader.read();
                }
                return result;
            }catch (Exception ex){
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject=new JSONObject(s);
                String base=jsonObject.getString("base");
                Double input=Double.valueOf(inpDeci.getText().toString());

                String rates=jsonObject.getString("rates");

                JSONObject jsonObject1=new JSONObject(rates);
                tl=jsonObject1.getString("TRY");
                tryText.setText("TRY:"+Double.valueOf(tl)*input);


                String usd=jsonObject1.getString("USD");
                usdText.setText("USD:"+Double.valueOf(usd)*input);

                String inpMoney=jsonObject1.getString(inpText.getText().toString().toUpperCase());
                inputText.setText(inpText.getText().toString().toUpperCase()+":"+Double.valueOf(inpMoney)*input);

            }catch (Exception ex){

            }
        }


    }
}
