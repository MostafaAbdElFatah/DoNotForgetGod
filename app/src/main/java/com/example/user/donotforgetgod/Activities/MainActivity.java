package com.example.user.donotforgetgod.Activities;

import com.example.user.donotforgetgod.R;
import com.example.user.donotforgetgod.Services.AlarmReceiver;
import com.example.user.donotforgetgod.Services.Audio;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    Switch off_on;
    Spinner dropDown;
    TextView zakerPicker;

    private int flag = 0;
    private int zakerNumber;
    private int timeDuration;
    private int[] timesDuration;
    private String[] azakers;
    private String[] timesDurationNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timesDurationNames = getResources().getStringArray(R.array.times);
        azakers = getResources().getStringArray(R.array.azakers);
        timesDuration = new int[]{60000, 300000, 600000, 900000, 1200000, 1800000
                , 3600000, 43200000, 86400000};

        /**
         * Initialized component
         * */
        this.InitializeViews();
        setData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_share) {
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "تطبيق لا تنسي ذكر الله");
            intent.putExtra(Intent.EXTRA_TEXT, "تطبيق لا تنسي ذكر الله يساعدك في تذكر بعض الاذكار صوتا"+
                    "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(intent, "Please Choose One ......."));
            return true;
        }else if (id == R.id.action_Valuable) {
            final String appPackageName = getPackageName();  // getPackageName() طلبنا اسم الباكيج الخاص للتطبيق من هذا التطبيق, لو أردت تقييم تطبيق اخر ضع اسم الباكيج الخاصة به
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            return true;
        }else if (id == R.id.action_more) {
            String developerName = "MostafaAbdElFatah";  // your name in google play
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + developerName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q="+developerName)));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void InitializeViews(){

        dropDown = findViewById(R.id.timesDuration);
        ArrayAdapter<String> adapter;
        String language = Locale.getDefault().getDisplayLanguage();
        if (language.equals("English"))
            adapter = new ArrayAdapter<>(this
                    , R.layout.spinner_text, timesDurationNames);
        else
            adapter = new ArrayAdapter<>(this
                    , R.layout.spinner_text_ar, timesDurationNames);

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        dropDown.setAdapter(adapter);

        off_on = findViewById(R.id.switchButton);

        zakerPicker = findViewById(R.id.zaherPicker);

        /**
         * When Switch Change Value
         * Save Change
         * */

        off_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /**
                 * stop Alarm Manager
                 * */
                Log.v("TAG", "onCheckedChanged: " + flag);
                saveData();
                if (isChecked) {
                    off_on.setText(R.string.turn_on);
                } else {
                    off_on.setText(R.string.turn_off);
                }
                if (flag > 1) {
                    if (isChecked) {
                        MainActivity.this.startAZakers();
                    } else {
                        MainActivity.this.stopAZakers();
                    }
                }else
                    flag++;

            }

        });

        /**
         * When dropdowm Change Value
         * Save Change
         * */
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int postion, long l) {
                Log.v("TAG", "onItemSelected: " + flag);
                saveData();
                timeDuration = timesDuration[postion];
                if (flag >= 2) {
                    MainActivity.this.stopAZakers();
                    MainActivity.this.startAZakers();
                }else
                    flag++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    public void saveData(){
        SharedPreferences sharedPreferences=getSharedPreferences("Setting", MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("timeDuration", dropDown.getSelectedItemPosition());
        editor.putInt("ZakerNumber", zakerNumber);
        editor.putBoolean("checked", off_on.isChecked());
        editor.commit();
        setData();
    }

    public void setData(){
        SharedPreferences sharedPreferences = getSharedPreferences("Setting"
                , Context.MODE_PRIVATE);
        dropDown.setSelection(sharedPreferences.getInt("timeDuration", 0));
        zakerNumber = sharedPreferences.getInt("ZakerNumber", 0);
        String zakerText = azakers[zakerNumber];
        zakerPicker.setText(zakerText);
        off_on.setChecked(sharedPreferences.getBoolean("checked", false));
        timeDuration = timesDuration[dropDown.getSelectedItemPosition()];

    }
    void startAZakers(){
        if (Audio.isRunningAudio())
            return;
        else {
            Audio.playAudio(this);
        }
        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timeDuration, pi);
    }
    void stopAZakers(){
        Audio.releaseAudio();
        AlarmManager alarmManager=(AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    public void changeZakerText(View view) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(R.string.zekarAlertTitle)
                .setSingleChoiceItems(azakers, -1 , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        zakerPicker.setText(azakers[which]);
                        zakerNumber = which;
                        saveData();
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}