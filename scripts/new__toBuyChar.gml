///new__toBuyChar(charname, charprice, isBuy [false or true], standard sprite, x spawn, y spawn);

inst = instance_create(argument4, argument5, toBuyChar);
inst.charName = argument0;
inst.charPrice = argument1;
inst.isBuy = argument2;
inst.notBuySprite = argument3;
return inst;
