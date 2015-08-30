package org.maxgamer.rs.model.item;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.structure.dbmodel.Serializer;

public class RequirementSerializer implements Serializer{

	@Override
	public void serialize(Map<String, Object> map, Field field, Object o) {
		if(o == null){
			//No requirements.
			return;
		}
		
		@SuppressWarnings("unchecked")
		Map<SkillType, Integer> m = (Map<SkillType, Integer>) o;
		StringBuilder sb = new StringBuilder();
		
		Iterator<Entry<SkillType, Integer>> it = m.entrySet().iterator();
		while(it.hasNext()){
			Entry<SkillType, Integer> e = it.next();
			sb.append(e.getKey().getId() + ":" + e.getValue());
			if(it.hasNext()){
				sb.append(";");
			}
		}
		
		map.put(field.getName(), sb.toString());
	}

	@Override
	public Object deserialize(Field field, ResultSet rs) throws SQLException {
		String serial = rs.getString("requirements");
		if(serial == null) return null;
		HashMap<SkillType, Integer> map = new HashMap<>(1);
		for(String piece : serial.split(";")){
			if(piece.isEmpty()) continue;
			
			String[] ids = piece.split(":");
			SkillType skill = SkillType.forId(Integer.parseInt(ids[0]));
			int level = Integer.parseInt(ids[1]);
			map.put(skill, level);
		}
		if(map.isEmpty()) return null;
		return map;
	}
	
}