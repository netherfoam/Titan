chat(npc, "Greetings " + player.getName() + "! How may I be of service?");
var opt = option(["What do you do here?", "Can you heal my wounds?"]);
if(opt == 0){
	chat(npc, "We keep in touch with our diety, Saradomin");
	opt = option(["Peace be with you", "Hail Zamorak!"]);
	if(opt == 1){
		thought("The " + npc.getName() + " gives you a look of disdain and wanders off");
	}
	die();
}

chat(npc, "Very well, hold still...");
player.graphics(84); //Healing graphics
npc.animate(438); //Sort of healing looking. Or 210
player.heal(50); //Probably an incorrect amount
npc.face(player);
player.getProtocol().sendSound(98, 255, 255);