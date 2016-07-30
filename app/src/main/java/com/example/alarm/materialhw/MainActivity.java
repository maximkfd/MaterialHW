package com.example.alarm.materialhw;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alarm.materialhw.fragments.LoginFragment;
import com.example.alarm.materialhw.fragments.MainFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.*;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    HttpCookie jsessionid;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    MainFragment mMainFragment;
    ProgressBar progress;
    SharedPreferences preferences;

    JSONObject eregister;
    CookieManager cookieManager = new CookieManager();
    boolean logged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
//        initFAB();
        initDrawer();
        initNavigationView();


        preferences = getPreferences(MODE_PRIVATE);
        logged = preferences.getBoolean("logged", false);
        if (!logged){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, LoginFragment.getInstance(getApplicationContext()));
            transaction.commit();
        } else {
            loadUserData(true);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void loadUserData(boolean changeFragment) {
        if (changeFragment) {
            mMainFragment = MainFragment.getInstance(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, mMainFragment)
                    .commit();
        }
        String login = preferences.getString("login", "");
        String passwd = preferences.getString("passwd", "");
        String rating = preferences.getString("rating", "");
        String name = preferences.getString("full name", "");
        User.getInstance().setName(name);
        User.getInstance().getCurrentRating().rate = rating;
        new login().execute(login, passwd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
//        if (id == R.id.action_refresh){
//            loadUserData(false);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, LoginFragment.getInstance(getApplicationContext()))
                    .commit();
        } else if (id == R.id.nav_rating50) {
            Toast.makeText(this, "Не реализовано", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_book_101) {
            Toast.makeText(this, "Не реализовано", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_developers) {
            Toast.makeText(this, "Не реализовано", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_protocol) {
            Toast.makeText(this, "Не реализовано", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_register) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, mMainFragment)
                    .commit();
        } else if (id == R.id.nav_settings){
            Toast.makeText(this, "Не реализовано", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void saveUser(View view){
        cookieManager.getCookieStore().removeAll();
        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("login",((EditText) findViewById(R.id.editLogin)).getText().toString());
        editor.putString("passwd",((EditText) findViewById(R.id.editPassword)).getText().toString());
        editor.putBoolean("logged", true);
        editor.apply();

        getSupportFragmentManager().popBackStack();
        loadUserData(true);
        new LoadFullName().execute();
        logged = true;
    }

    private class loadInfo extends AsyncTask<URL, Integer, JSONObject>{
        @Override
        protected void onPreExecute() {

            ((TextView)findViewById(R.id.nav_name)).setText(User.getInstance().getName());
            if (progress.getVisibility() != View.VISIBLE){
                progress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected JSONObject doInBackground(URL... urls) {
            InputStream is = null;
            int resp = -1;

            try {
                URLConnection connection = urls[0].openConnection();
                HttpsURLConnection cnctn = (HttpsURLConnection)  connection;
                resp = cnctn.getResponseCode();
                is = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStreamReader reader = new InputStreamReader(is);
            char[] buffer = new char[256];
            int rc;
            StringBuilder sb = new StringBuilder();
            Log.d("Https Response code", resp + "");
            try {
                while ((rc = reader.read(buffer)) != -1)
                    sb.append(buffer, 0, rc);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject object = null;
            try {
                object = new JSONObject(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return object;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            eregister = jsonObject;
            progress.setVisibility(View.INVISIBLE);
            new LoadRating().execute();
            loaded();
        }

    }
    private class login extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            java.net.CookieHandler.setDefault(cookieManager);
            progress.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //call private
            try {
                new loadInfo().execute(new URL("https://de.ifmo.ru/api/private/eregister"));
                Log.d("Cookies", cookieManager.getCookieStore().getCookies().toString());
                for (HttpCookie c :
                        cookieManager.getCookieStore().getCookies()) {
                    if (c.getName().equals("JSESSIONID")){
                        jsessionid = c;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String login = params[0];
            String password = params[1];
            String args;
            args = "Rule=LOGON&LOGIN=" +
                    login +
                    "&PASSWD=" +
                    password;
            try {
                URL url = new URL("https://de.ifmo.ru/servlet/");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                connection.setDoOutput(true);
                ByteArrayOutputStream byteStream=new
                        ByteArrayOutputStream(400);
                PrintWriter out=new PrintWriter(byteStream,true);
                out.write(args);
                out.flush();

                connection.setRequestMethod("POST");
//        connection.setRequestProperty("User-Agent", USER_AGENT); //установка свойств запроса
                connection.setRequestProperty("Content-Length", String.valueOf(byteStream.size()));
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");//установка свойств запроса
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                byteStream.writeTo(connection.getOutputStream());

                int jresponsAeCode = connection.getResponseCode(); //получение кода ответа(200)
//                Log.d("DLC", responseCode + "");
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                BufferedReader inputStream = new BufferedReader(reader);
                String inputLinne;
                StringBuilder builder = new StringBuilder();
                while ((inputLinne = inputStream.readLine()) != null){
                    builder.append(inputLinne);
                }
                SpannableStringBuilder sp = (SpannableStringBuilder) Html.fromHtml(builder.toString());
                Log.d("Res", builder.toString());

            } /*catch (MalformedURLException e) {
                e.printStackTrace();
            }*/ catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

    }
    private class LoadRating extends AsyncTask<Void, Integer, List<Rating>>{
        @Override
        protected List<Rating> doInBackground(Void... params) {

            List<Rating> rating = new ArrayList<>();
            try {
                Document doc = Jsoup.connect("https://de.ifmo.ru/servlet/distributedCDE?Rule=REP_EXECUTE_PRINT&REP_ID=1441&PARAMS=&IN_ADMIN=0").get();
                Elements td = doc.select("td");
                Log.d("Jsoup", td.toString());
                String rate;
                String fuckulty;
                int i = 6;
                while (td.get(i).childNodeSize() == 1){
                    rate = td.get(i).childNode(0).outerHtml();
                    fuckulty = td.get(i-2).childNode(0).outerHtml();
                    i+=3;
                    rating.add(new Rating(rate, fuckulty));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return rating;
        }


        @Override
        protected void onPostExecute(List<Rating> ratings) {
            User.getInstance().setCurrentRating(ratings.get(ratings.size()-1));
            ((TextView) findViewById(R.id.nav_rating)).setText(User.getInstance().getCurrentRating().rate);
        }
    }
    private class LoadFullName extends AsyncTask<Void, Integer, String >{
        @Override
        protected String  doInBackground(Void... params) {
            String out = "";
            try {
                Document doc = Jsoup.connect("https://de.ifmo.ru/servlet/distributedCDE?Rule=ShowWorkSpace").get();
                Elements td = doc.select("td.fio");
                out = (td.get(0).childNode(0).outerHtml());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return out;
        }


        @Override
        protected void onPostExecute(String in) {
            User.getInstance().setName(in);

        }
    }
    public void loaded(){
        Toast.makeText(MainActivity.this, "Done.. maybe", Toast.LENGTH_SHORT).show();
        try {
            JSONArray years = eregister.getJSONArray("years");
            for (int j = 0; j < years.length(); j++)
            {
                JSONObject currentYear = years.getJSONObject(j);
                JSONArray subjects = currentYear.getJSONArray("subjects");
                for (int i = 0; i<subjects.length(); i++)
                            {
                                Subject.subjects.add(new Subject(subjects.getJSONObject(i)));
                            }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mMainFragment.updateCards();
        Log.d("Subject parse", Subject.subjects.get(1).getName());
    }
    private void initNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progress = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onBackPressed() {
        Log.d("Back", getSupportFragmentManager().getBackStackEntryCount()+"");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (User.getInstance().getCurrentRating()!= null){
            preferences.edit()
                    .putString("rating" ,User.getInstance().getCurrentRating().rate)
                    .apply();
        }
        if (User.getInstance().getName() != null)
        {
            preferences
                    .edit()
                    .putString("full name", User.getInstance().getName())
                    .apply();
        }
        super.onDestroy();
    }
}

