package io.github.gandres42.audiofile;

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

public class ReceiveFragment extends Fragment {

    boolean active = true;
    private Thread listen = new Thread(new Runnable() {
        @Override
        public void run() {
            int i = 0;
            int tempVal = 0;
            int[] buffer = new int[1000];
            FrequencyCalc calc = new FrequencyCalc(44100, 1024);

            while (active)
            {
                tempVal = calc.listen(.5, 450,452, 454, 456, 458, 460);

                if (tempVal != 0)
                {
                    buffer[i] = tempVal;
                    i++;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView data = (TextView)getActivity().findViewById(R.id.text_data);
                        String arr = "";
                        for (int i = 0; i < buffer.length; i++)
                        {
                            if (buffer[i] != 0)
                            {
                                arr += buffer[i];
                            }
                        }
                        if (data != null)
                        {
                            data.setText(arr);
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

}