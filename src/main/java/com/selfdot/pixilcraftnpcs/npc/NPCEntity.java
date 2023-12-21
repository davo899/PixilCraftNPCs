package com.selfdot.pixilcraftnpcs.npc;

import com.selfdot.pixilcraftnpcs.util.CommandUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class NPCEntity extends MobEntity {

    private List<String> commandList;
    private String displayName;
    private boolean nameplateEnabled;

    public NPCEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        setNoGravity(true);
        setPersistent();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void pushAway(Entity entity) { }

    @Override
    public void pushAwayFrom(Entity entity) { }

    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (nameplateEnabled) setCustomName(Text.literal(displayName));
    }

    public void setNameplateEnabled(boolean nameplateEnabled) {
        this.nameplateEnabled = nameplateEnabled;
        setCustomNameVisible(nameplateEnabled);
        if (nameplateEnabled) setCustomName(Text.literal(displayName));
        else setCustomName(null);
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        if (getWorld().isClient) return ActionResult.PASS;
        if (hand == Hand.OFF_HAND) return ActionResult.PASS;
        commandList.forEach(
            command -> CommandUtils.executeCommandAsServer(command, Objects.requireNonNull(getServer()))
        );
        return super.interactAt(player, hitPos, hand);
    }

}
