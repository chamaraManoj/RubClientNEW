package com.example.clientside;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocektReceiveFile implements Runnable {

    private Socket socket;
    byte[] imageFrame;
    ReceivedBufferUpdateTask  updateResults;
    int thread;
    int chunk;

    public SocektReceiveFile(Socket socket, ReceivedBufferUpdateTask updateMainThreadBuffer,int thread, int chunk){
        this.socket = socket;
        this.updateResults = updateMainThreadBuffer;
        this.thread = thread;
        this.chunk = chunk;
        ModuleManager.getDownloadManager().getMainThreadExecutor().execute(updateResults);
    }

    @Override
    public void run(){
        boolean allFrameReceived = getFIle();
        if(allFrameReceived) {
            decodeFrame();
        }
        updateResults.setBackgroundMsg(imageFrame);
    }

    public boolean getFIle(){
        int bytesRead = 0;
        int bytesAvailable = 0;

        int totByteRead = 0;
        int[] layerLength = new int[Constants.NUM_OF_LAYERS];
        boolean[] frameReceived = new boolean[Constants.NUM_OF_LAYERS];
        int tempCounter;


        imageFrame = new byte[600000];
        if (socket != null) {
            //Log.d("Debug", "1");

            try {
                //Log.d("Debug", "2");
                InputStream inputStream = socket.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                bytesRead = bufferedInputStream.read(imageFrame, 0, 8);

                for (tempCounter = 0; tempCounter < Constants.NUM_OF_LAYERS; tempCounter++) {
                    layerLength[tempCounter] = (imageFrame[tempCounter*2] & 0xFF) << 8 | imageFrame[tempCounter * 2 + 1] & 0xFF;
                }
                totByteRead = bytesRead;

                //Log.d("Debug", String.valueOf(bytesRead));
                int tempByteRead=0;
                for (tempCounter = 0; tempCounter < Constants.NUM_OF_LAYERS; tempCounter++) {
                    //Log.d("Debug", "Length " + String.valueOf(layerLength[tempCounter]) + "\n");
                    int endOfByte = totByteRead + layerLength[tempCounter];
                    //Log.d("Debug", "End Byte " + String.valueOf(endOfByte) + "\n");
                    do {
                        tempByteRead = bufferedInputStream.read(imageFrame, totByteRead, endOfByte - totByteRead);
                        if (tempByteRead >= 0) totByteRead += tempByteRead;
                        //Log.d("Debug", "Temp Byte " + String.valueOf(tempByteRead) + "\n");
                    } while (tempByteRead > 0);

                    if(endOfByte - totByteRead == 0) {
                        frameReceived[tempCounter] = true;
                    }else{
                        frameReceived[tempCounter] = false;
                    }
                    tempByteRead=0;
                }
                Log.d("Debug", "Thread "+String.valueOf(thread)+"    Chunk "+String.valueOf(chunk)+ "     Tot " + String.valueOf(totByteRead) + "\n");

                //Log.d("Debug", "Reply Byte " +bytesRead+ "\n");
                socket.close();
                //Log.d("Debug", "3");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /*If any of the frame is not received then return false*/
        for(tempCounter =0;tempCounter<Constants.NUM_OF_LAYERS;tempCounter++){
            if(frameReceived[tempCounter]==false)
                return false;
        }
        return true;
    }

    public void decodeFrame(){
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setFeatureEnabled(MediaFormat.MIMETYPE_VIDEO_HEVC,true);
        mediaFormat.setFeatureEnabled(MediaCodecInfo.CodecCapabilities.FEATURE_SecurePlayback,true);

        String nameOfDecoder = mediaCodecList.findDecoderForFormat(mediaFormat);

        try {
            MediaCodec mediaCodec = MediaCodec.createByCodecName(nameOfDecoder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


