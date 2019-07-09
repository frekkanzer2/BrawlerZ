///cf_topdownparticle_explosion(x,y,repeat number);
var xx = argument0;
var yy = argument1;
var repeat_numb = argument2;

repeat(repeat_numb){
    instance_create(xx-16+random(32), yy-16+random(32), obj_PS_explosion);
}
part_particles_create(obj_particle_system.system0, xx, yy, obj_particle_system.explosion_center_part, 2);
