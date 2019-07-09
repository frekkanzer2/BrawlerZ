///cf_draw_ammo(x, y, actual ammo, max ammo, font)

xval = argument0;
yval = argument1;
ammo = argument2;
ammomax = argument3;
fontt = argument4;

draw_set_halign(fa_center);
draw_set_font(fontt);
draw_set_colour(c_black);
draw_text(xval,yval,string(ammo) + "/" + string(ammomax));

