package net.kraftw.yetch.mixin;

import gg.moonflower.etched.api.sound.download.SoundSourceManager;
import gg.moonflower.etched.api.sound.source.AudioSource;
import gg.moonflower.etched.api.util.DownloadProgressListener;
import net.kraftw.yetch.LavaplayerLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundSourceManager.class)
public class MixinSoundSourceManager
{
    @Inject(method = "getAudioSource", at = @At("HEAD"), cancellable = true, remap = false)
    private static void interceptYouTubeAudio(String url, DownloadProgressListener listener, Proxy proxy, AudioSource.AudioFileType type, CallbackInfoReturnable<CompletableFuture<AudioSource>> cir)
    {
        if (url != null && (url.contains("youtube.com") || url.contains("youtu.be")))
        {
            cir.setReturnValue(CompletableFuture.supplyAsync(() ->
            {
                return LavaplayerLoader.createAudioSource(url);
            }));
        }
    }
}
