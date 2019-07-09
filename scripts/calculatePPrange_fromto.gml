///calculatePPrange_fromto(refereInstance, rangeStart, rangeEnd);
inst = argument0;
rstart = argument1;
rend = argument2;

//up indica l'estremo superiore con valore range passato da argomento
up = rend;
//down indica l'estremo inferiore con valore range negativo calcolato da argomento
down = 0-rend;
//limitUp indica il minimo estremo superiore
limitUp = rstart;
//limitDown indica il minimo estremo inferiore
limitDown = 0-rstart;
//highCounter verrà incrementato ogni volta: indica la i limiti dx e sx
highCounter = 0; //max of highCounter = rend
//loseCounter verrà incrementato in modo da non creare caselle entro una certa posizione
loseCounter = 0;
//i indica l'estremo sinistro
i = 0;
//j indica l'estremo destro
j = 0;

while(up > 0 || down < 0){
    if (highCounter == 0) {
        instance_create(inst.x, inst.y + up * TILEPX, obj_tilePP);
        instance_create(inst.x, inst.y + down * TILEPX, obj_tilePP);
    } else {
        if (up >= limitUp || down <= limitDown){
            i = 0-highCounter;
            j = highCounter;
            while(i < 0 || j > 0){
                instance_create(inst.x + i * TILEPX, inst.y + up * TILEPX, obj_tilePP);
                instance_create(inst.x + j * TILEPX, inst.y + up * TILEPX, obj_tilePP);
                instance_create(inst.x + i * TILEPX, inst.y + down * TILEPX, obj_tilePP);
                instance_create(inst.x + j * TILEPX, inst.y + down * TILEPX, obj_tilePP);
                i++; j--;
            }
            instance_create(inst.x, inst.y + up * TILEPX, obj_tilePP);
            instance_create(inst.x, inst.y + down * TILEPX, obj_tilePP);
        } else {
            //caso nel quale si presentano gli spazi vuoti
            loseCounter++;
            i = 0-highCounter;
            j = highCounter;
            while(i <= 0-loseCounter || j >= loseCounter){
                instance_create(inst.x + i * TILEPX, inst.y + up * TILEPX, obj_tilePP);
                instance_create(inst.x + j * TILEPX, inst.y + up * TILEPX, obj_tilePP);
                instance_create(inst.x + i * TILEPX, inst.y + down * TILEPX, obj_tilePP);
                instance_create(inst.x + j * TILEPX, inst.y + down * TILEPX, obj_tilePP);
                i++; j--;
            }
        }
    }
    up--;
    down++;
    highCounter++;
}

minv = argument1;
maxv = argument2;
while (minv <= maxv){
    instance_create(inst.x + minv * TILEPX, inst.y, obj_tilePP);
    instance_create(inst.x + (0-minv) * TILEPX, inst.y, obj_tilePP);
    minv++;
}
