package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.SideInterface;
import org.maxgamer.rs.model.entity.mob.combat.AttackStyle;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;

/**
 * @author netherfoam
 */
public class CombatStyles extends SideInterface {
	public static final int BUTTON_LOBBY = 5;
	public static final int BUTTON_LOGIN = 10;
	
	public CombatStyles(Player p) {
		super(p, (short) 884, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 202 : 87));
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (buttonId) {
			case 4:
				getPlayer().sendMessage("Special attacks not implemented yet, sorry!");
				break;
			case 11:
			case 12:
			case 13:
			case 14:
				//getPlayer().sendMessage("Combat Styles not implemented yet, sorry!");
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