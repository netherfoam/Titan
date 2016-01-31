function talkTo(player, npc){
	chat(player, "Good day.");
	chat(npc, "Hello, I'm Gillie. What can I do for you?");
	var opt = option(["Who are you?", "Can you tell me how to milk a cow?", "Can I buy milk off you?", "I'm fine, thanks."]);
	if (opt == 0) {
		chat(player, "Who are you?");
		chat(npc, "My name's Gillie Groats. My father is a farmer and I milk the cows for him.");
		chat(player, "Do you have any buckets of milk spare?");
		chat(npc, "I'm afraid not. We need all of our milk to sell to market, but you can milk the cow yourself if you need milk.");
		chat(player, "Thanks.");
	} else if (opt == 1) {
		chat(player, "Can you tell me how to milk a cow?");
		chat(npc, "It's very easy. First, you need an empty bucket to hold the milk.");
		chat(npc, "You can buy empty buckets from the general store in Lumbridge, south-west of here, or from most general stores in Titan. You can also buy them from the Grand Exchange in Varrock.");
		chat(npc, "Then find a dairy cow to milk - you can't milk just any cow.");
		chat(player, "How do I find a dairy cow?");
		chat(npc, "They are easy to spot - they have a cowbell around their neck and are tethered to a post to stop them wandering around all over the place.");
		chat(npc, "There are a couple in this field. Then you just need to use your bucket on the cow and you'll get some tasty, nutritious milk.");
	} else if (opt == 2) {
		chat(player, "I'm afraid not. My husmand has already taken all of our stock to the market.");
		chat(npc, "You could get some by milking the dairy cows yourself. If you would still rather buy it, you can probably get some at the Grand Exchange in Varrock,");
		chat(npc, "just north of here. A lot of adventurers sell their goods there.");
	} else if (opt == 3) {
		chat(player, "I'm fine, thanks.");
	}
}