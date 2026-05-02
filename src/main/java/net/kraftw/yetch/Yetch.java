package net.kraftw.yetch;

import gg.moonflower.etched.api.sound.download.SoundSourceManager;
import net.neoforged.fml.common.Mod;

@Mod(Yetch.MODID)
public class Yetch
{
    public static final String MODID = "yetch";

    public Yetch()
    {
        SoundSourceManager.registerSource(LavaplayerLoader.getYouTubeSource());
    }
}
