///static_createparticle_sprite(sprite, blend, min lifetime, max lifetime, min scale, max scale, scaling);
sprite = argument0;
blend = argument1;
min_lt = argument2;
max_lt = argument3;
min_scale = argument4;
max_scale = argument5;
scaling = argument6;

type = part_type_create();
part_type_alpha2(type, .75, 0);
part_type_sprite(type, sprite, false, true, false);
part_type_blend(type, blend);
part_type_size(type, min_scale, max_scale, scaling, 0);
part_type_life(type, min_lt, max_lt);
part_type_orientation(type, 0, 360, 0, 0, 0);

return type;
