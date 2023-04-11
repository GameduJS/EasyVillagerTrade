# Scripting

This part of EasyVillagerTrade allows a player to fully automize his setup of a trading hall. \
You can add simple commands into a `.txt` file, that can be executed ingame.

## Add scripts
A script gets called every time a trade has been found
- Create a `.txt` file in the `.minecraft/config/evt` folder
- Start scripting, all commands are listed
```txt
    WALK <RIGHT/LEFT/FORWARD/BACKWARD> <n>  | Walk n blocks in any direction
    EXECUTE                                 | Start a new trading process
    SETTURN <yaw> <pitch>                   | Rotates the player's camera
    SELECT                                  | Selects closest villager & lectern if available 
    
   (IF <n>                                  | Executes code, when <n> = curent repetition of script )
   (ELSE                                    | Executes, when IF <n> is false )
    
```

## Information
- ***IF-ELSE Statements are currently not available**
- Make sure you take care of the case sensitivity
- Make sure if you use `IF` that you also use an `ELSE` statement (must not contain code)
- Make sure to use indentation in an if-else statement
---
### Example Script
````
WALK RIGHT 3
SELECT
EXECUTE
````
