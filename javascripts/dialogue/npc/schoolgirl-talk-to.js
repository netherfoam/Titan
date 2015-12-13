var messages = ["Maz...Zar...Za-mor-ak is the bestest!", 
            "Can you find my teacher? I need the toilet!",
            "I wanna be an archaeologist when I grow up!",
            "*sniff* They won't let me take an arrowhead as a souvenir.",
            "Yaaay! A day off school.",
            "I wonder what they are doing behind that rope.",
            "*cough* It's so dusty in here.",
            "Sada...Sram...Sa-ra-do-min is the bestest!",
            "Teacher! Can we go to the Natural History exhibit now?"];
var random = messages[Math.floor(Math.random() * messages.length)];

chat(npc, random);