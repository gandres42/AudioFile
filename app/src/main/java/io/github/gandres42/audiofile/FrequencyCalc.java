package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import be.tarsos.dsp.util.fft.FFT;

public class FrequencyCalc {

    private int refreshRate;
    private int bufferSize;
    private double[] frequencyKey;
    private FFT fft;
    private AudioRecord record;
    private float[] audioData;
    private boolean previousEmpty = true;

    public FrequencyCalc(int refreshRate, int bufferSize)
    {
        this.refreshRate = refreshRate;
        this.bufferSize = bufferSize;
        this.frequencyKey = new double[bufferSize];
        this.fft = new FFT(bufferSize);
        for (int i = 0; i < bufferSize; i++)
        {
            frequencyKey[i] = fft.binToHz(i, refreshRate);
        }
        this.record = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, refreshRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, bufferSize);
        this.audioData = new float[bufferSize];
        record.startRecording();
    }
    public double getHzMod(int i)
    {
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.forwardTransform(audioData);
        return audioData[i];
    }

    public int listen(double MIN, int a, int b, int c, int d, int e, int f)
    {
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.forwardTransform(audioData);
        float mod1 = fft.modulus(audioData, a) - fft.modulus(audioData, b);
        float mod2 = fft.modulus(audioData, c) - fft.modulus(audioData, d);
        float mod3 = fft.modulus(audioData, e) - fft.modulus(audioData, f);

        if (mod1 > MIN && previousEmpty)
        {
            previousEmpty = false;
            return 1;
        }
        else if (mod1 < (-1 * MIN) && previousEmpty)
        {
            previousEmpty = false;
            return 2;
        }
        else if (mod2 > MIN && previousEmpty)
        {
            previousEmpty = false;
            return 3;
        }
        else if (mod2 < (-1 * MIN) && previousEmpty)
        {
            previousEmpty = false;
            return 4;
        }
        else if (mod3 > MIN && previousEmpty)
        {
            previousEmpty = false;
            return 5;
        }
        else if (mod3 < (-1 * MIN) && previousEmpty)
        {
            previousEmpty = false;
            return 6;
        }
        else if ((mod1 < MIN && mod1 > -MIN) && (mod2 < MIN && mod2 > -MIN) && (mod3 < MIN && mod3 > -MIN) && !previousEmpty)
        {
            previousEmpty = true;
        }
        return 0;
    }

    public double getIndexHz(int i)
    {
        return fft.binToHz(i, refreshRate);
    }
}
