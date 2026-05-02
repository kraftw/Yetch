package net.kraftw.yetch.source;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.api.sound.download.SoundDownloadSource;
import gg.moonflower.etched.api.sound.source.AudioSource;
import gg.moonflower.etched.api.util.DownloadProgressListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class YouTubeSource implements SoundDownloadSource
{
    public static final AudioPlayerManager PLAYER_MANAGER = new DefaultAudioPlayerManager();
    private static final Component BRAND = Component.literal("YouTube").withStyle(style -> style.withColor(TextColor.fromRgb(0xFF0000)));

    static
    {
        PLAYER_MANAGER.getConfiguration().setOutputFormat(StandardAudioDataFormats.COMMON_PCM_S16_LE);

        PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER);
    }

    @Override
    public boolean isValidUrl(String url)
    {
        return url != null && (url.contains("youtube.com") || url.contains("youtu.be"));
    }

    @Override
    public String getApiName()
    {
        return "YouTube";
    }

    @Override
    public Collection<URL> resolveUrl(String url, @Nullable DownloadProgressListener listener, Proxy proxy)
    {
        try
        {
            return Collections.singletonList(URI.create("http://youtube.local.dummy").toURL());
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<TrackData> resolveTracks(String url, @Nullable DownloadProgressListener listener, Proxy proxy)
    {
        List<TrackData> tracks = new ArrayList<>();
        try
        {
            SyncAudioLoadResultHandler handler = new SyncAudioLoadResultHandler();

            PLAYER_MANAGER.loadItem(url, handler);

            AudioItem item = handler.get();

            if (item instanceof AudioTrack track)
            {
                tracks.add(new TrackData(url, track.getInfo().author, Component.literal(track.getInfo().title)));
            }
            else if (item instanceof AudioPlaylist playlist && !playlist.getTracks().isEmpty())
            {
                AudioTrack trackToPlay = playlist.getSelectedTrack();

                if (trackToPlay == null)
                {
                    trackToPlay = playlist.getTracks().getFirst();
                }

                tracks.add(new TrackData(url, trackToPlay.getInfo().author, Component.literal(trackToPlay.getInfo().title)));
            }
        }
        catch (Exception e)
        {
            AudioSource.LOGGER.error("Failed to fetch YouTube metadata for: {}", url, e);
        }
        return tracks;
    }

    @Override
    public Optional<String> resolveAlbumCover(String url, @Nullable DownloadProgressListener listener, Proxy proxy, ResourceManager resourceManager)
    {
        String videoId = extractVideoId(url);
        if (videoId != null)
        {
            return Optional.of("https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg");
        }
        return Optional.empty();
    }

    @Override
    public boolean isTemporary(String url)
    {
        return true;
    }

    @Override
    public Optional<Component> getBrandText(String url)
    {
        return Optional.of(BRAND);
    }

    private String extractVideoId(String url)
    {
        if (url.contains("v="))
        {
            return url.split("v=")[1].split("&")[0];
        }
        else if (url.contains("youtu.be/"))
        {
            return url.split("youtu.be/")[1].split("\\?")[0];
        }
        return null;
    }
}
