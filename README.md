![Project Icon](https://user-images.githubusercontent.com/58639173/90967216-ffc61900-e4a9-11ea-88bc-169dd28c8735.png)

# Simple Stack Plugin
Simple Stack is a plugin designed to make all items stack to 64. No permissions are required to make Simple Stack work, 
just install the plugin to your server and run!

⚠️ Note: Simple Stack does not currently work in the creative inventory. This is because the creative inventory works 
differently from the normal inventories (Survival inventory, chest inventory, etc). To stack normally unstackable items 
in creative mode, open any other inventory besides the creative inventory like a chest or furnace.

### How to Use

After installing this plugin on a server items that didn't previously stack to 64 can now be stacked to 64
simply by moving items around the inventory and combining them into a stack.

![Example](https://user-images.githubusercontent.com/58639173/90967434-479a6f80-e4ad-11ea-8758-9ba1be2494df.gif)

### Commands

`/simplestack help` - An in game help menu for admin commands

`/simplestack reload` - Reload the blacklist/whitelist from config

`/simplestack reset` - Reset the config to it's default values and remove all custom values

`/simplestack setamount` <amount> - Set the amount of an item in the executing player's hand

### Permissions

`simplestack.use` - Use Simple Stack to combine items in inventories

`simplestack.help` - Allow access to view the admin help menu (/simplestack help)

`simplestack.reload` - Allow access to reload the config (/simplestack reload)

`simplestack.reset` - Allow access to reset the simplestack config to default values (/simplestack reset)

`simplestack.setamount` - Allow a player to set the item currently held in their hand to any amount (/simplestack setamount)

### Config

Overview of the config file:
##### `ListMode:` The mode that the list below will operate in
 * `Blacklist` = Items added to the list will not be stackable
 * `Whitelist` = Items added to the list will be stackable, but unstackable items will stay unstackable if they're not in the list

##### `Items:` Items that will or will not be stacked to 64 based on the list mode above

##### `Language:` The default language for in game text. If the specified language is not supported English will be used.
Supported Languages:
 * `en_us` - English
 * `zh_cn` - 简体中文 (Simplified Chinese) created by yueyinqiu
Note: Language is also based off of per-player, the language specified in this config only applies
to the console.

##### `Item Amounts:` Set the amount of items that an item of that type can hold.
Note: Items in item amounts must also be in the items list if in whitelist mode

### Translating

Do you want to help bring this plugin to another language? If so, you can create a copy of the [en_us.json](https://github.com/Mikedeejay2/SimpleStackPlugin/blob/master/src/main/resources/en_us.json) file and fill out the
right side of the file (all of the words after the colons, you must keep the capitalized words in the brackets in 
English though). Then you can either create an issue or a pull request in the Github Repository or alternatively 
upload it to a file sharing service and send it to me via a direct message or in the discussion thread on Spigot.

### Downloads

This plugin can be downloaded through any of the below links:

[GitHub Releases Page](https://github.com/Mikedeejay2/SimpleStackPlugin/releases)

[Spigot](https://www.spigotmc.org/resources/simple-stack.83044/)

[Bukkit](https://dev.bukkit.org/projects/simple-stack)

If you enjoy this plugin and want to throw money at me: [Donation Link](paypal.me/mikedeejay2)
