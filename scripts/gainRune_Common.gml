///gainRune_Common();
randomize();
var choise = irandom(199); //range = 0 to 199
choise += 1;               //new range = 1 to 200
randomize();
var choise2 = irandom(4);  //range = 0 to 4
choise2 += 1;              //new range = 1 to 5
var container = "";

ini_open("brawlerz.ini");

if (choise >= 1 && choise <= 140){
    //case common
    switch(choise2){
        case 1:
            //rune hp
            ini_write_string("ssh", "rh1", base64_encode("true"));
            container = "HP Rune I";
            break;
        case 2:
            //rune dmg
            ini_write_string("ssh", "rd1", base64_encode("true"));
            container = "Damage Rune I";
            break;
        case 3:
            //rune res
            ini_write_string("ssh", "rr1", base64_encode("true"));
            container = "Resistance Rune I";
            break;
        case 4:
            //rune mp
            ini_write_string("ssh", "rm1", base64_encode("true"));
            container = "MP Rune I";
            break;
        case 5:
            //rune pp
            ini_write_string("ssh", "rp1", base64_encode("true"));
            container = "PP Rune I";
            break;
    }
    
}
if (choise >= 141 && choise <= 190){
    //case rare
    switch(choise2){
        case 1:
            //rune hp
            ini_write_string("ssh", "rh2", base64_encode("true"));
            container = "HP Rune II";
            break;
        case 2:
            //rune dmg
            ini_write_string("ssh", "rd2", base64_encode("true"));
            container = "Damage Rune II";
            break;
        case 3:
            //rune res
            ini_write_string("ssh", "rr2", base64_encode("true"));
            container = "Resistance Rune II";
            break;
        case 4:
            //rune mp
            ini_write_string("ssh", "rm2", base64_encode("true"));
            container = "MP Rune II";
            break;
        case 5:
            //rune pp
            ini_write_string("ssh", "rp2", base64_encode("true"));
            container = "PP Rune II";
            break;
    }
}
if (choise >= 191 && choise <= 198){
    //case epic
    switch(choise2){
        case 1:
            //rune hp
            ini_write_string("ssh", "rh3", base64_encode("true"));
            container = "HP Rune III";
            break;
        case 2:
            //rune dmg
            ini_write_string("ssh", "rd3", base64_encode("true"));
            container = "Damage Rune III";
            break;
        case 3:
            //rune res
            ini_write_string("ssh", "rr3", base64_encode("true"));
            container = "Resistance Rune III";
            break;
        case 4:
            //rune mp
            ini_write_string("ssh", "rm3", base64_encode("true"));
            container = "MP Rune III";
            break;
        case 5:
            //rune pp
            ini_write_string("ssh", "rp3", base64_encode("true"));
            container = "PP Rune III";
            break;
    }
}
if (choise >= 199 && choise <= 200){
    //case celestial
    switch(choise2){
        case 1:
            //rune hp
            ini_write_string("ssh", "rcelc", base64_encode("true"));
            container = "Celestial Critical Rune";
            break;
        case 2:
            //rune dmg
            ini_write_string("ssh", "rcelt", base64_encode("true"));
            container = "Celestial Earth Rune";
            break;
        case 3:
            //rune res
            ini_write_string("ssh", "rcelari", base64_encode("true"));
            container = "Celestial Air Rune";
            break;
        case 4:
            //rune mp
            ini_write_string("ssh", "rcelf", base64_encode("true"));
            container = "Celestial Fire Rune";
            break;
        case 5:
            //rune pp
            ini_write_string("ssh", "rcelacq", base64_encode("true"));
            container = "Celestial Water Rune";
            break;
    }
}

ini_close();
return container; //container is a string
//container contains the name of the given rune
