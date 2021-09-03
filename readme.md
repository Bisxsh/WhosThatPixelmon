# Whos That Pixelmon
A ChatGame plugin for pixelmon to mimic the 'Whos that Pixelmon' intervals from the show. 

![Plugin in action](https://media.giphy.com/media/pvBfl5cYHHoWXPGlIS/giphy.gif?cid=790b7611881fa0b3afbd9be7584f4928c128385627e40146&rid=giphy.gif&ct=g/giphy.gif)

### Installation
Check out the project on [Ore](https://ore.spongepowered.org/Bisxsh/WhosThatPixelmon)  
  
Drag the jar into your server's mods folder.  
Make sure you have the dependencies [*RealMap*](https://ore.spongepowered.org/Eric12324/RealMap) and [*Pixelmon*](https://reforged.gg/) installed.

### Features
- Picks a random sprite from all available sprites. 
- Configurable item rewards and time intervals. 
- Prevents players from keeping maps created.
  
### Config  
**Default Config File:**  
```
item=[  
    {  
        amount=2  
        name="pixelmon:rare_candy"  
    },  
    {  
        amount=2  
        name="pixelmon:ultra_ball"  
    }  
]  
time {  
    maximumTimeInterval=35  
    minimumTimeInterval=30  
}
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

#### Changing interval times:  
The time node in the config file defines the time interval for the chat game to take place.  
**mimimumTimeInterval** and **maximumTimeInterval** is the earliest and latest that a new instance of the chat game will launch in minutes following an instance that has passed. If the server has started, these values will define the earliest and latest the first instance of the chat game will launch.  

The time that the chat game launches will be randomised between these defined intervals. If you would like a static time between launches, simply put down the same value for both intervals, which will cause the game to launch at exactly the time specified.
  
**Not for Commercial Use**