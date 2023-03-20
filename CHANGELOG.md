# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.3.8] - 2021-10-02

### Fixed

- Fix duplication bug when spam clicking items around in the inventory (#84)
- Fix grindstone not dropping XP on use
- Fix villagers not leveling up or dropping XP on trade
- Fix shulker boxes unstacking items upon an explosion (#85)

## [1.3.7] - 2021-07-31

### Fixed

- Potentially fix some GUI duplication bugs (Register SimpleStack's listeners after all other listeners) (#70)
- Shulker boxes protected by any form of world protection could still be broken (#75)
- Curse of binding armor pieces could be taken off in survival mode (#78)
- Ground items could spawn above the stack limit (#60)
- Items taken out of the result slot of the grindstone disappeared (#68)

### Changed

- Updated to 1.17.1+ (#82)

## [1.3.6] - 2020-12-19

### Fixed

- Fix bug where shulker boxes unstacked items while in whitelist mode (#63)

## [1.3.5] - 2020-12-12

### Fixed

- Custom GUIs item dragging would create one extra item per slot (#61 & #62)

### Changed

- Improved performance of shulker box detection

## [1.3.4] - 2020-12-08

### Added

- German translations from OneSky
  Option to change the item dragging mode for creative mode (Always create full stacks or normal stacking behavior)

### Fixed

- Some custom GUIs from different plugins weren't stacking items properly

### Changed

- Moved the "Switch List Mode" button to the main configuration menu
- Items in the "Item Amounts" list no longer need to also be in the "Item Types" list (#58)

## [1.3.3] - 2020-11-25

### Fixed

- Shulker boxes could be stored in shulker boxes (#56)

### Changed

- Updated Simplified Chinese translations from Onesky
- Jar file name is now properly capitalized

## [1.3.2] - 2020-12-01

### Fixed

- The config.yml file was not generating example entries on startup
- When consuming soup or stew, the entire stack would be consumed and replaced by a single bowl (#55)

### Changed

- Updated Simplified Chinese translations from Onesky
- Jar file name is now properly capitalized

## [1.3.1] - 2020-23-23

### Added

- Update checker (Checks for updates upon start up / reload of server, uses api.github.com)

### Fixed

- Ground item stacking wasn't stacking unstackable items most of the time (#53)

## [1.3.0] - 2020-11-22

### Added

- Allow items to be properly stacked together when on the ground (#34)
- Allow "unique items" (Items where all data needs to match) to be specified in a list in the config to allow for a specific item to have a custom stack size (#12)
- Allow a new default max item value to be set in config (#35)
- Add a config option to disable custom hopper stacking to improve performance
- Add a real-time configuration GUI in-game using /simplestack config (#15)
- This plugin now records plugin statistics using BStats, link to Simple Stack statistics can be found 
  [here](https://bstats.org/plugin/bukkit/Simple%20Stack/9379)

### Fixed

- Ground items that usually stacked to 64 but were configured to stack to less than 64 could still be stacked together on the ground up to 64 (#32)
- When a player has a full inventory, items could be picked up into armor slots (#33)
- Rare NullPointerException error when dragging items around in inventory (#40)
- Shulkers (sometimes other items) could be stacked in the inventory on top of items that weren't of the same type (#41)
- Fix bug where items stacking on the ground were not checking the config before stacking together (#42)
- Hoppers didn't stack items in their inventory upon initial pickup (#46)
- Rare AssertionError when unstackable items were moving through a hopper into a hopper minecart (#43)
- Too many potions could be moved into a brewing stand through a hopper resulting in item loss (#44)
- Entities without inventories that were picking up unstackable items would throw an error to the console (#47)
- Items that weren't armor could be put in armor slots of a player's inventory (#49)
- Add a few safeguards for potential NullPointerException errors
- Fix a few rare errors when saving the config in-game

### Changed

- This plugin now internally uses Mikedeejay2Lib (Already included in the plugin, there's no need to install it), source 
  code for Mikedeejay2Lib can be found [here](https://github.com/Mikedeejay2/Mikedeejay2Lib)

## [1.2.5] - 2020-09-27

### Added

- Added Spanish (Latin America) Language (Thanks thefrogline!)

## [1.2.4] - 2020-09-24

### Fixed

- When crafting something that uses bucket in its recipe, the player would not get the buckets back.

## [1.2.3] - 2020-09-24

### Fixed

- When buying an unstackable item from a villager, the input items would not be consumed.

## [1.2.2] - 2020-09-18

### Added

- Added Korean Language (Thanks mekayusina!)

## [1.2.1] - 2020-08-31

### Added

- Add Simplified Chinese (Thanks yueyinqiu!)

### Fixed

- Plugin still attempts to check for a Smithing table below version 1.16 which causes an error
- Make json files use UTF-8

## [1.2.0] - 2020-08-30

### Added

- Added lang files, this plugin can now be translated over to different languages
- Simplestack setamount command that can set the amount of an item in a player's hand. Permission: simplestack.setamount
- Simplestack reset command that resets the config.yml for the server. Permission: simplestack.reset
- Custom Item limit added in configuration per item

### Fixed

- Stone cutter was deleting items under some conditions
- Moving items around the hotbar using arrow keys was not working
- Take into account the amount of items currently in an item stack when dragging inventory items
- Any item could be shift clicked into a horse saddle slot and armor slot
- Stone cutters were not taking items properly
- Custom GUIs would not function properly
- Brewing stand inventories allowed invalid items to be placed inside of them
- Items would disappear if multiple items were put into a beacon
- Blaze powder didn't shift click into the correct spot in a bewing stand
- The brewing stand could have multiple bottles in 1 slot which would be deleted upon brewing
- When using a stack of water buckets, the entire stack would be used
- Crafting tables would dupe items if the result item was a custom stack size
- Shift clicking armor onto an armor slot currently occupied with armor would delete the existing armor
- Sometimes items would be shift clicked into the wrong side of the player's hotbar
- Items couldn't be shift clicked into a donkey / mule's inventory
- Cloning items in creative split the items as if the player was in survival mode

## [1.1.4] - 2020-08-26

### Fixed

- Dragging items did not properly distribute the items
- Cloning items (Middle clicking on an item) did not give a full stack
- Items in grindstone would be buggy when shift clicking the output slot
- Invalid items could be shift clicked into loom causing a client crash
- Loom and grindstone unstacking items on close
- Smithing table unstacking items on close
- Items would sometimes ghost on inventory close until the inventory was updated
- Check player permissions and item type for dragging un-stackable items in an inventory
- Invalid items could be shift clicked into the banner slot of the loom
- Shift clicking out of anvils and smithing tables did not have vanilla behavior
- Invalid items could be shift clicked into the cartography table

### Changed

- /simplestack automatically shows the help screen if no arguments are specified
- /simplestack now has 2 aliases: ss, sstack

## [1.1.3] - 2020-08-26

### Fixed

- Items that were shift-clicked into the crafting grid would disappear or be duped.

## [1.1.2] - 2020-08-26

### Fixed

- Hoppers did not restack unstackable items
- Items did not properly shift click into crafting tables
- Fuel for furnaces did not automatically shift click into the correct place
- Shift clicking items into the player's inventory had different behavior from vanilla
- Crafting tables and anvils unstacked items when they were closed with items inside of them
- Items upgraded from the smithing table would disappear if shift clicked
- Items that were stacked in an anvil or smithing table would act odd and sometimes have bugs
- Enchantment table could enchant multiple items at once

## [1.1.1] - 2020-08-24

### Fixed

- Anvils duping items on shift-click fixed.

## [1.1.0] - 2020-08-24

### Added

- Commands
  - /simplestack help - An in game help menu for admin commands
  - /simplestack reload - Reload the blacklist/whitelist from config

- Permissions
  - simplestack.use - Use Simple Stack to combine items in inventories
  - simplestack.help - Allow access to view the admin help menu (/simplestack help)
  - simplestack.reload - Allow access to reload the config (/simplestack reload)

- Config
  - In config.yml there are options for configuring which items should be stacked by this plugin or not. A list mode can 
    be choosed (blacklist / whitelist) which tells the plugin how to handle items in the list.

## [1.0.2] - 2020-08-23

### Fixed

- Items in shulker boxes don't get unstacked when broken
- Some inventories didn't properly shift click items into the right place
- Shulkers that had been placed on the ground don't combine with non-placed shulkers
- Elytras couldn't be shift clicked onto your player

## [1.0.1] - 2020-08-23

### Fixed

- Fix bug where shulker boxes would stack with any other item

## [1.0.0] - 2020-08-22

### Added

- Initial Release

[unreleased]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.3.8...HEAD
[1.3.8]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.3.7...1.3.8
[1.3.7]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.3.6...1.3.7
[1.3.6]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.3.5...1.3.6
[1.3.5]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.3.4...1.3.5
[1.3.4]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.3.3...1.3.4
[1.3.3]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.3.2...1.3.3
[1.3.2]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.3.1...1.3.2
[1.3.1]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.3.0...1.3.1
[1.3.0]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.2.5...1.3.0
[1.2.5]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.2.4...1.2.5
[1.2.4]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.2.3...1.2.4
[1.2.3]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.2.2...1.2.3
[1.2.2]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.2.1...1.2.2
[1.2.1]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.2.0...1.2.1
[1.2.0]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.1.4...1.2.0
[1.1.4]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.1.3...1.1.4
[1.1.3]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.1.2...1.1.3
[1.1.2]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.1.1...1.1.2
[1.1.1]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.1.0...1.1.1
[1.1.0]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.0.2...1.1.0
[1.0.2]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.0.1...1.0.2
[1.0.1]: https://github.com/Mikedeejay2/SimpleStackPlugin/compare/1.0.0...1.0.1
[1.0.0]: https://github.com/Mikedeejay2/SimpleStackPlugin/releases/tag/1.0.0