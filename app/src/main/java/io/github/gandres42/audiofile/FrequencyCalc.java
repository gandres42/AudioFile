package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import be.tarsos.dsp.util.fft.FFT;

public class FrequencyCalc {

    private int refreshRate;
    private int bufferSize;
    private double[] frequencyKey;
    private FFT fft;
    private AudioRecord record;
    private float[] audioData;
    private int previous = 0;

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
        this.record = new AudioRecord(0, refreshRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, bufferSize);
        this.audioData = new float[bufferSize];
        record.startRecording();
    }

    public double getIndexModulus(int i)
    {
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.forwardTransform(audioData);
        return fft.modulus(audioData, i);
    }

    public double getIndexDifference(int i, int j)
    {
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.forwardTransform(audioData);
        return fft.modulus(audioData, i) - fft.modulus(audioData, j);
    }

    public int listen(double MIN, int i, int j)
    {
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.forwardTransform(audioData);
        float mod = fft.modulus(audioData, i) - fft.modulus(audioData, j);

        //Log.i("freqtemp", "" + mod);

        if (mod > MIN && previous == 0)
        {
            previous = -1;
            return -1;
        }
        else if (mod < (-1 * MIN) && previous == 0)
        {
            previous = 1;
            return 1;
        }
        else if ((mod < MIN && mod > -MIN) && previous != 0)
        {
            previous = 0;
        }
        return 0;
    }

    public double getIndexHz(int i)
    {
        return fft.binToHz(i, refreshRate);
    }
}
