package com.bbsvfx.bbsvfx.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.cubic.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Emoticons ({@code emoticons/alex}, {@code emoticons/steve} and the {@code *_simple} variants) are
 * BOBJ models. BBS loads every model asynchronously: {@code getModel} plants a {@code null} placeholder
 * and a worker fills it later. The form editor's {@code startEdit} and the first world/UI frames read
 * that placeholder — empty bone list, no body-part attachments, invisible mesh. A failed worker load
 * then leaves {@code null} in the map forever.
 *
 * <p>On the render thread, load emoticons synchronously the first time they're asked for so the editor
 * and the mesh see a real {@link ModelInstance}. Failures are remembered so a broken file doesn't hitch
 * every frame. Other models keep the async path.</p>
 */
@Mixin(value = ModelManager.class, remap = false)
public abstract class BOBJEmoticonsLoadMixin
{
    @Shadow
    public Map<String, ModelInstance> models;

    @Shadow
    public abstract ModelInstance loadModel(String id);

    @Unique
    private final Set<String> bbsvfx$failed = new HashSet<>();

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void bbsvfx$loadEmoticonsNow(String id, CallbackInfoReturnable<ModelInstance> cir)
    {
        if (id == null || !id.startsWith("emoticons/") || !RenderSystem.isOnRenderThread())
        {
            return;
        }

        /* UI/user ids sometimes use a hyphen (emoticons/alex-simple); folders are underscore. */
        String resolved = id.replace('-', '_');
        ModelInstance loaded = this.models.get(id);

        if (loaded == null && !resolved.equals(id))
        {
            loaded = this.models.get(resolved);
        }

        if (loaded != null)
        {
            if (!resolved.equals(id))
            {
                cir.setReturnValue(loaded);
                cir.cancel();
            }

            return;
        }

        if (this.bbsvfx$failed.contains(id) || this.bbsvfx$failed.contains(resolved))
        {
            return;
        }

        try
        {
            loaded = this.loadModel(resolved);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            loaded = this.models.get(resolved);
        }

        if (loaded == null)
        {
            this.bbsvfx$failed.add(id);
            this.bbsvfx$failed.add(resolved);
        }
        else if (!resolved.equals(id))
        {
            this.models.put(id, loaded);
        }

        cir.setReturnValue(loaded);
        cir.cancel();
    }
}
