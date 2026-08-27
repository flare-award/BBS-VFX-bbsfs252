package com.bbsvfx.bbsvfx.mixin.client;

import mchorse.bbs_mod.bobj.BOBJBone;
import mchorse.bbs_mod.cubic.render.vao.BOBJModelSimpleVAO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code BOBJModelSimpleVAO} looks up hard-coded bone names ({@code low_left_leg}, {@code low_leg_right},
 * …). A missing bone leaves {@code Joint.top}/{@code Joint.joint} null; {@code process} then NPEs on
 * every {@code updateMesh}, so {@code emoticons/*_simple} never upload a mesh. Skip the joint instead
 * of aborting the whole skinning pass — the parent VAO already CPU-skinned the vertices.
 */
@Mixin(value = BOBJModelSimpleVAO.Joint.class, remap = false)
public abstract class BOBJSimpleJointMixin
{
    @Shadow
    public BOBJBone top;

    @Shadow
    public BOBJBone joint;

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void bbsvfx$skipNullBones(CallbackInfo ci)
    {
        if (this.top == null || this.joint == null)
        {
            ci.cancel();
        }
    }
}
