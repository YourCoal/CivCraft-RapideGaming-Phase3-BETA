/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
package com.civcraft.threading.tasks;

import gpl.AttributeUtil;
import net.minecraft.server.v1_8_R3.Material;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.civcraft.exception.CivException;
import com.civcraft.loreenhancements.LoreEnhancement;
import com.civcraft.lorestorage.LoreMaterial;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.BuildableDamageBlock;
import com.civcraft.util.BlockCoord;
import com.civcraft.util.CivColor;

public class StructureBlockHitEvent implements Runnable {

	/*
	 * Called when a structure block is hit, this async task quickly determines
	 * if the block hit should take damage during war.
	 * 
	 */
	String playerName;
	BlockCoord coord;
	BuildableDamageBlock dmgBlock;
	World world;
	
	public StructureBlockHitEvent(String player, BlockCoord coord, BuildableDamageBlock dmgBlock, World world) {
		this.playerName = player;
		this.coord = coord;
		this.dmgBlock = dmgBlock;
		this.world = world;
	}
	
	@Override
	public void run() {
		
		if (playerName == null) {
			return;
		}
		Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e) {
			//Player offline now?
			return;
		}
		if (dmgBlock.allowDamageNow(player)) {
			/* Do our damage. */
			int damage = 1;
			LoreMaterial material = LoreMaterial.getMaterial(player.getItemInHand());
			if (material != null) {
				damage = material.onStructureBlockBreak(dmgBlock, damage);
			}
			
			if (player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
				AttributeUtil attrs = new AttributeUtil(player.getItemInHand());
				for (LoreEnhancement enhance : attrs.getEnhancements()) {
					damage = enhance.onStructureBlockBreak(dmgBlock, damage);
				}
			}
			
			if (damage > 1) {
				CivMessage.send(player, CivColor.LightGray+"Punchout Lore: "+(damage-1)+" extra damage!");
			}
				
			dmgBlock.getOwner().onDamage(damage, world, player, dmgBlock.getCoord(), dmgBlock);
		} else {
			CivMessage.sendErrorNoRepeat(player, 
					"This block belongs to a "+dmgBlock.getOwner().getDisplayName()+" and cannot be destroyed right now.");
		}
	}
}
