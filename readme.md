# Whos That Pixelmon
A ChatGame plugin for Pixelmon to mimic the 'Whos that Pixelmon' intervals from the show. 

![Plugin in action](https://media.giphy.com/media/pvBfl5cYHHoWXPGlIS/giphy.gif?cid=790b7611881fa0b3afbd9be7584f4928c128385627e40146&rid=giphy.gif&ct=g/giphy.gif)

### Installation
Check out the project on [Ore](https://ore.spongepowered.org/Bisxsh/WhosThatPixelmon)  
Get the latest jar file (Version 1.1.0) [here](https://ore.spongepowered.org/Bisxsh/WhosThatPixelmon/versions/1.1.0)  
  
Drag the jar into your server's mods folder.  
Make sure you have the dependencies [*RealMap*](https://ore.spongepowered.org/Eric12324/RealMap) and [*Pixelmon Reforged*](https://reforged.gg/) installed.

### Features
- Picks a random sprite from all available sprites. 
- Configurable item rewards and time intervals. 
- Prevents players from keeping maps created.  
  
### Commands  
`/whosthatpixelmon, /wtp start`  
Forces the chat game to start and resets the time elapsed towards the next instance  
#### Permissions  
`whosthatpixelmon.command.start`  
  
### Config  
The config file for this plugin is located in `config/whosthatpixelmon/whosthatpixelmon.conf` within your server's folder.  
**Default Config File:**  
```
#List of potential item rewards, a random item will be selected from the list if itemRewardsEnabled=true
item=[
    {
        #Quantity of item being given
        amount=2 
        #The ItemID of the item being rewarded, enable tooltips ingame to see itemIDs when hovering over items
        name="pixelmon:rare_candy"
    },
    {
        amount=2
        name="pixelmon:ultra_ball"
    }
]

#Enables item rewards being given
itemsEnabled=true

#List of commands to be run by the server when a player wins the chat game (all commands will be exectued)
#Type <player> in the command string to insert the winning player in the command.
#The command you enter here must be a valid command that is executable by the server.
commands=[
        "give <player> minecraft:diamond 1" #example command, will give the winning player a diamond
        "give <player> minecraft:emerald 1"
]

#Enable/disable commands list (see above) being executed when a player wins
commandsEnabled=false

#Enable/disable answer being revealed if the sprite is not guessed correctly in time
revealAnswer=false

#Time interval between successive instances of the chatgame. A random time between these values will be used when counting
#towards the next event launch. Make both values the same if you want the event to launch at the same time interval.
time {
    maximumTimeInterval=35
    minimumTimeInterval=30
}

#Time interval in seconds for players to enter guesses for the chat game
guessingTime=30
```  
#### Changing item rewards:  
To change the possible item rewards, simply copy and paste the following block of text inbetween the square brackets, making sure there is a comma after the final curly brace if another block is to follow. The amount of blocks represent the amount of potential item rewards when the chat game is answered correctly.  

```
{  
        amount=2  
        name="<modid>:<itemname>"  
    },
```  
Each block has a **name**, and an **amount**. To get an item's ID, press **F3 + H** in minecraft, which will enable tooltips and show each item's ID when hovered over in your inventory. The item amount is an integer that will define the quantity of the item give, please ensure that the item amount **is not higher than the max stack size** of the item you are rewarding.

If you only want to include one possible item reward, ensure that there is only one block in the square brackets, and that a comma does not follow the curly brace.  
  
Item rewards can be disabled by setting `itemsEnabled=false`.  
  
#### Changing commands:  
A list of commands can be added which will be run by the server when a winner is announced. These commands must be valid and executable by the server. If the command is derived from a plugin, ensure that the plugin is installed correctly on the server. All commands in the list will be executed. By default this feature is disabled, but can be enabled by setting `commandsEnabled=true`.  
  
#### Changing the answer being revealed:  
By default the correct answer will not be displayed if no players guess the sprite correctly within the given time interval. This can be changed by setting `revealAnswer=true`, which will display the answer in chat even when it is not guessed correctly in time.  
  
#### Changing guessing interval times:  
The time interval allocated for player guesses can be modified by changing the value of `guessingTime`. This value will be read in seconds, and will start counting down when the map is given to the player and not when the event broadcast is sent in chat.  
  
#### Changing interval times:  
The time node in the config file defines the time interval for the chat game to take place.  
**mimimumTimeInterval** and **maximumTimeInterval** is the earliest and latest that a new instance of the chat game will launch in minutes following an instance that has passed. If the server has started, these values will define the earliest and latest the first instance of the chat game will launch.  

The time that the chat game launches will be randomised between these defined intervals. If you would like a static time between launches, simply put down the same value for both intervals, which will cause the game to launch at exactly the time specified.  
   
#### Contact me  
Please report any bugs with the plugin on the [Issue Tracker](https://github.com/Bisxsh/WhosThatPixelmon/issues).  
Any further queries can be raised by contacting me on [Discord](https://discordapp.com/channels/@me/Bisxsh#0408/).  
  
**Not for Commercial Use**
