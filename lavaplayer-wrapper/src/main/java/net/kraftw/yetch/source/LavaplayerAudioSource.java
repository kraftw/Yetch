package net.kraftw.yetch.source;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import gg.moonflower.etched.api.sound.source.AudioSource;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class LavaplayerAudioSource implements AudioSource
{
    private final String url;

    public LavaplayerAudioSource(String url)
    {
        this.url = url;
    }

    @Override
    public CompletableFuture<InputStream> openStream()
    {
        return CompletableFuture.supplyAsync(() ->
        {
            AudioPlayer player = YouTubeSource.PLAYER_MANAGER.createPlayer();

            try
            {
                SyncAudioLoadResultHandler handler = new SyncAudioLoadResultHandler();

                YouTubeSource.PLAYER_MANAGER.loadItem(url, handler);

                AudioItem item = handler.get();

                if (item instanceof AudioTrack track)
                {
                    player.playTrack(track);
                }
                else if (item instanceof AudioPlaylist playlist && !playlist.getTracks().isEmpty())
                {
                    AudioTrack trackToPlay = playlist.getSelectedTrack();

                    if (trackToPlay == null)
                    {
                        trackToPlay = playlist.getTracks().getFirst();
                    }

                    player.playTrack(trackToPlay);
                }
            }
            catch (Exception e)
            {
                AudioSource.LOGGER.error("Failed to load YouTube track stream: {}", url, e);
            }

            return new LavaplayerInputStream(player);
        });
    }
}
