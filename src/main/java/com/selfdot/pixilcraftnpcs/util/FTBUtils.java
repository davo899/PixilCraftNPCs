package com.selfdot.pixilcraftnpcs.util;

import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.entity.player.PlayerEntity;

public class FTBUtils {

    public static boolean completedQuest(PlayerEntity player, long questID) {
        TeamData teamData = ServerQuestFile.INSTANCE.getOrCreateTeamData(player);
        if (teamData == null) return false;
        Quest quest = ServerQuestFile.INSTANCE.getQuest(questID);
        if (quest == null) return false;
        return teamData.isCompleted(quest);
    }

}
