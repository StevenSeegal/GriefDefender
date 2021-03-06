/*
 * This file is part of GriefDefender, licensed under the MIT License (MIT).
 *
 * Copyright (c) bloodmc
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.griefdefender.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.text.Component;
import net.kyori.text.adapter.spongeapi.TextAdapter;

import org.spongepowered.api.entity.living.player.Player;
import com.google.common.collect.ImmutableMap;
import com.griefdefender.GDPlayerData;
import com.griefdefender.GriefDefenderPlugin;
import com.griefdefender.api.claim.ClaimBlockSystem;
import com.griefdefender.cache.MessageCache;
import com.griefdefender.configuration.MessageStorage;
import com.griefdefender.permission.GDPermissions;

@CommandAlias("%griefdefender")
@CommandPermission(GDPermissions.COMMAND_CLAIM_MODE)
public class CommandClaimMode extends BaseCommand {

    @CommandAlias("claim|claimmode")
    @Description("Toggles claim mode creation. Note: This will default to basic claim mode.")
    @Subcommand("mode claim")
    public void execute(Player player) {
        final GDPlayerData playerData = GriefDefenderPlugin.getInstance().dataStore.getOrCreatePlayerData(player.getWorld(), player.getUniqueId());
        playerData.claimMode = !playerData.claimMode;
        playerData.claimSubdividing = null;
        if (!playerData.claimMode) {
            playerData.revertActiveVisual(player);
            // check for any active WECUI visuals
            if (GriefDefenderPlugin.getInstance().getWorldEditProvider() != null) {
                GriefDefenderPlugin.getInstance().getWorldEditProvider().revertVisuals(player, playerData, null);
            }
            TextAdapter.sendComponent(player, MessageCache.getInstance().COMMAND_CLAIMMODE_DISABLED);
        } else {
            TextAdapter.sendComponent(player, MessageCache.getInstance().COMMAND_CLAIMMODE_ENABLED);
            if (GriefDefenderPlugin.CLAIM_BLOCK_SYSTEM == ClaimBlockSystem.VOLUME) {
                final Component message = GriefDefenderPlugin.getInstance().messageData.getMessage(MessageStorage.PLAYER_REMAINING_BLOCKS_3D,
                        ImmutableMap.of(
                        "block-amount", playerData.getRemainingClaimBlocks(),
                        "chunk-amount", playerData.getRemainingChunks()));
                GriefDefenderPlugin.sendMessage(player, message);
            } else {
                final Component message = GriefDefenderPlugin.getInstance().messageData.getMessage(MessageStorage.PLAYER_REMAINING_BLOCKS_2D,
                       ImmutableMap.of(
                        "block-amount", playerData.getRemainingClaimBlocks()));
                GriefDefenderPlugin.sendMessage(player, message);
            }
        }
    }
}
