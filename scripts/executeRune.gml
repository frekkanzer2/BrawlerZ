///executeRune(char, rune);

char = argument0;
rune = argument1;

switch(rune){
    case "HP Rune I":
        if (char.rune1hp_state == false){
            perc = round(char.acthp * 15 / 100);
            char.maxhp += perc;
            char.acthp += perc;
            char.rune1hp_state = true;
        }
        break;
    case "HP Rune II":
        if (char.rune2hp_state == false){
            perc = round(char.acthp * 30 / 100);
            char.maxhp += perc;
            char.acthp += perc;
            char.rune2hp_state = true;
        }
        break;
    case "HP Rune III":
        if (char.rune3hp_state == false){
            perc = round(char.acthp * 60 / 100);
            char.maxhp += perc;
            char.acthp += perc;
            char.rune3hp_state = true;
        }
        break;
    case "DMG Rune I":
        if (char.rune1dmg_state == false){
            char.dmg_earth += 15;
            char.dmg_air += 15;
            char.dmg_fire += 15;
            char.dmg_water += 15;
            char.rune1dmg_state = true;
        }
        break;
    case "DMG Rune II":
        if (char.rune2dmg_state == false){
            char.dmg_earth += 30;
            char.dmg_air += 30;
            char.dmg_fire += 30;
            char.dmg_water += 30;
            char.rune2dmg_state = true;
        }
        break;
    case "DMG Rune III":
        if (char.rune3dmg_state == false){
            char.dmg_earth += 60;
            char.dmg_air += 60;
            char.dmg_fire += 60;
            char.dmg_water += 60;
            char.rune3dmg_state = true;
        }
        break;
    case "RES Rune I":
        if (char.rune1res_state == false){
            char.res_earth += 10;
            char.res_air += 10;
            char.res_fire += 10;
            char.res_water += 10;
            char.rune1res_state = true;
        }
        break;
    case "RES Rune II":
        if (char.rune2res_state == false){
            char.res_earth += 20;
            char.res_air += 20;
            char.res_fire += 20;
            char.res_water += 20;
            char.rune2res_state = true;
        }
        break;
    case "RES Rune III":
        if (char.rune3res_state == false){
            char.res_earth += 30;
            char.res_air += 30;
            char.res_fire += 30;
            char.res_water += 30;
            char.rune3res_state = true;
        }
        break;
    case "MP Rune I":
        if (char.rune1mp_state == false){
            char.maxmp += 1;
            char.actmp += 1;
            char.rune1mp_state = true;
        }
        break;
    case "MP Rune II":
        if (char.rune2mp_state == false){
            perc = round(char.acthp * 20 / 100);
            char.maxhp += perc;
            char.acthp += perc;
            char.maxmp += 1;
            char.actmp += 1;
            char.rune2mp_state = true;
        }
        break;
    case "MP Rune III":
        if (char.rune3mp_state == false){
            char.maxmp += 2;
            char.actmp += 2;
            char.rune3mp_state = true;
        }
        break;
    case "PP Rune I":
        if (char.rune1pp_state == false){
            char.maxpp += 1;
            char.actpp += 1;
            char.rune1pp_state = true;
        }
        break;
    case "PP Rune II":
        if (char.rune2pp_state == false){
            char.maxpp += 1;
            char.actpp += 1;
            char.dmg_earth += 25;
            char.dmg_air += 25;
            char.dmg_fire += 25;
            char.dmg_water += 25;
            char.rune2pp_state = true;
        }
        break;
    case "PP Rune III":
        if (char.rune1pp_state == false){
            char.maxpp += 2;
            char.actpp += 2;
            char.rune1pp_state = true;
        }
        break;
    case "Celestial CRI Rune":
        if (char.runeCcri_state == false)
            char.runeCcri_state = true;
        break;
    case "Celestial EARTH Rune":
        if (char.runeCearth_state == false){
            char.dmg_earth += 80;
            char.res_earth += 40;
            char.runeCearth_state = true;
        }
        break;
    case "Celestial AIR Rune":
        if (char.runeCair_state == false){
            char.dmg_air += 80;
            char.res_air += 40;
            char.runeCair_state = true;
        }
        break;
    case "Celestial FIRE Rune":
        if (char.runeCfire_state == false){
            char.dmg_fire += 80;
            char.res_fire += 40;
            char.runeCfire_state = true;
        }
        break;
    case "Celestial WATER Rune":
        if (char.runeCwater_state == false){
            char.dmg_water += 80;
            char.res_water += 40;
            char.runeCwater_state = true;
        }
        break;
}
