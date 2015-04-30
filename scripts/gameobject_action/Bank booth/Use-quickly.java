boolean run(Persona p, GameObject obj){
	if(p instanceof Player){
		p.getWindow().open(new BankInterface(p));
	}
	return true;
}