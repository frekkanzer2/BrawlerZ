///cf_drawbar_life(x of bar, y of bar, obj value, obj max value, border sprite, full part sprite, empty part sprite)
barx = argument0;
bary = argument1;
objval = argument2;
objmaxval = argument3;
border = argument4;
full = argument5;
empty = argument6;

//x diff: 17, y diff: 6
draw_sprite(empty,1,barx,bary);
draw_sprite_ext(full,1,barx,bary,objval/objmaxval,1,0,c_white,1);
draw_sprite(border,1,barx-17,bary-6);

