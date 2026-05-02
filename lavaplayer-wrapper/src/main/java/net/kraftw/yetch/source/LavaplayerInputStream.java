package net.kraftw.yetch.source;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class LavaplayerInputStream extends InputStream
{
    private final AudioPlayer player;
    private byte[] currentFrameData;
    private int frameIndex = 0;

    private final byte[] header;
    private int headerIndex = 0;

    public LavaplayerInputStream(AudioPlayer player)
    {
        this.player = player;
        this.header = createWavHeader();
    }

    private byte[] createWavHeader()
    {
        int sampleRate = 48000;
        short channels = 2;
        short bitsPerSample = 16;
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        short blockAlign = (short) (channels * bitsPerSample / 8);

        byte[] header = new byte[44];
        ByteBuffer buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);

        buffer.put("RIFF".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(0x7FFFFFFF);
        buffer.put("WAVE".getBytes(StandardCharsets.US_ASCII));
        buffer.put("fmt ".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(16);
        buffer.putShort((short) 1);
        buffer.putShort(channels);
        buffer.putInt(sampleRate);
        buffer.putInt(byteRate);
        buffer.putShort(blockAlign);
        buffer.putShort(bitsPerSample);
        buffer.put("data".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(0x7FFFFFFF);

        return header;
    }

    @Override
    public int read()
    {
        if (headerIndex < header.length)
        {
            return header[headerIndex++] & 0xFF;
        }

        if (currentFrameData == null || frameIndex >= currentFrameData.length)
        {
            AudioFrame frame = player.provide();
            if (frame != null)
            {
                currentFrameData = frame.getData();
                frameIndex = 0;
            }
            else
            {
                if (player.getPlayingTrack() == null)
                {
                    return -1;
                }
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException ignored)
                {

                }
                return read();
            }
        }

        return currentFrameData[frameIndex++] & 0xFF;
    }

    @Override
    public void close()
    {
        player.stopTrack();
        player.destroy();

        try
        {
            super.close();
        }
        catch (Exception ignored)
        {

        }
    }
}
