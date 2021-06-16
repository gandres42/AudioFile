package io.github.gandres42.audiofile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HammingWindow;

public class FrequencyCalc {

    private FFT fft;
    private AudioRecord record;
    private float[] audioData;
    float maxval;
    float minval;
    int maxindex;
    int previndex;

    public FrequencyCalc(int refreshRate, int bufferSize)
    {
        this.fft = new FFT(bufferSize, new HammingWindow());
        this.record = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, refreshRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, 1024);
        this.audioData = new float[bufferSize];
        this.maxval = Integer.MIN_VALUE;
        this.minval = Integer.MAX_VALUE;
        this.maxindex = -1;
        this.previndex = -1;
        record.startRecording();
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

        if (previndex != maxindex && maxval > Math.pow(minval, .6))
        {
            previndex = maxindex;
            return maxindex;
        }

        return -1;
    }
}
