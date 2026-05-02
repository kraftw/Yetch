package net.kraftw.yetch.source;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.concurrent.CompletableFuture;

public class SyncAudioLoadResultHandler extends CompletableFuture<AudioItem> implements AudioLoadResultHandler
{
    @Override
    public void trackLoaded(AudioTrack track)
    {
        this.complete(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist)
    {
        this.complete(playlist);
    }

    @Override
    public void noMatches()
    {
        this.complete(null);
    }

    @Override
    public void loadFailed(FriendlyException exception)
    {
        this.completeExceptionally(exception);
    }
}
