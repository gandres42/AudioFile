package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import be.tarsos.dsp.util.fft.BlackmanHarrisNuttall;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HammingWindow;
import be.tarsos.dsp.util.fft.RectangularWindow;
import be.tarsos.dsp.util.fft.WindowFunction;

public class FrequencyCalc {

    private int refreshRate;
    private FFT fft;
    private AudioRecord record;
    private float[] audioData;
    private boolean previousEmpty = true;
    float[] mods;
    float maxval = 0;
    float minval = 0;
    int maxindex;
    int previndex = -1;

    public FrequencyCalc(int refreshRate, int bufferSize)
    {
        this.mods = new float[4];
        this.refreshRate = refreshRate;
        this.fft = new FFT(bufferSize, new HammingWindow());
        this.record = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, refreshRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, 1024);
        this.audioData = new float[bufferSize];
        record.startRecording();
    }
    public double getHzMod(int i)
    {
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.forwardTransform(audioData);
        return audioData[i];
    }

    public int listen2(double MIN, int[] tones)
    {
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.forwardTransform(audioData);
        for (int i = 0; i < mods.length; i++)
        {
            mods[i] = fft.modulus(audioData, tones[i * 2]) - fft.modulus(audioData, tones[(i * 2) + 1]);
            Log.i("freq", "" + (fft.modulus(audioData, tones[i * 2]) - fft.modulus(audioData, tones[(i * 2) + 1])));
        }

        for (int i = 0; i < mods.length; i++)
        {
            if (mods[i] > MIN && previousEmpty)
            {
                previousEmpty = false;
                return (i * 2) + 1;
            }
            else if (mods[i] < (-1 * MIN) && previousEmpty)
            {
                previousEmpty = false;
                return (i * 2) + 2;
            }
        }

        if (!previousEmpty)
        {
            previousEmpty = true;
        }

        return 0;
    }

    public int listen(int[] tones)
    {
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.forwardTransform(audioData);
        maxval = Integer.MIN_VALUE;
        minval = Integer.MAX_VALUE;

        for (int i = 0; i < tones.length; i++)
        {
            if (maxval < fft.modulus(audioData, tones[i]))
            {
                maxval = fft.modulus(audioData, tones[i]);
                maxindex = i;
            }
            if (minval > fft.modulus(audioData, tones[i]))
            {
                minval = fft.modulus(audioData, tones[i]);
            }
        }

        if (previndex != maxindex && Math.pow(minval, .5) < maxval)
        {
            previndex = maxindex;
            return maxindex;
        }

        return -1;
    }

    public double getIndexHz(int i)
    {
        return fft.binToHz(i, refreshRate);
    }
}
