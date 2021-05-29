package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import be.tarsos.dsp.util.fft.FFT;

public class SendFragment extends Fragment {
    FFT fft = new FFT(1024);
    private boolean active;
    //450, 454, 458, 462, 466, 470, 474, 478
    private double[] frequencies = {
            fft.binToHz(440, 44100),
            fft.binToHz(445, 44100),
            fft.binToHz(450, 44100),
            fft.binToHz(455, 44100),
            fft.binToHz(460, 44100),
            fft.binToHz(465, 44100),
            fft.binToHz(470, 44100),
            fft.binToHz(475, 44100)
    };

    private double safetyFrequency = fft.binToHz(435, 44100);

    private int count;
    private AudioTrack track;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedsInstanceState) {
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        this.active = true;
        this.count = (int)(44100.0 * 2.0 * (40/*ms*/ / 1000.0)) & ~1;

        track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);
        track.play();
       
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (active)
                {
                    for (int i = 0; i < frequencies.length; i++)
                    {
                        writeTone(track, frequencies[i]);
                        //writeTone(track, 0);
                    }
                }
            }
        }).start();

        return root;
    }

    private void writeTone(AudioTrack track, double freqHz)
    {
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(Math.PI * i / (44100.0 / freqHz)) * 0x7FFF * (-Math.pow((((1.0/(count/2.0)) * i) - 1), 8) + 1));
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
        track.write(samples, 0, count);
    }
    public void onPause() {

        super.onPause();
        active = false;
    }

    public void onResume() {
        super.onResume();
        active = true;
    }
}