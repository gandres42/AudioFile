package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SendFragment extends Fragment {

    int[] tones = {440, 444, 448, 452, 456, 460, 464, 468};
    private boolean active;
    private int count;
    private AudioTrack track;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedsInstanceState) {
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        this.active = true;
        this.count = (int)(44100.0 * 2.0 * (100/*ms*/ / 1000.0)) & ~1;

        track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);
        track.play();
       
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (active)
                {
                    for (int i = 0; i < tones.length; i++)
                    {
                        writeTone(track, binToHz(tones[i], 44100));
                    }
                }
            }
        }).start();

        return root;
    }

    public double binToHz(int n, int sample_rate)
    {
        return (n * ((float)sample_rate / (float)1024));
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