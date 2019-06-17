///calculateMPrange(refereInstance, actualMP);
inst = argument0;
mp = argument1;

up = mp; down = 0-mp; counter = 0; i = counter; j = 0-counter;
while(up >= 0 || down < 0){
    if (counter == 0) {
        instance_create(inst.x, inst.y + TILEPX * up, obj_tileMP);
        instance_create(inst.x, inst.y + TILEPX * down, obj_tileMP);
    } else {
        if (up != 0 && down != 0){
            i = counter; j = 0-counter;
            while(i>0 || j<0){
                instance_create(inst.x + i * TILEPX, inst.y + up * TILEPX, obj_tileMP);
                instance_create(inst.x + i * TILEPX, inst.y + down * TILEPX, obj_tileMP);
                instance_create(inst.x + j * TILEPX, inst.y + up * TILEPX, obj_tileMP);
                instance_create(inst.x + j * TILEPX, inst.y + down * TILEPX, obj_tileMP);
                i--; j++;
            }
            instance_create(inst.x, inst.y + TILEPX * up, obj_tileMP);
            instance_create(inst.x, inst.y + TILEPX * down, obj_tileMP);
        }
        if (up == 0 && down == 0){
            i = counter; j = 0-counter;
            while(i>0 || j<0){
                instance_create(inst.x + i * TILEPX, inst.y + up * TILEPX, obj_tileMP);
                instance_create(inst.x + j * TILEPX, inst.y + up * TILEPX, obj_tileMP);
                i--; j++;
            }
        }
    }
    up--; down++; counter++;
}
