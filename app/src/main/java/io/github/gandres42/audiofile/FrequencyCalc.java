package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.Arrays;

import be.tarsos.dsp.util.fft.FFT;

public class FrequencyCalc {

    private FFT fft;
    private AudioRecord record;
    private float[] audioData;
    float maxval;
    float minval;
    int maxindex;
    int previndex;

    int[] buffer;
    int buffer_index;

    public FrequencyCalc(int refreshRate, int bufferSize)
    {
        this.fft = new FFT(bufferSize);
        this.record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, refreshRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, bufferSize);
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
        //read into buffer, perform fft
        record.read(audioData, 0, audioData.length, AudioRecord.READ_BLOCKING);
        fft.forwardTransform(audioData);
        maxval = Integer.MIN_VALUE;
        minval = Integer.MAX_VALUE;

        //find max frequency
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

        /*if (previndex != maxindex && maxval > Math.pow(minval, .6))
        {
            previndex = maxindex;
            return maxindex;
        }*/

        //check for flush buffer
        if (buffer_index == 4)
        {
            buffer_index = 0;
        }
        buffer[buffer_index] = maxindex;
        buffer_index++;

        if (buffer[0] == buffer[1] && buffer[1] == buffer[2] && buffer[2] == buffer[3] && buffer[0] != previndex)
        {
            previndex = maxindex;
            return maxindex;
        }

        Log.d("maxindex", "" + maxindex + " " + Arrays.toString(buffer));

        return -1;
    }
}
