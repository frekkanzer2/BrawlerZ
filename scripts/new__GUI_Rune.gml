///new__GUI_Rune(x, y, instID);

varx = argument0;
vary = argument1;
idString = argument2;

newRune = instance_create(varx, vary, runeFather);
newRune.idRune = idString;
newRune.buySet = "true";
newRune.fileCanSelect = true;
newRune.image_alpha = 1;

return newRune;
