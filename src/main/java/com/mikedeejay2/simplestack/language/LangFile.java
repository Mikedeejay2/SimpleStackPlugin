package com.mikedeejay2.simplestack.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikedeejay2.simplestack.Simplestack;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LangFile
{
    private static final Simplestack plugin = Simplestack.getInstance();

    // Locale (Language that this lang file is)
    private String locale;
    // The json of this lang file
    private JsonObject json;
    // The json parser that the json was created with
    private JsonParser parser = new JsonParser();

    /**
     * Construct a lang file
     * A lang file will not load itself! It must be loaded after declaration.
     *
     * @param locale The locale (From a player or from the default value) that this lang file will be
     */
    public LangFile(String locale)
    {
        this.locale = locale;
    }

    /**
     * Attempt to load this lang file.
     *
     * @return If load was successful and file was loaded or not.
     */
    public boolean load()
    {
        InputStream input = plugin.getResource(locale + ".json");
        if(input == null) return false;
        try
        {
            json = (JsonObject)parser.parse(new InputStreamReader(input, StandardCharsets.UTF_8));
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get a JsonElement from the json file
     *
     * @param element Element to get
     * @return The JsonElement. If non-existent, will return null.
     */
    public JsonElement getElement(String element)
    {
        return json.get(element);
    }

    /**
     * Gets a string from the json file based off of the path.
     *
     * @param path Path that contains wanted String
     * @return The wanted string. If non-existent, will return null.
     */
    public String getString(String path)
    {
        JsonElement element = getElement(path);
        if(element == null) return null;
        return element.getAsString();
    }
}
