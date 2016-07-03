package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
@CmdName(names={"interfaceshow", "interface", "showinterface", "sendinterface", "interfacesend"})
public class InterfaceShow implements PlayerCommand {
	@Override
	public void execute(final Player p, String[] args) {
		if(args.length <= 0){
			//We ask each question manually.
			p.getWindow().open(new IntRequestInterface(p, "Parent ID (Eg 746 for resizable, 548 for not)") {
				@Override
				public void onInput(final long parentID) {
					p.getWindow().open(new IntRequestInterface(p, "ChildID (Eg the type ID. 300 for smithing)") {
						
						@Override
						public void onInput(final long childID) {
							
							p.getWindow().open(new IntRequestInterface(p, "ChildPos ID (Eg 9 for primary resizable, 18 for primary fixed)") {
								
								@Override
								public void onInput(long childPos) {
									p.getProtocol().sendInterface(false, (int) parentID, (int) childPos, (int) childID);
								}
							});
						}
					});
				}
			});
			
			return;
		}
		
		if (args.length < 3) {
			p.sendMessage("Arg0: ParentId");
			p.sendMessage("Arg1: ChildId");
			p.sendMessage("Arg2: ChildPos");
			return;
		}
		try {
			int parent = Integer.parseInt(args[0]);
			int childId = Integer.parseInt(args[1]);
			int childPos = Integer.parseInt(args[2]);
			
			p.sendMessage("Sending Interface... Parent: " + parent + ", ChildId: " + childId + ", ChildPos: " + childPos);
			p.getProtocol().sendInterface(true, parent, childPos, childId);
		}
		catch (NumberFormatException e) {
			p.sendMessage("All 3 args must be integers. One is not. Given " + args[0] + ", " + args[1] + ", " + args[2]);
			return;
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}