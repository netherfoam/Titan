
//IDs of bones -              normal - burnt - wolf - monkey - bat - big - jogre - zogre - shaikahan - baby - wyvern - dragon - fayrg - raurg - dagannoth - ourg - frost-dragon
static int[] bones = new int[]{526,    528,    2859,  3183,    530,  532,  3125,   4812,   3123,       534,   6812,    536,     4830,   4832,   6729,       4834,  18830};
//Experience gained from the bones
static double[] xp = new double[]{10,  10,     10,    12.5,    12.5, 20,   20,     25,     30,         35,    40,      50,      52.5,   55,     65,         75,    85};

//The bones which are buried by the player
int bone = -1;
//True if we're running this for the first time
boolean first = true;
boolean run(Persona p, ItemStack item, int slot){
	if(first){
		first = false;
		
		//Find the bone
		for(int i = 0; i < bones.length; i++){
			if(bones[i] == item.getId()){
				bone = i;
				//Remove the bone
				p.getInventory().remove(slot, item);
				//Do an animation action, we continue when it's finished
				p.getActions().insertBefore(self, new AnimateAction(p, 827, false));
				if(p instanceof Client){
					p.getProtocol().sendSound(2738, 0, 1);
				}
				//We've done nothing important here, so yield and let the animation run
				self.yield();
				//We are guaranteed to have a valid bone here. Now awarding experience for burying the bone.
				p.getSkills().addExp(SkillType.PRAYER, xp[bone]);
				return false;
			}
		}
		//Not implemented
		if(p instanceof Client){
			p.sendMessage("That bone has not been implemented yet, sorry!");
		}
		return true;
	}
	return true;
}