package org.maxgamer.rs.model.interfaces.impl.side;

import org.maxgamer.rs.model.interfaces.SideInterface;
import org.maxgamer.rs.model.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
public class SkillsInterface extends SideInterface {
	public SkillsInterface(Player p) {
		super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 204 : 89));
		setChildId(320);
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, final int buttonId, int slotId, int itemId) {
		if (option == 1) {
			player.getWindow().open(new IntRequestInterface(player, "Enter a level target") {
				@Override
				public void onInput(long value) {
					if (buttonId == 200) player.getSkills().targetSkillLevel(SkillType.ATTACK, (int) value);
					if (buttonId == 11) player.getSkills().targetSkillLevel(SkillType.STRENGTH, (int) value);
					if (buttonId == 28) player.getSkills().targetSkillLevel(SkillType.DEFENCE, (int) value);
					if (buttonId == 52) player.getSkills().targetSkillLevel(SkillType.RANGE, (int) value);
					if (buttonId == 76) player.getSkills().targetSkillLevel(SkillType.PRAYER, (int) value);
					if (buttonId == 93) player.getSkills().targetSkillLevel(SkillType.MAGIC, (int) value);
					if (buttonId == 110) player.getSkills().targetSkillLevel(SkillType.RUNECRAFTING, (int) value);
					if (buttonId == 134) player.getSkills().targetSkillLevel(SkillType.CONSTRUCTION, (int) value);
					if (buttonId == 158) player.getSkills().targetSkillLevel(SkillType.DUNGEONEERING, (int) value);
					if (buttonId == 193) player.getSkills().targetSkillLevel(SkillType.CONSTITUTION, (int) value);
					if (buttonId == 19) player.getSkills().targetSkillLevel(SkillType.AGILITY, (int) value);
					if (buttonId == 36) player.getSkills().targetSkillLevel(SkillType.HERBLORE, (int) value);
					if (buttonId == 60) player.getSkills().targetSkillLevel(SkillType.THIEVING, (int) value);
					if (buttonId == 84) player.getSkills().targetSkillLevel(SkillType.CRAFTING, (int) value);
					if (buttonId == 101) player.getSkills().targetSkillLevel(SkillType.FLETCHING, (int) value);
					if (buttonId == 118) player.getSkills().targetSkillLevel(SkillType.SLAYER, (int) value);
					if (buttonId == 142) player.getSkills().targetSkillLevel(SkillType.HUNTER, (int) value);
					if (buttonId == 186) player.getSkills().targetSkillLevel(SkillType.MINING, (int) value);
					if (buttonId == 179) player.getSkills().targetSkillLevel(SkillType.SMITHING, (int) value);
					if (buttonId == 44) player.getSkills().targetSkillLevel(SkillType.FISHING, (int) value);
					if (buttonId == 68) player.getSkills().targetSkillLevel(SkillType.COOKING, (int) value);
					if (buttonId == 172) player.getSkills().targetSkillLevel(SkillType.FIREMAKING, (int) value);
					if (buttonId == 165) player.getSkills().targetSkillLevel(SkillType.WOODCUTTING, (int) value);
					if (buttonId == 126) player.getSkills().targetSkillLevel(SkillType.FARMING, (int) value);
					if (buttonId == 150) player.getSkills().targetSkillLevel(SkillType.SUMMONING, (int) value);
				}
			});
		}
		else if (option == 2) {
			player.getWindow().open(new IntRequestInterface(player, "Enter a experience target") {
				@Override
				public void onInput(long value) {
					if (buttonId == 200) player.getSkills().targetSkillExp(SkillType.ATTACK, (int) value);
					if (buttonId == 11) player.getSkills().targetSkillExp(SkillType.STRENGTH, (int) value);
					if (buttonId == 28) player.getSkills().targetSkillExp(SkillType.DEFENCE, (int) value);
					if (buttonId == 52) player.getSkills().targetSkillExp(SkillType.RANGE, (int) value);
					if (buttonId == 76) player.getSkills().targetSkillExp(SkillType.PRAYER, (int) value);
					if (buttonId == 93) player.getSkills().targetSkillExp(SkillType.MAGIC, (int) value);
					if (buttonId == 110) player.getSkills().targetSkillExp(SkillType.RUNECRAFTING, (int) value);
					if (buttonId == 134) player.getSkills().targetSkillExp(SkillType.CONSTRUCTION, (int) value);
					if (buttonId == 158) player.getSkills().targetSkillExp(SkillType.DUNGEONEERING, (int) value);
					if (buttonId == 193) player.getSkills().targetSkillExp(SkillType.CONSTITUTION, (int) value);
					if (buttonId == 19) player.getSkills().targetSkillExp(SkillType.AGILITY, (int) value);
					if (buttonId == 36) player.getSkills().targetSkillExp(SkillType.HERBLORE, (int) value);
					if (buttonId == 60) player.getSkills().targetSkillExp(SkillType.THIEVING, (int) value);
					if (buttonId == 84) player.getSkills().targetSkillExp(SkillType.CRAFTING, (int) value);
					if (buttonId == 101) player.getSkills().targetSkillExp(SkillType.FLETCHING, (int) value);
					if (buttonId == 118) player.getSkills().targetSkillExp(SkillType.SLAYER, (int) value);
					if (buttonId == 142) player.getSkills().targetSkillExp(SkillType.HUNTER, (int) value);
					if (buttonId == 186) player.getSkills().targetSkillExp(SkillType.MINING, (int) value);
					if (buttonId == 179) player.getSkills().targetSkillExp(SkillType.SMITHING, (int) value);
					if (buttonId == 44) player.getSkills().targetSkillExp(SkillType.FISHING, (int) value);
					if (buttonId == 68) player.getSkills().targetSkillExp(SkillType.COOKING, (int) value);
					if (buttonId == 172) player.getSkills().targetSkillExp(SkillType.FIREMAKING, (int) value);
					if (buttonId == 165) player.getSkills().targetSkillExp(SkillType.WOODCUTTING, (int) value);
					if (buttonId == 126) player.getSkills().targetSkillExp(SkillType.FARMING, (int) value);
					if (buttonId == 150) player.getSkills().targetSkillExp(SkillType.SUMMONING, (int) value);
				}
			});
		} else if (option == 3) {
			if (buttonId == 200) player.getSkills().removeTarget(SkillType.ATTACK);
			if (buttonId == 11) player.getSkills().removeTarget(SkillType.STRENGTH);
			if (buttonId == 28) player.getSkills().removeTarget(SkillType.DEFENCE);
			if (buttonId == 52) player.getSkills().removeTarget(SkillType.RANGE);
			if (buttonId == 76) player.getSkills().removeTarget(SkillType.PRAYER);
			if (buttonId == 93) player.getSkills().removeTarget(SkillType.MAGIC);
			if (buttonId == 110) player.getSkills().removeTarget(SkillType.RUNECRAFTING);
			if (buttonId == 134) player.getSkills().removeTarget(SkillType.CONSTRUCTION);
			if (buttonId == 158) player.getSkills().removeTarget(SkillType.DUNGEONEERING);
			if (buttonId == 193) player.getSkills().removeTarget(SkillType.CONSTITUTION);
			if (buttonId == 19) player.getSkills().removeTarget(SkillType.AGILITY);
			if (buttonId == 36) player.getSkills().removeTarget(SkillType.HERBLORE);
			if (buttonId == 60) player.getSkills().removeTarget(SkillType.THIEVING);
			if (buttonId == 84) player.getSkills().removeTarget(SkillType.CRAFTING);
			if (buttonId == 101) player.getSkills().removeTarget(SkillType.FLETCHING);
			if (buttonId == 118) player.getSkills().removeTarget(SkillType.SLAYER);
			if (buttonId == 142) player.getSkills().removeTarget(SkillType.HUNTER);
			if (buttonId == 186) player.getSkills().removeTarget(SkillType.MINING);
			if (buttonId == 179) player.getSkills().removeTarget(SkillType.SMITHING);
			if (buttonId == 44) player.getSkills().removeTarget(SkillType.FISHING);
			if (buttonId == 68) player.getSkills().removeTarget(SkillType.COOKING);
			if (buttonId == 172) player.getSkills().removeTarget(SkillType.FIREMAKING);
			if (buttonId == 165) player.getSkills().removeTarget(SkillType.WOODCUTTING);
			if (buttonId == 126) player.getSkills().removeTarget(SkillType.FARMING);
			if (buttonId == 150) player.getSkills().removeTarget(SkillType.SUMMONING);
		}
	}
}
