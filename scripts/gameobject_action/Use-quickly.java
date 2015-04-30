public boolean run(Persona p, GameObject g){
	p.getWindow().open(new BankInterface(p));
	return true;
}