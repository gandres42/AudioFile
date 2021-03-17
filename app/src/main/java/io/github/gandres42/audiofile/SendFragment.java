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

import be.tarsos.dsp.util.fft.FFT;

public class SendFragment extends Fragment {
    FFT fft = new FFT(1024);
    private boolean active;
    private final double freq1 = fft.binToHz(450, 44100);
    private final double freq2 = fft.binToHz(451, 44100);
    private final double freq3 = fft.binToHz(452, 44100);
    private final double freq4 = fft.binToHz(453, 44100);
    private final double freq5 = fft.binToHz(454, 44100);
    private final double freq6 = fft.binToHz(455, 44100);

    private int count;
    private AudioTrack track1;
    private AudioTrack track2;
    private AudioTrack track3;
    private AudioTrack track4;
    private AudioTrack track5;
    private AudioTrack track6;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        this.active = true;

        this.count = (int)(44100.0 * 2.0 * (70/*ms*/ / 1000.0)) & ~1;
        track1 = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);
        track2 = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);
        track3 = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);
        track4 = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);
        track5 = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);
        track6 = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, count * (Short.SIZE / 8), AudioTrack.MODE_STREAM);

        track1.play();
        track2.play();
        track3.play();
        track4.play();
        track5.play();
        track6.play();



        new Thread(new Runnable() {
            @Override
            public void run() {
                while (active)
                {
                    writeTone(track1, freq1);
                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    writeTone(track2, freq2);
                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    writeTone(track3, freq3);
                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    writeTone(track4, freq4);
                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    writeTone(track5, freq5);
                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    writeTone(track6, freq6);
                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
            short sample = (short)(Math.sin(Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
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