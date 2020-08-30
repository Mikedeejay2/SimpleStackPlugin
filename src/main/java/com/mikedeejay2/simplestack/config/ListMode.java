package com.mikedeejay2.simplestack.config;

/**
 * The mode that the list in config.yml functions.
 */
public enum ListMode
{
    // Only the items in the list will be stackable by default
    WHITELIST,
    // Only the items int the list will be unstackable by default.
    BLACKLIST;
}
