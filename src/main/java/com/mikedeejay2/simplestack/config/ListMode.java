package com.mikedeejay2.simplestack.config;

/**
 * <p>
 * The mode that the list in config.yml functions.
 * </p>
 * <p>
 * This enum simply contains two values:
 * <ul>
 *     <li>Blacklist - Don't stack items in the list</li>
 *     <li>Whitelist - Only stack items in the list</li>
 * </ul>
 * </p>
 *
 * @author Mikedeejay2
 */
public enum ListMode {
    // Only the items in the list will be stackable by default
    WHITELIST,
    // Only the items in the list will be unstackable by default.
    BLACKLIST;
}
