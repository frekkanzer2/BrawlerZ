///cf_topdownparticle_firetype_sphere(x, y, depth, min size, max size, spawn variation);
//IM Tech

xval = argument0;
yval = argument1;
depthpart = argument2;
minsize = argument3;
maxsize = argument4;
spawnvariation = argument5;

x = xval;
y = yval;

//Particle_System
part1_sys = part_system_create();
part_system_depth(part1_sys,depthpart);

//Particle
part1 = part_type_create();
part_type_shape(part1, pt_shape_sphere);
part_type_scale(part1,1,1);
part_type_size(part1,minsize,maxsize,-0.005,0);
part_type_colour3(part1, c_red, c_orange, c_white);
part_type_alpha2(part1,1,0.75);
part_type_speed(part1,0.1,0.5,0,0);
part_type_direction(part1, 0, 359,0,0);

//Particle Emitter
part1_emit = part_emitter_create(part1_sys);
part_emitter_region(part1_sys, part1_emit, x-spawnvariation, x+spawnvariation, y-spawnvariation, y+spawnvariation, ps_shape_ellipse, ps_distr_gaussian);
part_emitter_stream(part1_sys, part1_emit, part1, 1);

