package com.tm.calemicrime.flashnpc;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.*;
import flash.npcmod.core.functions.AbstractFunction;
import flash.npcmod.entity.NpcEntity;
import flash.npcmod.network.PacketDispatcher;
import flash.npcmod.network.packets.server.SMoveToDialogue;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class MoveOnFTBQuestFunction extends AbstractFunction {

    public MoveOnFTBQuestFunction() {
        super("moveOnFTBQuest", new String[]{"questID", "trueDialogue", "falseDialogue"}, empty);
    }

    @Override
    public void call(String[] params, ServerPlayer sender, NpcEntity npcEntity) {

        if (params.length == 3) {

            String questID = params[0];

            QuestFile file = findQuestFile();
            QuestObject questObject = null;

            if (file == null) {
                return;
            }

            if (questID.startsWith("#")) {

                for (QuestObject object : file.getChildren()) {

                    if (object.hasTag(questID.substring(1))) {
                        questObject = object;
                        break;
                    }
                }
            }

            else {
                long num = file.getID(questID);

                QuestObject object = file.get(num);

                if (object == null) {
                    return;
                }

                questObject = object;
            }

            if (questObject == null) {
                return;
            }

            if (TeamData.get(sender).isCompleted(questObject)) {
                PacketDispatcher.sendTo(new SMoveToDialogue(params[1], npcEntity.getId()), sender);
            }

            else PacketDispatcher.sendTo(new SMoveToDialogue(params[2], npcEntity.getId()), sender);
        }
    }

    @Nullable
    private static QuestFile findQuestFile() {
        if (!QuestObjectBase.isNull(ServerQuestFile.INSTANCE)) {
            return ServerQuestFile.INSTANCE;
        }
        else if (!QuestObjectBase.isNull(ClientQuestFile.INSTANCE)) {
            return ClientQuestFile.INSTANCE;
        }

        return null;
    }
}