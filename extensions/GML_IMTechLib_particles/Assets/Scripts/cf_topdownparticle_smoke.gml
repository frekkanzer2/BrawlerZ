///cf_topdownparticle_smoke(x,y,repeat number);
var xx = argument0;
var yy = argument1;
var repeat_numb = argument2;

repeat(repeat_numb){
    instance_create(xx-16+random(32), yy-16+random(32), obj_PS_smoke);
}
part_particles_create(obj_particle_system1.system1, xx, yy, obj_particle_system1.effectsmoke_particle_part, 2);
