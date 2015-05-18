import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import com.sun.media.sound.FFT;


public class FFTVis {
	
	private static final int RECORDER_BPP = 16;
	private static final int RECORDER_SAMPLERATE = 44100;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	public byte[] convertArray(int[] array) { 

        int minBufferSize = AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING,minBufferSize, AudioTrack.MODE_STREAM);

    byte[] newarray = new byte[array.length];
    for (int i = 0; i < array.length; i++) {
    newarray[i] = (byte) ((array[i]) & 0xFF);       }

        absNormalizedSignal = calculateFFT(newarray);
        return newarray;
    }

	private void asli(){

	            int counter = 0;
	            int data;
	            InputStream inputStream  = getResources().openRawResource(R.raw.b1);
	            DataInputStream dataInputStream = new DataInputStream(inputStream);
	            List<Integer> content = new ArrayList<Integer>(); 

	            try {
	                while ((data = dataInputStream.read()) != -1) {
	                    content.add(data);
	                    counter++; }
	            } catch (IOException e) {
	                e.printStackTrace();}

	                int[] b = new int[content.size()];
	                int cont = 0;
	                byte[] audio = convertArray(b);
	        }
	
	public double[] calculateFFT(byte[] signal)
    {           
        final int mNumberOfFFTPoints =1024;
        double mMaxFFTSample;
        double temp;
        Complex[] y;
        Complex[] complexSignal = new Complex[mNumberOfFFTPoints];
        double[] absSignal = new double[mNumberOfFFTPoints/2];

        for(int i = 0; i < mNumberOfFFTPoints; i++){
            temp = (double)((signal[2*i] & 0xFF) | (signal[2*i+1] << 8)) / 32768.0F;
            complexSignal[i] = new Complex(temp,0.0);
        }

        y = FFT.fft(complexSignal);

        mMaxFFTSample = 0.0;
        mPeakPos = 0;
        for(int i = 0; i < (mNumberOfFFTPoints/2); i++)
        {
             absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
             if(absSignal[i] > mMaxFFTSample)
             {
                 mMaxFFTSample = absSignal[i];
                 mPeakPos = i;
             } 
        }

        return absSignal;

    }
}
