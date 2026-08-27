package com.bbsvfx.bbsvfx.mixin.client;

import mchorse.bbs_mod.bobj.BOBJArmature;
import mchorse.bbs_mod.bobj.BOBJBone;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * {@code BOBJArmature.initArmature} does {@code bone.parentBone = bones.get(parent)} then immediately
 * reads {@code parentBone.boneMat}. A missing parent (or a null parent string) NPEs the whole BOBJ
 * load — {@code ModelManager} then keeps {@code models[id] = null} forever, which is exactly
 * "emoticons actor is invisible and the editor has no bones / body parts".
 *
 * <p>Re-run the init with null-safe parent wiring: null/{@code ""} parent = root, a named parent that
 * isn't in the map is treated as a root instead of crashing the load.</p>
 */
@Mixin(value = BOBJArmature.class, remap = false)
public abstract class BOBJArmatureNullParentMixin
{
    @Shadow
    public Map<String, BOBJBone> bones;

    @Shadow
    public List<BOBJBone> orderedBones;

    @Shadow
    public Matrix4f[] matrices;

    @Shadow
    private boolean initialized;

    @Inject(method = "initArmature", at = @At("HEAD"), cancellable = true)
    private void bbsvfx$safeInit(CallbackInfo ci)
    {
        if (this.initialized)
        {
            ci.cancel();
            return;
        }

        for (BOBJBone bone : this.bones.values())
        {
            if (bone.parent == null)
            {
                bone.parent = "";
            }

            if (!bone.parent.isEmpty())
            {
                bone.parentBone = this.bones.get(bone.parent);

                if (bone.parentBone != null)
                {
                    bone.relBoneMat.set(bone.parentBone.boneMat);
                    bone.relBoneMat.invert();
                    bone.relBoneMat.mul(bone.boneMat);
                }
                else
                {
                    bone.relBoneMat.set(bone.boneMat);
                }
            }
            else
            {
                bone.relBoneMat.set(bone.boneMat);
            }
        }

        this.orderedBones.sort(Comparator.comparingInt((o) -> o.index));
        this.matrices = new Matrix4f[this.orderedBones.size()];
        this.initialized = true;
        ci.cancel();
    }
}
