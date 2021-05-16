package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.text.DecimalFormat;

import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HammingWindow;

public class ReceiveFragment extends Fragment {

    boolean active = true;
    int[] tones = {440, 445, 450, 455, 460, 465, 470, 475};

    private Thread listen = new Thread(new Runnable() {
        @Override
        public void run() {
            int tempVal = 0;
            FrequencyCalc calc = new FrequencyCalc(44100, 1024);
            String msg = "";

            while (active)
            {
                tempVal = calc.listen2(1, tones);
                if (tempVal != 0)
                {
                    msg = msg + tempVal;
                }

                String finalMsg = msg;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView data = (TextView)getActivity().findViewById(R.id.text_data);
                        if (data != null)
                        {
                            data.setText(finalMsg);
                        }
                    }
                });
            }
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_receive, container, false);
        listen.start();
        return root;
    }

    public void onPause() {

        super.onPause();
        active = false;
    }

    public void onResume()
    {
        super.onResume();
        active = true;
    }

    public void listen3()
    {

    }
}