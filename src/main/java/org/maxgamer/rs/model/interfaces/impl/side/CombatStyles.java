package org.maxgamer.rs.model.interfaces.impl.side;

import java.io.File;
import java.io.IOException;

import org.maxgamer.rs.model.interfaces.SideInterface;
import org.maxgamer.rs.util.Log;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.AttackStyle;
import org.maxgamer.rs.model.entity.mob.combat.DamageType;
import org.maxgamer.rs.model.entity.mob.combat.JavaScriptAttack;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
public class CombatStyles extends SideInterface {
	public static final int SPECIAL_TOGGLE_CONFIG = 301;
	
	private JavaScriptAttack attack;
	
	public CombatStyles(Player p) {
		super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 202 : 87));
		setChildId(884);
	}
	
	public JavaScriptAttack attack(){
		if(attack != null){
			if(attack.isFinished()){
				attack = null;
			}
		}
		return attack;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (buttonId) {
			case 4:
				if(this.attack != null && !this.attack.isFinished()){
					this.attack = null;
					getPlayer().getProtocol().sendConfig(SPECIAL_TOGGLE_CONFIG, 0);
					return;
				}
				
				ItemStack w = getPlayer().getEquipment().getWeapon();
				if(w == null){
					getPlayer().getCheats().log(4, "Player attempted to use a special attack while not wielding a weapon.");
					return;
				}
				
				// Naming conventions: Remove anything inside brackets such as (p++)
				// And remove anything that's not alphanumeric, a space, underscore or dash.
				String name = w.getName();
				name = name.replaceAll("\\(.*\\)", "");
				name = name.replaceAll("[^A-Za-z0-9 _ -]", "");
				name = name.trim();
				
				DamageType type;
				if(getPlayer().getAttackStyle().isType(SkillType.RANGE)){
					type = DamageType.RANGE;
				}
				else{
					type = DamageType.MELEE;
				}
				
				File file = new File(JavaScriptFiber.SCRIPT_FOLDER, "attacks/special/" + name + ".js");
				if(file.exists() == false){
					Log.debug("Couldn't find " + name + " - Special attack failed.");
					return;
				}
				
				try {
					this.attack = new JavaScriptAttack(getPlayer(), -1, -1, file, type){
						@Override
						public boolean run(Mob target){
							getPlayer().getProtocol().sendConfig(SPECIAL_TOGGLE_CONFIG, 0);
							return super.run(target);
						}
					};
					getPlayer().getProtocol().sendConfig(SPECIAL_TOGGLE_CONFIG, 1);
				}
				catch (IOException e) {
					Log.info("Failed to read " + file + ", message: " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")");
				}
				break;
			case 11:
			case 12:
			case 13:
			case 14:
				ItemStack wep = getPlayer().getEquipment().get(WieldType.WEAPON);
				AttackStyle style;
				if (wep == null) {
					style = AttackStyle.getStyle(-1, buttonId - 10);
				}
				else {
					style = wep.getDefinition().getAttackStyle(buttonId - 10);
				}
				if (style == null) {
					getPlayer().getCheats().log(10, "Player attempted to use an attack style which is not available on their weapon. Weapon: " + wep + ", Style number " + (buttonId - 10) + " requested.");
					return;
				}
				getPlayer().setAttackStyle(style);
				if (getPlayer().getAutocast() != null) {
					getPlayer().setAutocast(null);
				}
				break;
			case 15:
				//Auto retaliate toggle
				getPlayer().setRetaliate(!getPlayer().isRetaliate());
				break;
			default:
				break;
		}
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
}