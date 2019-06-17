///setMenu_btns(distance, heigth);
distance = argument0;
heigth = argument1;
var actualDistance = 300;
var i = 0;
while (i < 3){
    if (i == 0){
        instance_create(actualDistance, heigth, btnHost);
        actualDistance = actualDistance + 230;
    }
    if (i == 1) {
        actualDistance = actualDistance + distance;
        instance_create(actualDistance, heigth, btnJoin);
        actualDistance = actualDistance + 230;
    }
    if (i == 2) {
        actualDistance = actualDistance + distance;
        instance_create(actualDistance, heigth, btnLocalPlay);
    }
    i++;
}
