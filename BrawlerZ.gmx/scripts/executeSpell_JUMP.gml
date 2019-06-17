///executeSpell_JUMP(spell, launcher, tile target);

spell = argument0;
launcher = argument1;
tile = argument2;

//check reqs
if (spell.usable == false) {
    //produrre suono per far capire che l'attacco non è andato a buon fine
    return false;
} else {
    
    cf_topdownparticle_explosion(tile.x + TILEPX/2, tile.y + TILEPX/2, 3);
    
    launcher.acthp -= spell.reqHP;
    launcher.actpp -= spell.reqPP;
    launcher.actmp -= spell.reqMP;
    
    if (spell.name != "SPACE SWAP") {
        launcher.x = tile.x;
        launcher.y = tile.y;
    }   
}
