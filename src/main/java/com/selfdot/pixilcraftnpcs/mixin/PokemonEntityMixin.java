package com.selfdot.pixilcraftnpcs.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.selfdot.pixilcraftnpcs.imixin.IPokemonEntityMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PokemonEntity.class)
public class PokemonEntityMixin implements IPokemonEntityMixin {

    @Unique
    private boolean isNPC = false;
    
    @Unique
    private boolean isDiscardable = false;

    @Unique
    public void pixilCraftNPCs$setNPC(boolean NPC) {
        isNPC = NPC;
    }

    @Unique
    public void pixilCraftNPCs$setDiscardable(boolean discardable) {
        isDiscardable = discardable;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void injectDamage(CallbackInfoReturnable<Boolean> cir) {
        if (isNPC) cir.setReturnValue(false);
    }
    
    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void injectRemove(CallbackInfo cir) {
        if (isNPC && !isDiscardable) cir.cancel();
    }

}
