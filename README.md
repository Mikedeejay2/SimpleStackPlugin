![Project Icon](https://user-images.githubusercontent.com/58639173/90967216-ffc61900-e4a9-11ea-88bc-169dd28c8735.png)

# Simple Stack Plugin
![GitHub stars](https://img.shields.io/github/stars/Mikedeejay2/SimpleStackPlugin)
![GitHub issues](https://img.shields.io/github/issues/Mikedeejay2/SimpleStackPlugin)
![GitHub tag](https://img.shields.io/github/tag/Mikedeejay2/SimpleStackPlugin)
![GitHub license](https://img.shields.io/github/license/Mikedeejay2/SimpleStackPlugin)

Simple Stack is a plugin designed to make all items stack to 64. No permissions are required to make Simple Stack work, 
just install the plugin to your server and run!

⚠️ Note: Simple Stack does not currently work in the creative inventory. This is because the creative inventory works 
differently from the normal inventories (Survival inventory, chest inventory, etc). To stack normally unstackable items 
in creative mode, open any other inventory besides the creative inventory like a chest or furnace.

### How to Use

After installing this plugin on a server items that didn't previously stack to 64 can now be stacked to 64
simply by moving items around the inventory and combining them into a stack.

![Item Movement Example](https://user-images.githubusercontent.com/58639173/99920830-423f0200-2cf4-11eb-9cf3-103bed0e8217.gif)


### Commands

`/simplestack help` - An in game help menu for admin commands

`/simplestack reload` - Reload the blacklist/whitelist from config

`/simplestack reset` - Reset the config to it's default values and remove all custom values

`/simplestack setamount` <amount> - Set the amount of an item in the executing player's hand
  
`/simplestack config` - Open the in-game configuration GUI

### Permissions

`simplestack.use` - Use Simple Stack to combine items in inventories

`simplestack.help` - Allow access to view the admin help menu (/simplestack help)

`simplestack.reload` - Allow access to reload the config (/simplestack reload)

`simplestack.reset` - Allow access to reset the simplestack config to default values (/simplestack reset)

`simplestack.setamount` - Allow a player to set the item currently held in their hand to any amount (/simplestack setamount)

`simplestack.config` - Allow a player to open and modify the configuration from a GUI (/simplestack config)

### Config

The config for Simple Stack can be modified through it's config file or through `/simplestack config` which opens a
GUI that can modify all values of the config.

![gif 2 15fps](https://user-images.githubusercontent.com/58639173/99920848-626ec100-2cf4-11eb-991b-119070cc7e03.gif)

List Mode: The mode that the item types list will operate in
  * `Blacklist` = Items added to the list will not be stackable
  * `Whitelist` = Items added to the list will be stackable, but unstackable items will stay unstackable if they're not in the list

Item Types: Item types that will or will not be stacked to 64 based on the list mode above

Item Amounts: Set the amount of items that an item of that type can hold.
Note: Items in item amounts must also be in the items list if in whitelist mode

Language: The default language for in game text. If the specified language is not supported English will be used.
Supported Languages:
  * `en_us` - English
  * `zh_cn` - 简体中文 (Simplified Chinese)
  * `ko_kr` - 한국어 (Korean)
  * `es_ar` - Español (Argentinian Spanish)
  * `es_cl` - Español (Chilean Spanish)
  * `es_mx` - Español (Mexican Spanish)
  * `es_uy` - Español (Uruguayan Spanish)
  * `es_ve` - Español (Venezuelan Spanish)
Note: Language is also based off of per-player, the language specified in this config only applies
to the console or unknown languages.

Default Max Amount: Set the default max amount for ALL items in Minecraft
This setting could be dangerous, do proper testing before changing this value.

Hopper Movement Checks: Hoppers will attempt to stack unstackable items together.
Setting this to false will stop hopper checks which could increase performance but will
Stop stacking of unstackables when moving through hoppers.

Ground Stacking Checks: Simple Stack will check whether unstackable items on the ground
will stack together or not.
Setting this to false will stop unstackables stacking when on the ground and could
increase performance at the cost of unstackables not stacking when on the ground.

### Translating

Translating this plugin into other languages is managed on [OneSky](https://osu0azw.oneskyapp.com/). 

### Downloads

This plugin can be downloaded through any of the below links:

[GitHub Releases Page](https://github.com/Mikedeejay2/SimpleStackPlugin/releases)

[Spigot](https://www.spigotmc.org/resources/simple-stack.83044/)

[Bukkit](https://dev.bukkit.org/projects/simple-stack)

If you enjoy this plugin and want to support me financially, [Donation Link](https://www.paypal.com/paypalme/mikedeejay2)
