package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.interfaces.Window;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.mage.CombatSpell;
import org.maxgamer.rs.model.entity.mob.combat.mage.ItemSpell;
import org.maxgamer.rs.model.entity.mob.combat.mage.MagicAttack;
import org.maxgamer.rs.model.entity.mob.combat.mage.Spell;
import org.maxgamer.rs.model.entity.mob.combat.mage.Spellbook;
import org.maxgamer.rs.model.entity.mob.combat.mage.TargetSpell;
import org.maxgamer.rs.model.entity.mob.combat.mage.TeleportSpell;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Inventory;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
public class MagicInterface extends SideInterface {
	private MagicAttack nextAttack;
	
	public MagicInterface(Player p, short childId) {
		super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 209 : 94));
		setChildId(childId);
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		Spellbook book = getPlayer().getSpellbook();
		
		if ((option == 5 && book == Spellbook.ANCIENT) || (option == 0 && book == Spellbook.MODERN)) { //Autocast swaps location depending on book
			Spell s = book.getSpell(buttonId);
			if (s == null) {
				getPlayer().getCheats().log(5, "Player attempted to cast a spell which is not listed in their spellbook");
				return;
			}
			
			if (getPlayer().getSkills().getLevel(SkillType.MAGIC, true) < s.getLevel()) {
				getPlayer().sendMessage("You do not have the required magic level to cast that spell.");
				return;
			}
			
			if (s instanceof CombatSpell) {
				CombatSpell t = (CombatSpell) s;
				if (getPlayer().getAutocast() == t) {
					//Player is toggling autocast off
					getPlayer().setAutocast(null);
				}
				else {
					//Player is toggling autocast on
					getPlayer().setAutocast(t);
				}
			}
			
			if (s instanceof TeleportSpell) {
				final TeleportSpell t = (TeleportSpell) s;
				t.cast(getPlayer());
			}
		}
	}
	
	@Override
	public void onClick(Mob target, int buttonId, int slotId, int itemId, boolean run) {
		super.onClick(target, buttonId, slotId, itemId, run);
		if (target.isDead()) {
			//Players may still target mobs which are performing the death animation
			return;
		}
		
		if (target.isHidden()) {
			getPlayer().getCheats().log(5, "Player attempted to cast a spell on a target which is hidden");
			return;
		}
		
		if (target.isAttackable(getPlayer()) == false) {
			//This can legitimately occur when a player tries to cast a spell on an NPC
			getPlayer().sendMessage("You can't attack that.");
			return;
		}
		
		Spellbook book = getPlayer().getSpellbook();
		Spell s = book.getSpell(buttonId);
		
		if (s == null) {
			getPlayer().getCheats().log(5, "Player attempted to cast a spell on " + target + ", but the player doesn't have that spell or it is not implemented");
			return;
		}
		
		if (s instanceof TargetSpell == false) {
			getPlayer().getCheats().log(5, "Player attempted to cast a spell " + s + " on a target, but the spell is a " + s.getClass().getSimpleName() + " which does not implement TargetSpell");
			return;
		}
		
		TargetSpell t = (TargetSpell) s;
		this.nextAttack = new MagicAttack(getPlayer(), t);
		getPlayer().setTarget(target);
	}
	
	@Override
	public void onUse(Window to, int fromButtonId, int fromItemId, int fromSlot, int toButtonId, int toItemId, int toSlot) {
		super.onUse(to, fromButtonId, fromItemId, fromSlot, toButtonId, toItemId, toSlot);
		
		Spell s = getPlayer().getSpellbook().getSpell(fromButtonId);
		if (s == null) {
			getPlayer().getCheats().log(5, "Player attempted to cast spell from buttonId " + fromButtonId + ", but spell not found.");
			return;
		}
		
		if (to instanceof InventoryInterface) {
			Inventory inv = getPlayer().getInventory();
			ItemStack item = inv.get(toSlot);
			if (item == null) {
				getPlayer().getCheats().log(5, "Player attempted to cast spell " + s + " on inventory slot " + toSlot + ", but slot is empty. Client says it has an item of ID " + toItemId + " in it.");
				return;
			}
			
			if (s instanceof ItemSpell) {
				ItemSpell t = (ItemSpell) s;
				t.cast(getPlayer(), inv, toSlot);
			}
			else {
				getPlayer().getCheats().log(5, "Player attempted to cast spell " + s + " on inventory slot, but that isn't an ItemSpell!");
				return;
			}
		}
	}
	
	public MagicAttack attack(){
		if(this.nextAttack != null){
			if(this.nextAttack.isFinished()){
				this.nextAttack = null;
			}
		}
		return this.nextAttack;
	}
}