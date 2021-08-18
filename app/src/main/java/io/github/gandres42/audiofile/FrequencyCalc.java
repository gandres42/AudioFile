package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import org.jtransforms.fft.FloatFFT_1D;

public class FrequencyCalc {

    FloatFFT_1D fft;
    private AudioRecord record;
    private float[] audioData;
    float maxval;
    float minval;
    int maxindex;
    int previndex;

    int[] buffer;
    int buffer_index;

    public FrequencyCalc(int sampleRate, int bufferSize)
    {
        this.fft = new FloatFFT_1D(bufferSize);
        this.record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, bufferSize);
        this.audioData = new float[bufferSize];
        this.maxval = Integer.MIN_VALUE;
        this.minval = Integer.MAX_VALUE;
        this.maxindex = -1;
        this.previndex = -1;

        this.buffer = new int[4];
        this.buffer_index = 0;
        for (int i = 0; i < buffer.length; i++)
        {
            buffer[i] = -1;
        }

        record.startRecording();
    }

    public int listen(int[] tones)
    {
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.realForward(audioData);
        maxval = Integer.MIN_VALUE;
        minval = Integer.MAX_VALUE;

        for (int i = 0; i < tones.length; i++)
        {
            if (maxval < magnitude(audioData[tones[i] * 2], audioData[(tones[i] * 2) + 1]))
            {
                maxval = magnitude(audioData[tones[i] * 2], audioData[(tones[i] * 2) + 1]);
                maxindex = i;
            }
            if (minval > magnitude(audioData[tones[i] * 2], audioData[(tones[i] * 2) + 1]))
            {
                minval = magnitude(audioData[tones[i] * 2], audioData[(tones[i] * 2) + 1]);
            }
        }

        if (previndex != maxindex)
        {
            previndex = maxindex;
            return maxindex;
        }

        return -1;
    }

    public float magnitude(float real, float imag)
    {
        return (float)Math.sqrt(Math.pow(real, 2) + Math.pow(imag, 2));
    }
}
