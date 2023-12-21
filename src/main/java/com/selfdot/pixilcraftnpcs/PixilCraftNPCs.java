package com.selfdot.pixilcraftnpcs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import com.selfdot.pixilcraftnpcs.command.CommandListArgumentType;
import com.selfdot.pixilcraftnpcs.command.NPCCommand;
import com.selfdot.pixilcraftnpcs.npc.InteractCooldownTracker;
import com.selfdot.pixilcraftnpcs.npc.NPCEntity;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import net.fabricmc.api.ModInitializer;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class PixilCraftNPCs implements ModInitializer {

    public static boolean DISABLED = false;
    public static Logger LOGGER = LogUtils.getLogger();
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final EntityType<NPCEntity> NPC = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(DataKeys.PIXILCRAFT_NAMESPACE, "npc"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, NPCEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
            .disableSummon()
            .build()
    );

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(NPC, NPCEntity.createMobAttributes());
        ArgumentTypeRegistry.registerArgumentType(
            new Identifier(DataKeys.PIXILCRAFT_NAMESPACE, "command_list"),
            CommandListArgumentType.class,
            ConstantArgumentSerializer.of(CommandListArgumentType::new)
        );

        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);

        NPCTracker.getInstance().load();
        InteractCooldownTracker.getInstance().load();
    }

    private void registerCommands(
        CommandDispatcher<ServerCommandSource> dispatcher,
        CommandRegistryAccess commandRegistryAccess,
        CommandManager.RegistrationEnvironment registrationEnvironment
    ) {
        new NPCCommand().register(dispatcher);
    }

    private void onServerStarted(MinecraftServer server) {
        NPCTracker.getInstance().setServer(server);
        NPCTracker.getInstance().summonAllNPCEntities();
    }

    private void onServerStopping(MinecraftServer server) {
        server.getWorlds()
            .forEach(serverWorld -> serverWorld.getEntitiesByType(NPC, e -> true)
                .forEach(e -> e.remove(Entity.RemovalReason.KILLED))
            );

        if (!DISABLED) {
            NPCTracker.getInstance().save();
            InteractCooldownTracker.getInstance().save();
        }
    }

}
