package io.github.gandres42.audiofile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Recieve extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FrequencyCalc calc = new FrequencyCalc(96000, 4096);
                Log.i("frequency", "" + calc.getIndexHz(850));
                Log.i("frequency", "" + calc.getIndexHz(851));

                int i = 0;
                int previous = 0;
                double diff = 0;
                int[] buffer = new int[1000];

                while (i < buffer.length)
                {
                    diff = calc.getIndexDifference(850, 851);
                    if (diff < -1 && previous == 0)
                    {
                        buffer[i] = -1;
                        previous = -1;
                        i++;
                    }
                    else if (diff > 1 && previous == 0)
                    {
                        buffer[i] = 1;
                        previous = 1;
                        i++;
                    }
                    else if ((diff < 1 && diff > -1) && previous != 0)
                    {
                        previous = 0;
                    }

                    double finalDiff = diff;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DecimalFormat df = new DecimalFormat("####0.00");
                            //display.setText("" + df.format(finalDiff));
                            String arr = "";
                            for (int i = 0; i < buffer.length; i++)
                            {
                                if (buffer[i] != 0)
                                {
                                    arr += Math.max(buffer[i], 0);
                                }
                            }
                            //count.setText(arr);
                        }
                    });
                }
            }
        }).start();
    }
}