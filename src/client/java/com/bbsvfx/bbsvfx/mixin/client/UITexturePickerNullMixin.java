package com.bbsvfx.bbsvfx.mixin.client;

import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.input.UITexturePicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * BBS 2.4 regression guard: {@code UITexturePicker.updateOptions()} calls
 * {@code TextureManager.getTexture(this.current)} unconditionally, and with no saved panel state
 * {@code current} is null — the NPE kills dashboard panel registration mid-way (every panel after
 * the texture manager never registers) and the half-built dashboard crashes the world render
 * seconds later ("Pose stack not empty"). With nothing selected the picker is simply hidden.
 */
@Mixin(value = UITexturePicker.class, remap = false)
public abstract class UITexturePickerNullMixin
{
    @Shadow
    public Link current;

    @Shadow
    public UIElement options;

    @Inject(method = "updateOptions", at = @At("HEAD"), cancellable = true)
    private void bbsvfx$nullSafeUpdateOptions(CallbackInfo ci)
    {
        if (this.current == null)
        {
            this.options.setVisible(false);
            ci.cancel();
        }
    }
}
