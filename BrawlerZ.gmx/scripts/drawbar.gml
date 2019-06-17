///drawbar(x of bar, y of bar, obj value, obj max value, backsprite, uppersprite, bordersprite);

barx = argument0;
bary = argument1;
objval = argument2;
objmaxval = argument3;
dwn = argument4;
upp = argument5;
brd = argument6;

//x diff: 17, y diff: 6
draw_sprite(dwn,1,barx,bary);
draw_sprite_ext(upp,1,barx,bary,objval/objmaxval,1,0,c_white,1);
draw_sprite(brd,1,barx,bary);
