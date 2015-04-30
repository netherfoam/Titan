package org.maxgamer.rs.command.commands;

import java.util.Map;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.item.ItemProto;

/**
 * @author netherfoam
 */
public class ItemScriptDump implements GenericCommand {
	@Override
	public void execute(CommandSender sender, String[] args) {
		ItemProto proto = ItemProto.getDefinition(Integer.parseInt(args[0]));
		Map<Integer, Object> data = proto.getScriptData();
		if (data.isEmpty()) return;
		sender.sendMessage(proto.getName() + "(" + proto.getId() + ") -> " + data.toString());
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}