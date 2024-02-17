package src.app.Classes.Models;

import src.app.Interfaces.IGetName;

public class Channel implements IGetName {

    private String name;

    private String nameOfTheChannelCreator;

    public Channel(String name, String nameOfTheChannelCreator) {
        this.name = name;
        this.nameOfTheChannelCreator = nameOfTheChannelCreator;
    }

    public String getNameOfTheChannelCreator() {
        return nameOfTheChannelCreator;
    }

    @Override
    public String getName() {
        return name;
    }
}
