///new__Character(name, team, startPoint_to_traslate);

charname = argument0;
team = argument1;
ptt = argument2;
var character;

/***CHARS BOX***/
//add here new chars

//dedhalus
if (charname == "Dedhalus" || charname == "dedhalus") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "DEDHALUS";
    character.identity = "C01";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_dedhalus;
    character.preview = spr_c_dedhalus;
    character.maxhp = 870;
    character.maxpp = 6;
    character.maxmp = 3;
    character.acthp = 870;
    character.actpp = 6;
    character.actmp = 3;
    character.dmg_earth = 8;
    character.dmg_fire = 0;
    character.dmg_air = 15;
    character.dmg_water = 0;
    character.res_earth = 5;
    character.res_fire = 0;
    character.res_air = 22;
    character.res_water = 0;
    character.spells[0] = spell1;
    character.spells[1] = spell2;
    character.spells[2] = spell3;
    character.spells[3] = spell4;
    character.sprite_index = character.sprite;
}

//emerald
if (charname == "Emerald" || charname == "emerald") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "EMERALD";
    character.identity = "C02";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_emerald;
    character.preview = spr_c_emerald;
    character.maxhp = 820;
    character.maxpp = 6;
    character.maxmp = 3;
    character.acthp = 820;
    character.actpp = 6;
    character.actmp = 3;
    character.dmg_earth = 12;
    character.dmg_fire = 4;
    character.dmg_air = 0;
    character.dmg_water = 0;
    character.res_earth = 20;
    character.res_fire = 15;
    character.res_air = -6;
    character.res_water = -8;
    character.spells[0] = spell5;
    character.spells[1] = spell6;
    character.spells[2] = spell7;
    character.spells[3] = spell8;
    character.sprite_index = character.sprite;
}

//shana
if (charname == "Shana" || charname == "shana") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "SHANA";
    character.identity = "C03";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_shana;
    character.preview = spr_c_shana;
    character.maxhp = 670;
    character.maxpp = 9;
    character.maxmp = 4;
    character.acthp = 670;
    character.actpp = 9;
    character.actmp = 4;
    character.dmg_earth = 27;
    character.dmg_fire = 0;
    character.dmg_air = -35;
    character.dmg_water = 0;
    character.res_earth = 28;
    character.res_fire = 22;
    character.res_air = -38;
    character.res_water = -5;
    character.spells[0] = spell9;
    character.spells[1] = spell10;
    character.spells[2] = spell11;
    character.spells[3] = spell12;
    character.sprite_index = character.sprite;
}

//hozor
if (charname == "Hozor" || charname == "hozor") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "HOZOR";
    character.identity = "C04";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_hozor;
    character.preview = spr_c_hozor;
    character.maxhp = 630;
    character.maxpp = 5;
    character.maxmp = 3;
    character.acthp = 630;
    character.actpp = 5;
    character.actmp = 3;
    character.dmg_earth = 0;
    character.dmg_fire = 8;
    character.dmg_air = -10;
    character.dmg_water = 0;
    character.res_earth = 0;
    character.res_fire = 30;
    character.res_air = -5;
    character.res_water = -40;
    character.spells[0] = spell13;
    character.spells[1] = spell14;
    character.spells[2] = spell15;
    character.spells[3] = spell16;
    character.sprite_index = character.sprite;
}

//akiria
if (charname == "Akiria" || charname == "akiria") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "AKIRIA";
    character.identity = "C05";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_akiria;
    character.preview = spr_c_akiria;
    character.maxhp = 1200;
    character.maxpp = 6;
    character.maxmp = 2;
    character.acthp = 1200;
    character.actpp = 6;
    character.actmp = 2;
    character.dmg_earth = 0;
    character.dmg_fire = 0;
    character.dmg_air = 0;
    character.dmg_water = 0;
    character.res_earth = 10;
    character.res_fire = 10;
    character.res_air = 10;
    character.res_water = 10;
    character.spells[0] = spell17;
    character.spells[1] = spell18;
    character.spells[2] = spell19;
    character.spells[3] = spell20;
    character.sprite_index = character.sprite;
}

//barbequu
if (charname == "Barbequu" || charname == "barbequu") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "BARBEQUU";
    character.identity = "C06";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_barbequu;
    character.preview = spr_c_barbequu;
    character.maxhp = 740;
    character.maxpp = 5;
    character.maxmp = 3;
    character.acthp = 740;
    character.actpp = 5;
    character.actmp = 3;
    character.dmg_earth = 0;
    character.dmg_fire = 8;
    character.dmg_air = 0;
    character.dmg_water = -30;
    character.res_earth = 4;
    character.res_fire = 22;
    character.res_air = 0;
    character.res_water = -60;
    character.spells[0] = spell21;
    character.spells[1] = spell22;
    character.spells[2] = spell23;
    character.spells[3] = spell24;
    character.sprite_index = character.sprite;
}

//laurette
if (charname == "Laurette" || charname == "laurette") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "LAURETTE";
    character.identity = "C07";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_laurette;
    character.preview = spr_c_laurette;
    character.maxhp = 720;
    character.maxpp = 8;
    character.maxmp = 3;
    character.acthp = 720;
    character.actpp = 8;
    character.actmp = 3;
    character.dmg_earth = 0;
    character.dmg_fire = 0;
    character.dmg_air = 13;
    character.dmg_water = 18;
    character.res_earth = 8;
    character.res_fire = -40;
    character.res_air = 8;
    character.res_water = 22;
    character.spells[0] = spell25;
    character.spells[1] = spell26;
    character.spells[2] = spell27;
    character.spells[3] = spell28;
    character.sprite_index = character.sprite;
}

//trofu
if (charname == "Trofu" || charname == "trofu") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "TROFU";
    character.identity = "C08";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_trofu;
    character.preview = spr_c_trofu;
    character.maxhp = 640;
    character.maxpp = 7;
    character.maxmp = 4;
    character.acthp = 640;
    character.actpp = 7;
    character.actmp = 4;
    character.dmg_earth = 0;
    character.dmg_fire = 0;
    character.dmg_air = 22;
    character.dmg_water = -7;
    character.res_earth = 4;
    character.res_fire = 2;
    character.res_air = 35;
    character.res_water = 0;
    character.spells[0] = spell29;
    character.spells[1] = spell30;
    character.spells[2] = spell31;
    character.spells[3] = spell32;
    character.sprite_index = character.sprite;
}

//zaraida
if (charname == "Zaraida" || charname == "zaraida") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "ZARAIDA";
    character.identity = "C09";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_zaraida;
    character.preview = spr_c_zaraida;
    character.maxhp = 950;
    character.maxpp = 5;
    character.maxmp = 2;
    character.acthp = 950;
    character.actpp = 5;
    character.actmp = 2;
    character.dmg_earth = 0;
    character.dmg_fire = 0;
    character.dmg_air = 0;
    character.dmg_water = 0;
    character.res_earth = 8;
    character.res_fire = 8;
    character.res_air = 8;
    character.res_water = 8;
    character.spells[0] = spell33;
    character.spells[1] = spell34;
    character.spells[2] = spell35;
    character.spells[3] = spell36;
    character.sprite_index = character.sprite;
}

//drib
if (charname == "Drib" || charname == "drib") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "DRIB";
    character.identity = "C10";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_drib;
    character.preview = spr_c_drib;
    character.maxhp = 780;
    character.maxpp = 7;
    character.maxmp = 5;
    character.acthp = 780;
    character.actpp = 7;
    character.actmp = 5;
    character.dmg_earth = -15;
    character.dmg_fire = 16;
    character.dmg_air = 10;
    character.dmg_water = -7;
    character.res_earth = -35;
    character.res_fire = 24;
    character.res_air = 22;
    character.res_water = -40;
    character.spells[0] = spell37;
    character.spells[1] = spell38;
    character.spells[2] = spell39;
    character.spells[3] = spell40;
    character.sprite_index = character.sprite;
}

//monolith
if (charname == "Monolith" || charname == "monolith") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "MONOLITH";
    character.identity = "C11";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_monolith;
    character.preview = spr_c_monolith;
    character.maxhp = 870;
    character.maxpp = 8;
    character.maxmp = 3;
    character.acthp = 870;
    character.actpp = 8;
    character.actmp = 3;
    character.dmg_earth = 0;
    character.dmg_fire = -14;
    character.dmg_air = 12;
    character.dmg_water = -8;
    character.res_earth = 2;
    character.res_fire = -16;
    character.res_air = 10;
    character.res_water = -7;
    character.spells[0] = spell41;
    character.spells[1] = spell42;
    character.spells[2] = spell43;
    character.spells[3] = spell44;
    character.sprite_index = character.sprite;
}

//xorium
if (charname == "Xorium" || charname == "xorium") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "XORIUM";
    character.identity = "C12";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_xorium;
    character.preview = spr_c_xorium;
    character.maxhp = 750;
    character.maxpp = 10;
    character.maxmp = 0;
    character.acthp = 750;
    character.actpp = 10;
    character.actmp = 0;
    character.dmg_earth = -20;
    character.dmg_fire = -24;
    character.dmg_air = 16;
    character.dmg_water = 22;
    character.res_earth = -24;
    character.res_fire = -30;
    character.res_air = 18;
    character.res_water = 30;
    character.spells[0] = spell45;
    character.spells[1] = spell46;
    character.spells[2] = spell47;
    character.spells[3] = spell48;
    character.sprite_index = character.sprite;
}

//kaitho
if (charname == "Kaitho" || charname == "kaitho") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "KAITHO";
    character.identity = "C13";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_kaitho;
    character.preview = spr_c_kaitho;
    character.maxhp = 700;
    character.maxpp = 7;
    character.maxmp = 3;
    character.acthp = 700;
    character.actpp = 7;
    character.actmp = 3;
    character.dmg_earth = 15;
    character.dmg_fire = 0;
    character.dmg_air = 8;
    character.dmg_water = 0;
    character.res_earth = 30;
    character.res_fire = 0;
    character.res_air = 5;
    character.res_water = 0;
    character.spells[0] = spell49;
    character.spells[1] = spell50;
    character.spells[2] = spell51;
    character.spells[3] = spell52;
    character.sprite_index = character.sprite;
}

//etorp
if (charname == "Etorp" || charname == "etorp") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "ETORP";
    character.identity = "C14";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_etorp;
    character.preview = spr_c_etorp;
    character.maxhp = 950;
    character.maxpp = 6;
    character.maxmp = 3;
    character.acthp = 950;
    character.actpp = 6;
    character.actmp = 3;
    character.dmg_earth = -4;
    character.dmg_fire = -26;
    character.dmg_air = 0;
    character.dmg_water = 15;
    character.res_earth = 0;
    character.res_fire = -5;
    character.res_air = 10;
    character.res_water = 35;
    character.spells[0] = spell53;
    character.spells[1] = spell54;
    character.spells[2] = spell55;
    character.spells[3] = spell56;
    character.sprite_index = character.sprite;
}

//namoco
if (charname == "Namoco" || charname == "namoco") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "NAMOCO";
    character.identity = "C15";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_namoco;
    character.preview = spr_c_namoco;
    character.maxhp = 1040;
    character.maxpp = 7;
    character.maxmp = 3;
    character.acthp = 1040;
    character.actpp = 7;
    character.actmp = 3;
    character.dmg_earth = 4;
    character.dmg_fire = 2;
    character.dmg_air = 8;
    character.dmg_water = 0;
    character.res_earth = 5;
    character.res_fire = 5;
    character.res_air = 12;
    character.res_water = 2;
    character.spells[0] = spell57;
    character.spells[1] = spell58;
    character.spells[2] = spell59;
    character.spells[3] = spell60;
    character.sprite_index = character.sprite;
}

//dacoo
if (charname == "Dacoo" || charname == "dacoo") {
    character = instance_create(ptt.varx,ptt.vary,obj_character);
    character.name = "DACOO";
    character.identity = "C16";
    if (team == "red" || team == "RED" || team == "Red")
        character.team = "red";
    else character.team = "blue";
    character.myturn = false;
    character.sprite = spr_t_dacoo;
    character.preview = spr_c_dacoo;
    character.maxhp = 1350;
    character.maxpp = 8;
    character.maxmp = 3;
    character.acthp = 1350;
    character.actpp = 8;
    character.actmp = 3;
    character.dmg_earth = 0;
    character.dmg_fire = 0;
    character.dmg_air = 0;
    character.dmg_water = 0;
    character.res_earth = 0;
    character.res_fire = 0;
    character.res_air = 0;
    character.res_water = 0;
    character.spells[0] = spell61;
    character.spells[1] = spell62;
    character.spells[2] = spell63;
    character.spells[3] = spell64;
    character.sprite_index = character.sprite;
}

/***END CHARS BOX***/

return character;
