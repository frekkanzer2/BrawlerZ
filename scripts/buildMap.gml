///buildMap();
customX = PIXELDIFF_X;
customY = PIXELDIFF_Y;

randomPicked = irandom_between(0,5);

switch(randomPicked){
    //case 0
    case 0:
        //soundtrack
        audio_play_sound(music_preset_1_4, 10, true);
        for(i = 0; i < 7; i++){
            customX = PIXELDIFF_X;
            for(j = 0; j < 15; j++){
                randomNumber = irandom(1); //restituisce un numero int tra 0 e 1
                if (randomNumber == 0){
                    instance_create(customX,customY,obj_p1t1);
                }
                if (randomNumber == 1){
                    instance_create(customX,customY,obj_p1t2);
                }
                customX += TILEPX;
            }
            customY += TILEPX;
        }
        break;
    //case 1
    case 1:
        //soundtrack
        audio_play_sound(music_preset_2_3, 10, true);
        for(i = 0; i < 7; i++){
            customX = PIXELDIFF_X;
            for(j = 0; j < 15; j++){
                instance_create(customX,customY,obj_p2t1);
                customX += TILEPX;
            }
            customY += TILEPX;
        }
        break;
    //case 2
    case 2:
        //soundtrack
        audio_play_sound(music_preset_2_3, 10, true);
        for(i = 0; i < 7; i++){
            customX = PIXELDIFF_X;
            for(j = 0; j < 15; j++){
                instance_create(customX,customY,obj_p3t1);
                customX += TILEPX;
            }
            customY += TILEPX;
        }
        break;
    //case 3
    case 3:
        //soundtrack
        audio_play_sound(music_preset_1_4, 10, true);
        for(i = 0; i < 7; i++){
            customX = PIXELDIFF_X;
            for(j = 0; j < 15; j++){
                randomNumber = irandom(1); //restituisce un numero int tra 0 e 1
                if (randomNumber == 0){
                    instance_create(customX,customY,obj_p4t1);
                }
                if (randomNumber == 1){
                    instance_create(customX,customY,obj_p4t2);
                }
                customX += TILEPX;
            }
            customY += TILEPX;
        }
        break;
    //case 4
    case 4:
        //soundtrack
        audio_play_sound(music_preset_5, 10, true);
        for(i = 0; i < 7; i++){
            customX = PIXELDIFF_X;
            for(j = 0; j < 15; j++){
                randomNumber = irandom_between(0,2); //restituisce un numero int tra 0 e 2
                if (randomNumber == 0){
                    instance_create(customX,customY,obj_p5t1);
                }
                if (randomNumber == 1){
                    instance_create(customX,customY,obj_p5t2);
                }
                if (randomNumber == 2){
                    instance_create(customX,customY,obj_p5t3);
                }
                customX += TILEPX;
            }
            customY += TILEPX;
        }
        break;
    //case 5
    case 5:
        //soundtrack
        audio_play_sound(music_preset_6, 10, true);
        for(i = 0; i < 7; i++){
            customX = PIXELDIFF_X;
            for(j = 0; j < 15; j++){
                instance_create(customX,customY,obj_p6t1);
                customX += TILEPX;
            }
            customY += TILEPX;
        }
        break;
        
}
