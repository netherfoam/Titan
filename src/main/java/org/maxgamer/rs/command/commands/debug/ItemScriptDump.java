package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.item.ItemType;
import org.maxgamer.rs.repository.ItemTypeRepository;

import java.util.Map;

/**
 * @author netherfoam
 */
public class ItemScriptDump implements GenericCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        ItemType proto = Core.getServer().getDatabase().getRepository(ItemTypeRepository.class).find(Integer.parseInt(args[0]));
        Map<Integer, Object> data = proto.getScriptData();
        if (data.isEmpty()) return;
        sender.sendMessage(proto.getName() + "(" + proto.getId() + ") -> " + data.toString());
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}