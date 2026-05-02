package net.kraftw.yetch;

import gg.moonflower.etched.api.sound.download.SoundDownloadSource;
import gg.moonflower.etched.api.sound.source.AudioSource;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LavaplayerLoader
{
    private static URLClassLoader classLoader;
    private static SoundDownloadSource youtubeSource;

    public static SoundDownloadSource getYouTubeSource()
    {
        if (classLoader == null) load();
        if (youtubeSource == null)
        {
            try
            {
                youtubeSource = (SoundDownloadSource) Class.forName("net.kraftw.yetch.source.YouTubeSource", true, classLoader)
                        .getDeclaredConstructor().newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return youtubeSource;
    }

    public static AudioSource createAudioSource(String url)
    {
        if (classLoader == null) load();
        try
        {
            return (AudioSource) Class.forName("net.kraftw.yetch.source.LavaplayerAudioSource", true, classLoader)
                    .getConstructor(String.class).newInstance(url);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static void load()
    {
        try
        {
            Path cacheDir = Minecraft.getInstance().gameDirectory.toPath().resolve("etched_youtube_internal");
            if (!Files.exists(cacheDir)) Files.createDirectories(cacheDir);
            Path libJar = cacheDir.resolve("lavaplayer.jar");

            try (InputStream in = LavaplayerLoader.class.getResourceAsStream("/assets/yetch/lavaplayer/lavaplayer.jar"))
            {
                if (in == null) throw new RuntimeException("Could not find lavaplayer.jar in mod assets!");
                Files.copy(in, libJar, StandardCopyOption.REPLACE_EXISTING);
            }

            classLoader = new URLClassLoader(new URL[]{libJar.toUri().toURL()}, LavaplayerLoader.class.getClassLoader());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
