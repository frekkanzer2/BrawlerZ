///calculatePPrange(refereInstance, range);
inst = argument0;
pp = argument1;

//up indica l'estremo superiore con valore range passato da argomento
up = pp;
//down indica l'estremo inferiore con valore range negativo calcolato da argomento
down = 0-pp;
//counter verrà incrementato ogni volta: indica la i limiti dx e sx
counter = 0;
//i indica l'estremo destro
i = counter;
//j indica l'estremo sinistro
j = 0-counter;
while(up >= 0 || down < 0){
    if (counter == 0) {
        instance_create(inst.x, inst.y + TILEPX * up, obj_tilePP);
        instance_create(inst.x, inst.y + TILEPX * down, obj_tilePP);
    } else {
        if (up != 0 && down != 0){
            i = counter; j = 0-counter;
            while(i>0 || j<0){
                instance_create(inst.x + i * TILEPX, inst.y + up * TILEPX, obj_tilePP);
                instance_create(inst.x + i * TILEPX, inst.y + down * TILEPX, obj_tilePP);
                instance_create(inst.x + j * TILEPX, inst.y + up * TILEPX, obj_tilePP);
                instance_create(inst.x + j * TILEPX, inst.y + down * TILEPX, obj_tilePP);
                i--; j++;
            }
            instance_create(inst.x, inst.y + TILEPX * up, obj_tilePP);
            instance_create(inst.x, inst.y + TILEPX * down, obj_tilePP);
        }
        if (up == 0 && down == 0){
            i = counter; j = 0-counter;
            while(i>0 || j<0){
                instance_create(inst.x + i * TILEPX, inst.y + up * TILEPX, obj_tilePP);
                instance_create(inst.x + j * TILEPX, inst.y + up * TILEPX, obj_tilePP);
                i--; j++;
            }
        }
    }
    up--; down++; counter++;
}
