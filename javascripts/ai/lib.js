var fiber;
var body;
var included_files = [];

function require(path){
	for(var v in included_files){
		if(v == path) return;
	}

	fiber.include(path);
	included_files.push(path);
}

function wait(){
	return fiber.pause();
}
