package org.maxgamer.rs.command.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.interfaces.impl.primary.BookInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.object.GameObject;

public class DespawnObject implements PlayerCommand{
	@Override
	public void execute(final Player player, String[] args) throws Exception {
		final ArrayList<GameObject> objects = new ArrayList<GameObject>(50);
		for(GameObject g : player.getLocation().getNearby(GameObject.class, 3)){
			if(g.isVisible(player) == false) continue;
			objects.add(g);
		}
		
		Collections.sort(objects, new Comparator<GameObject>(){
			@Override
			public int compare(GameObject a, GameObject b) {
				int da = player.getLocation().distanceSq(a.getLocation());
				int db = player.getLocation().distanceSq(b.getLocation());
				
				return da - db;
			}
		});
		
		final BookInterface book = new BookInterface(player){
			@Override
			public void onClose(){
				super.onClose();
				player.getWindow().close(IntRequestInterface.class);
			}
		};
		player.getWindow().open(book);
		
		for(int i = 0; i < 15 && i < objects.size(); i++){
			GameObject g = objects.get(i);
			
			String left = i + ":" + g.getName() + "id=" + g.getId() + ",typ=" + g.getType() + ",hid=" + g.isHidden();
			String right = "fac=" + g.getFacing() + ",solid=" + g.isSolid() + "low=" + g.hasRangeBlockClipFlag();
			
			book.setLine(i, left);
			book.setLine(i + 15, right);
		}
		
		IntRequestInterface input = new IntRequestInterface(player, "Which object should be deleted?") {
			@Override
			public void onInput(long value) {
				objects.get((int) value).hide();
				player.getWindow().close(book);
			}
		};
		player.getWindow().open(input);
	}

	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}