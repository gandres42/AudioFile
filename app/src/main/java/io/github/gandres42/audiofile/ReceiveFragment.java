package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.os.Bundle;
import android.media.AudioRecord;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import uk.me.berndporr.kiss_fft.KISSFastFourierTransformer;

public class ReceiveFragment extends Fragment {

    boolean active = true;
    int[] tones = {440, 445, 450, 455, 460, 465, 470, 475};

    private Thread listen = new Thread(new Runnable() {
        @Override
        public void run() {
            int tempVal;
            FrequencyCalc calc = new FrequencyCalc(44100, 1024);
            String msg = "";

            while(true)
            {
                tempVal = calc.listen(tones);
                if (tempVal != -1)
                {
                    msg = msg + tempVal;
                }
                if (tempVal == 7)
                {
                    msg = msg + '\n';
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
}