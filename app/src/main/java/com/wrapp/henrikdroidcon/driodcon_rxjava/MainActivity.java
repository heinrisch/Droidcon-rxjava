package com.wrapp.henrikdroidcon.driodcon_rxjava;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class MainActivity extends Activity {

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView3 = (TextView) findViewById(R.id.text3);

        final PublishSubject<String> subject = PublishSubject.create();

        subject.observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            private int count;

            @Override
            public void call(String s) {
                count++;
                textView1.setText("Regular is " + count);
            }
        });

        subject.buffer(5, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<String>>() {
            private int count;

            @Override
            public void call(List<String> strings) {
                for (String s : strings) {
                    count++;
                }
                textView2.setText("Buffer is " + count);
            }
        });

        subject.debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            private int count;

            @Override
            public void call(String s) {
                count++;
                textView3.setText("Debounce is " + count);
            }
        });


        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    private int i;

                    @Override
                    public void run() {
                        subject.onNext("hi!");
                        if (++i < 200) {
                            handler.postDelayed(this, 100);
                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
