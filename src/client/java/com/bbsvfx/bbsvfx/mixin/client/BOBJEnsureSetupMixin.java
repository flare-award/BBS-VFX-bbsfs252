package com.bbsvfx.bbsvfx.mixin.client;

import mchorse.bbs_mod.cubic.IModel;
import mchorse.bbs_mod.cubic.ModelInstance;
import mchorse.bbs_mod.cubic.model.bobj.BOBJModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * BOBJ {@code setup()} (VAO/VBO alloc) is deferred with {@code MinecraftClient.execute} from the loader
 * thread. {@code ModelInstance.render} bails out when {@code getVaos()} is empty, so a frame that races
 * the deferred setup draws nothing — emoticons look fully invisible. If we are already on the render
 * thread with empty VAOs, build them now.
 */
@Mixin(value = ModelInstance.class, remap = false)
public abstract class BOBJEnsureSetupMixin
{
    @Shadow
    public IModel model;

    @Inject(method = "render", at = @At("HEAD"))
    private void bbsvfx$setupEmptyBobj(CallbackInfo ci)
    {
        if (this.model instanceof BOBJModel bobj && bobj.getVaos().isEmpty())
        {
            try
            {
                bobj.setup();
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
    }
}
