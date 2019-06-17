///executeSpell_JUMP_SP(spell, launcher, tile target);

spell = argument0;
launcher = argument1;
target = argument2;

//check reqs
if (spell.usable == false) {
    //produrre suono per far capire che l'attacco non è andato a buon fine
    return false;
} else {
    
    cf_topdownparticle_explosion(target.x + TILEPX/2, target.y + TILEPX/2, 3);
    
    launcher.acthp -= spell.reqHP;
    launcher.actpp -= spell.reqPP;
    launcher.actmp -= spell.reqMP;
    
    if (spell.name == "SPACE SWAP") {
        tempX = launcher.x;
        tempY = launcher.y;
        launcher.x = target.x;
        launcher.y = target.y;
        target.x = tempX;
        target.y = tempY;
    }   
}
