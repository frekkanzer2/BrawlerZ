///cf_var_conversion_operation(decrpt value1, decrpt value2, type[0 for add * 1 for sub * 2 for mult * 3 for div])
value1 = argument0;
value2 = argument1;
type = argument2;

//restituisce il valore dell'operazione effettuata in valore decriptato
//restituisce 00000 in caso di problemi

temp1 = cf_var_conversion(value1,1);
temp2 = cf_var_conversion(value2,1);
var total;

switch(type){
    case 0:
        total = cf_var_add(temp1,temp2);
        break;
    case 1:
        total = cf_var_sub(temp1,temp2);
        break;
    case 2:
        total = cf_var_mult(temp1,temp2);
        break;
    case 3:
        total = cf_var_div(temp1,temp2);
        break;
    default:
        total = 00000;
        break;
}

if (total != 00000){
    retvar = cf_var_conversion(total,0);
}

return retvar;

