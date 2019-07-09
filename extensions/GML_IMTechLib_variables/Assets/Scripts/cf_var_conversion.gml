///cf_var_conversion(variable, type [0 for numb to decrpt * 1 for decrpt to numb])

variable = argument0;
type = argument1;

//questa funzione restituisce un valore decriptato
//restituisce -1 se viene passato un numero minore di 1 o maggiore di 10000
if (type == 0){
    if (variable < 1 || variable > 10000) {
        return -1;
    } else {
        vari = ((((((variable + 1250)*2)/4)-100)+1500)/5);
        return vari;
    }
} else if (type == 1){
    vari = ((((((variable*5)-1500)+100)*4)/2)-1250);
    return vari;
} else if (type != 0 && type != 1){
    return -1;
}

