package com.selfdot.pixilcraftnpcs;

import com.mojang.brigadier.CommandDispatcher;
import com.selfdot.pixilcraftnpcs.command.CommandListArgumentType;
import com.selfdot.pixilcraftnpcs.command.NPCCommandTree;
import com.selfdot.pixilcraftnpcs.npc.InteractCooldownTracker;
import com.selfdot.pixilcraftnpcs.npc.HumanNPCEntity;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.DisableableMod;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.ftb.mods.ftbquests.events.ObjectCompletedEvent;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class PixilCraftNPCs extends DisableableMod {

    private static PixilCraftNPCs INSTANCE;
    public static final EntityType<HumanNPCEntity> NPC_HUMAN_SLIM = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(DataKeys.MOD_NAMESPACE, "npc_human_slim"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HumanNPCEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
            .disableSummon()
            .build()
    );
    public static final EntityType<HumanNPCEntity> NPC_HUMAN_CLASSIC = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(DataKeys.MOD_NAMESPACE, "npc_human_classic"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HumanNPCEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
            .disableSummon()
            .build()
    );
    private final PixilCraftNPCsConfig config = new PixilCraftNPCsConfig(this);
    private final NPCTracker npcTracker = new NPCTracker(this);
    private final InteractCooldownTracker interactCooldownTracker = new InteractCooldownTracker(this);

    public static PixilCraftNPCs getInstance() {
        return INSTANCE;
    }

    public PixilCraftNPCsConfig getConfig() {
        return config;
    }

    public NPCTracker getNPCTracker() {
        return npcTracker;
    }

    public InteractCooldownTracker getInteractCooldownTracker() {
        return interactCooldownTracker;
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        
        FabricDefaultAttributeRegistry.register(NPC_HUMAN_SLIM, HumanNPCEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(NPC_HUMAN_CLASSIC, HumanNPCEntity.createMobAttributes());

        ArgumentTypeRegistry.registerArgumentType(
            new Identifier(DataKeys.MOD_NAMESPACE, "command_list"),
            CommandListArgumentType.class,
            ConstantArgumentSerializer.of(CommandListArgumentType::new)
        );

        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        PlayerEvent.PLAYER_JOIN.register(this::onPlayerJoin);
        InteractionEvent.INTERACT_ENTITY.register(this::onInteractEntity);
        ObjectCompletedEvent.QUEST.register(this::onQuestCompleted);
        TickEvent.SERVER_LEVEL_PRE.register(this::onLevelTick);

        interactCooldownTracker.load();
    }

    private void registerCommands(
        CommandDispatcher<ServerCommandSource> dispatcher,
        CommandRegistryAccess commandRegistryAccess,
        CommandManager.RegistrationEnvironment registrationEnvironment
    ) {
        new NPCCommandTree().register(dispatcher);
    }

    private void onServerStarted(MinecraftServer server) {
        config.load();
        config.save();
        npcTracker.setServer(server);
        npcTracker.load();
        npcTracker.summonAllNPCEntities();
    }

    private void onServerStopping(MinecraftServer server) {
        if (!isDisabled()) {
            npcTracker.save();
            interactCooldownTracker.save();
        }
    }

    private void onPlayerJoin(ServerPlayerEntity player) {
        npcTracker.sendClientUpdates(player);
    }

    private EventResult onInteractEntity(PlayerEntity player, Entity entity, Hand hand) {
        if (player.getWorld().isClient) return EventResult.pass();
        if (hand == Hand.OFF_HAND) return EventResult.pass();
        npcTracker.checkInteract(player, entity);
        return EventResult.pass();
    }

    private EventResult onQuestCompleted(ObjectCompletedEvent.QuestEvent questEvent) {
        questEvent.getOnlineMembers().forEach(
            player -> npcTracker.onQuestCompleted(player, questEvent.getQuest().id)
        );
        return EventResult.pass();
    }

    private void onLevelTick(ServerWorld world) {
        npcTracker.onTick(world);
    }

}
