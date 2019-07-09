///irandom_between(min, max);
vmin = argument0;
vmax = argument1;
if (vmin > vmax) return -1;
if (vmin == vmax) return vmin;
range = vmax - vmin;
result = irandom(range); //da 0 a range
result += vmin;
return result;

