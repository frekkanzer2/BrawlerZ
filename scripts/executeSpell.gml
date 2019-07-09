///executeSpell(spell, launcher, target);
//return false if an error is occured
//return true if it's OK

spell = argument0;
launcher = argument1;
target = argument2;
var dmg;
var dmgup;
var res;
var storedDMG = 0;

//check reqs
if (spell.usable == false || target.dead == true) {
    //produrre suono per far capire che l'attacco non è andato a buon fine
    audio_play_sound(Error, 1, false);
    return false;
}
else {

    cf_topdownparticle_explosion(target.x + TILEPX/2, target.y + TILEPX/2, 3);
    if (spell.dmg_earth > 0) {
        dmg = spell.dmg_earth;
        dmgup = launcher.dmg_earth;
        res = target.res_earth;
    } else if (spell.dmg_fire > 0) {
        dmg = spell.dmg_fire;
        dmgup = launcher.dmg_fire;
        res = target.res_fire;
    } else if (spell.dmg_air > 0) {
        dmg = spell.dmg_air;
        dmgup = launcher.dmg_air;
        res = target.res_air;    
    } else if (spell.dmg_water > 0) {
        dmg = spell.dmg_water;
        dmgup = launcher.dmg_water;
        res = target.res_water;
    } else {
        dmg = 0;
    }
    if (dmg > 0){
        dmgupPERC = (dmg * dmgup / 100);
        dmg += dmgupPERC;
        resPERC = (dmg * res / 100);
        dmg -= resPERC;
        if (spell.rangeCri > 0){
            diffX = cf_var_sub_difference(target.x, launcher.x);
            diffY = cf_var_sub_difference(target.y, launcher.y);
            sumDiff = cf_var_add(diffX, diffY);
            tilesDistance = cf_var_div(sumDiff, TILEPX);
            if (tilesDistance > 0 && tilesDistance <= spell.rangeCri){
                if (launcher.runeCcri_state == true){
                    augment = round(dmg * 75 / 100);
                } else augment = round(dmg * 25 / 100);
                dmg += augment;
            }
        }
        //guadagno per dacoo quando attacca siccome gli attacchi dipendono dalla vita
        if (launcher.name == "DACOO"){
            losenHP = launcher.maxhp - launcher.acthp;
            percGAIN = losenHP / launcher.maxhp;
            realGAIN = percGAIN * 100;
            tempGAINPERC = dmg * realGAIN / 100;
            dmg += tempGAINPERC;
        }
        target.acthp -= round(dmg);
        storedDMG = round(dmg);
    }
    
    if (round(dmg) > 0){
        out = instance_create(target.x + TILEPX/2, target.y + TILEPX/2, outputDMG);
        out.dmg = storedDMG;
        out.target = target;
    }
    
    //remove points to launcher
    
    launcher.acthp -= spell.reqHP;
    launcher.actpp -= spell.reqPP;
    launcher.actmp -= spell.reqMP;
    
    if (spell.type == "heal") {
        target.acthp += spell.heal;
    }
    
    if (spell.useEffects == true) {
        //controllo in base al nome dello spell
        
        /***ADD HERE NEW SPELLS WITH EFFECTS***/
        
        //case AGONY - DEDHALUS
        if (spell.name == "AGONY"){
            launcher.acthp += round(dmg * 30 / 100);
            if (launcher.acthp > launcher.maxhp)
                launcher.acthp = launcher.maxhp;
        }
        
        //case SANDIFICATION - SHANA
        if (spell.name == "SANDIFICATION"){
            if (target.state_heaviness == false) {
                target.maxmp -= 1;
                target.state_heaviness = true;
                if (target.maxmp < 0) target.maxmp = 0;
            }
            if (target.state_vulnerability == false) {
                target.res_earth -= 20;
                target.res_fire -= 20;
                target.res_air -= 20;
                target.res_water -= 20;
                target.state_vulnerability = true;
            }
            if (target.state_rage == false){
                target.dmg_earth += 20;
                target.dmg_fire += 20;
                target.dmg_air += 20;
                target.dmg_water += 20;
                target.state_rage = true;
            }
        }
        
        //case TORNADO - SHANA
        if (spell.name == "TORNADO"){
            if (target.state_damaged == false) {
                rand1 = irandom_between(1,4);
                switch(rand1){
                    case 1:
                        target.res_earth -= 30;
                        break;
                    case 2:
                        target.res_fire -= 30;
                        break;
                    case 3:
                        target.res_air -= 30;
                        break;
                    case 4:
                        target.res_water -= 30;
                        break;
                }
                target.state_damaged = true;
            }
        }
        
        //case ARMOR X - HOZOR
        if (spell.name == "ARMOR X"){
            if (target.state_hyperfury == false){
                rand2 = irandom_between(1,4);
                switch(rand2){
                    case 1:
                        target.dmg_earth += 30;
                        break;
                    case 2:
                        target.dmg_fire += 30;
                        break;
                    case 3:
                        target.dmg_air += 30;
                        break;
                    case 4:
                        target.dmg_water += 30;
                        break;
                }
                target.state_hyperfury = true;
            } else {
                target.dmg_earth += 8;
                target.dmg_fire += 8;
                target.dmg_air += 8;
                target.dmg_water += 8;
            }
            if (target.state_tank == false) {
                target.res_earth += 20;
                target.res_fire += 20;
                target.res_air += 20;
                target.res_water += 20;
                target.state_tank = true;
            } else {
                target.res_earth += 4;
                target.res_fire += 4;
                target.res_air += 4;
                target.res_water += 4;
            }
        }
        
        //case BASTION - AKIRIA
        if (spell.name == "BASTION"){
            target.res_earth += 3;
            target.res_fire += 3;
            target.res_air += 3;
            target.res_water += 3;
            rand3 = irandom_between(1,4);
            switch(rand3){
                case 1:
                    target.res_earth += 2;
                    break;
                case 2:
                    target.res_fire += 2;
                    break;
                case 3:
                    target.res_air += 2;
                    break;
                case 4:
                    target.res_water += 2;
                    break;
            }
        }
        
        //case HEALING WORD - AKIRIA
        if (spell.name == "HEALING WORD"){
            rand4 = irandom_between(1,4);
            switch(rand4){
                case 1:
                    target.res_earth += 4;
                    break;
                case 2:
                    target.res_fire += 4;
                    break;
                case 3:
                    target.res_air += 4;
                    break;
                case 4:
                    target.res_water += 4;
                    break;
            }
        }
        
        //case SHIELD BREAKER - AKIRIA
        if (spell.name == "SHIELD BREAKER"){
            if (target.state_vulnerability == false) {
                target.res_earth -= 20;
                target.res_fire -= 20;
                target.res_air -= 20;
                target.res_water -= 20;
                target.state_vulnerability = true;
            }
        }
        
        //case EXALTED FLAME - BARBEQUU
        if (spell.name == "EXALTED FLAME"){
            if (target.state_hyperpower == false) {
                target.maxpp += 1;
                target.state_hyperpower = true;
                if (target.maxpp > PPLIMIT) target.maxpp = PPLIMIT;
            }
            if (target.state_rage == false){
                target.dmg_earth += 20;
                target.dmg_fire += 20;
                target.dmg_air += 20;
                target.dmg_water += 20;
                target.state_rage = true;
            }
        }
        
        //case MOTIVATING TEAR - LAURETTE
        if (spell.name == "ABSOLUTION"){
            if (target.state_hyperpower == false) {
                target.maxpp += 1;
                target.state_hyperpower = true;
                if (target.maxpp > PPLIMIT) target.maxpp = PPLIMIT;
            }
            if (target.state_hyperfury == false){
                rand5 = irandom_between(1,4);
                switch(rand5){
                    case 1:
                        target.dmg_earth += 30;
                        break;
                    case 2:
                        target.dmg_fire += 30;
                        break;
                    case 3:
                        target.dmg_air += 30;
                        break;
                    case 4:
                        target.dmg_water += 30;
                        break;
                }
                target.state_hyperfury = true;
            }
            if (target.state_motivation == false){
                temphp = round(target.maxhp * 10 / 100);
                target.maxhp += temphp;
                target.acthp += temphp;
                if (target.maxhp > HPLIMIT) target.maxhp = HPLIMIT;
                if (target.acthp > HPLIMIT) target.acthp = HPLIMIT;
                target.state_motivation = true;
            }
        }
        
        //case PUNISHMENT - LAURETTE
        if (spell.name == "PUNISHMENT"){
            rand6 = irandom_between(1,4);
            switch(rand6){
                case 1:
                    target.res_earth -= 8;
                    break;
                case 2:
                    target.res_fire -= 8;
                    break;
                case 3:
                    target.res_air -= 8;
                    break;
                case 4:
                    target.res_water -= 8;
                    break;
            }
        }
        
        //case ACUTENESS - TROFU
        if (spell.name == "ACUTENESS"){
            if (target.state_hyperfury == false){
                rand7 = irandom_between(1,4);
                switch(rand7){
                    case 1:
                        target.dmg_earth += 30;
                        break;
                    case 2:
                        target.dmg_fire += 30;
                        break;
                    case 3:
                        target.dmg_air += 30;
                        break;
                    case 4:
                        target.dmg_water += 30;
                        break;
                }
                target.state_hyperfury = true;
            }
             if (target.state_rage == false){
                target.dmg_earth += 20;
                target.dmg_fire += 20;
                target.dmg_air += 20;
                target.dmg_water += 20;
                target.state_rage = true;
            }
        }
        
        //case SLOW ARROW - TROFU
        if (spell.name == "SLOW ARROW"){
            if (target.state_heaviness == false) {
                target.maxmp -= 1;
                target.state_heaviness = true;
                if (target.maxmp < 0) target.maxmp = 0;
            }
            if (target.state_fatigue == false) {
                target.maxpp -= 1;
                target.state_fatigue = true;
                if (target.maxpp < 0) target.maxpp = 0;
            }
        }
        
        //case MOODY ARROW - TROFU
        if (spell.name == "MOODY ARROW"){
            target.res_water -= 10;
        }
        
        //case FIREBALL - BARBEQUU
        if (spell.name == "FIREBALL"){
            target.res_fire -= 10;
        }
        
        //case HAPPINESS - ZARAIDA
        if (spell.name == "HAPPINESS"){
            rand8 = irandom_between(1,4);
            switch(rand8){
                case 1:
                    target.dmg_earth += 4;
                    break;
                case 2:
                    target.dmg_fire += 4;
                    break;
                case 3:
                    target.dmg_air += 4;
                    break;
                case 4:
                    target.dmg_water += 4;
                    break;
            }
        }
        
        //case SMILE - ZARAIDA
        if (spell.name == "SMILE"){
            target.dmg_earth += 4;
            target.dmg_fire += 4;
            target.dmg_air += 4;
            target.dmg_water += 4;
            target.res_earth += 3;
            target.res_fire += 3;
            target.res_air += 3;
            target.res_water += 3;
        }
        
        //case PRIG - ZARAIDA
        if (spell.name == "PRIG"){
            if (target.state_fatigue == false) {
                target.maxpp -= 1;
                target.state_fatigue = true;
                if (target.maxpp < 0) target.maxpp = 0;
            }
            if (target.state_painfulness == false){
                temphp = round(target.maxhp * 10 / 100);
                target.maxhp -= temphp;
                if (target.maxhp < 0) target.dead = true;
                if (target.acthp > target.maxhp) target.acthp = target.maxhp;
                target.state_painfulness = true;
            }
        }
        
        //case DEPRESSION - ZARAIDA
        if (spell.name == "DEPRESSION"){
            target.dmg_earth -= 4;
            target.dmg_fire -= 4;
            target.dmg_air -= 4;
            target.dmg_water -= 4;
            target.res_earth -= 4;
            target.res_fire -= 4;
            target.res_air -= 4;
            target.res_water -= 4;
        }
        
        //case EAGLE EYE - DRIB
        if (spell.name == "EAGLE EYE"){
            if (target.state_rage == false){
                target.dmg_earth += 20;
                target.dmg_fire += 20;
                target.dmg_air += 20;
                target.dmg_water += 20;
                target.state_rage = true;
            }
            if (target.state_hyperfury == false){
                rand9 = irandom_between(1,4);
                switch(rand9){
                    case 1:
                        target.dmg_earth += 30;
                        break;
                    case 2:
                        target.dmg_fire += 30;
                        break;
                    case 3:
                        target.dmg_air += 30;
                        break;
                    case 4:
                        target.dmg_water += 30;
                        break;
                }
                target.state_hyperfury = true;
            }
            if (target.state_agility == false) {
                target.maxmp += 1;
                target.state_agility = true;
                if (target.maxmp > MPLIMIT) target.maxmp = MPLIMIT;
            }
        }
        
        //case FIRE FEATHER - DRIB
        if (spell.name == "FIRE FEATHER"){
            target.dmg_earth -= 3;
            target.dmg_fire -= 3;
            target.dmg_air -= 3;
            target.dmg_water -= 3;
        }
        
        //case DEMON BITE - MONOLITH
        if (spell.name == "DEMON BITE"){
            launcher.acthp += round(dmg * 80 / 100);
            if (launcher.acthp > launcher.maxhp)
                launcher.acthp = launcher.maxhp;
        }
        
        //case DEMON MUSCLES - MONOLITH
        if (spell.name == "DEMON MUSCLES"){
            if (target.state_motivation == false){
                temphp = round(target.maxhp * 10 / 100);
                target.maxhp += temphp;
                target.acthp += temphp;
                if (target.maxhp > HPLIMIT) target.maxhp = HPLIMIT;
                if (target.acthp > HPLIMIT) target.acthp = HPLIMIT;
                target.state_motivation = true;
            }
            if (target.state_tank == false) {
                target.res_earth += 20;
                target.res_fire += 20;
                target.res_air += 20;
                target.res_water += 20;
                target.state_tank = true;
            }
            if (target.state_rage == false){
                target.dmg_earth += 20;
                target.dmg_fire += 20;
                target.dmg_air += 20;
                target.dmg_water += 20;
                target.state_rage = true;
            }
            if (target.state_heaviness == false) {
                target.maxmp -= 1;
                target.state_heaviness = true;
                if (target.maxmp < 0) target.maxmp = 0;
            }
        }
        
        //case VICTIM - XORIUM
        if (spell.name == "VICTIM"){
            if (target.state_heaviness == false) {
                target.maxmp -= 1;
                target.state_heaviness = true;
                if (target.maxmp < 0) target.maxmp = 0;
            }
            if (target.state_fatigue == false) {
                target.maxpp -= 1;
                target.state_fatigue = true;
                if (target.maxpp < 0) target.maxpp = 0;
            }
            if (target.state_painfulness == false){
                temphp = round(target.maxhp * 10 / 100);
                target.maxhp -= temphp;
                if (target.maxhp < 0) target.dead = true;
                if (target.acthp > target.maxhp) target.acthp = target.maxhp;
                target.state_painfulness = true;
            }
        }
        
        //case BULLYING - KAITHO
        if (spell.name == "BULLYING"){
            target.dmg_earth -= 3;
            target.dmg_fire -= 3;
            target.dmg_air -= 3;
            target.dmg_water -= 3;
            target.res_earth -= 3;
            target.res_fire -= 3;
            target.res_air -= 3;
            target.res_water -= 3;
        }
        
        //case THREAT - KAITHO
        if (spell.name == "THREAT"){
            target.dmg_earth += 3;
            target.dmg_fire += 3;
            target.dmg_air += 3;
            target.dmg_water += 3;
        }
        
        //case KAIWRATH - KAITHO
        if (spell.name == "KAIWRATH"){
            launcher.dmg_earth += 50;
        }
        
        //case SPACE ENERGY - ETORP
        if (spell.name == "SPACE ENERGY"){
            target.res_water -= 10;
        }
        
        //case SPACE HELMET - ETORP
        if (spell.name == "SPACE HELMET"){
            if (target.state_tank == false) {
                target.res_earth += 20;
                target.res_fire += 20;
                target.res_air += 20;
                target.res_water += 20;
                target.state_tank = true;
            }
        }
        
        //case GODLY POWER - NAMOCO
        if (spell.name == "GODLY POWER"){
            target.res_earth = -50;
            target.res_fire = -50;
            target.res_air = -50;
            target.res_water = -50;
            target.dmg_earth = 150;
            target.dmg_fire = 150;
            target.dmg_air = 150;
            target.dmg_water = 150;
            if (target.state_hyperpower == false) {
                target.maxpp += 1;
                target.state_hyperpower = true;
                if (target.maxpp > PPLIMIT) target.maxpp = PPLIMIT;
            }
        }
        
        //case CONTAGION - DACOO
        if (spell.name == "CONTAGION"){
            if (target.state_vulnerability == false) {
                target.res_earth -= 20;
                target.res_fire -= 20;
                target.res_air -= 20;
                target.res_water -= 20;
                target.state_vulnerability = true;
            }
            if (target.state_weakness == false){
                target.dmg_earth -= 20;
                target.dmg_fire -= 20;
                target.dmg_air -= 20;
                target.dmg_water -= 20;
                target.state_weakness = true;
            }
        }
        
        //case HOSTILITY - DACOO
        if (spell.name == "HOSTILITY"){
            launcher.acthp += round(dmg * 50 / 100);
            if (launcher.acthp > launcher.maxhp)
                launcher.acthp = launcher.maxhp;
        }
        
        if(spell.name == "GREEN TIGER"){
            target.res_earth -= 15;
        }
        
        /***END OF SPELLS WITH EFFECTS BLOCK***/
        
    }
    
    if (target.acthp <= 0)
        audio_play_sound(DeathSound, 1, false);
    
}
