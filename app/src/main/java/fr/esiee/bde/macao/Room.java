package fr.esiee.bde.macao;

/**
 * Created by Wallerand on 31/05/2017.
 */

public class Room {
    private int epi;
    private String rooms;

    public Room() {
    }

    public Room(String rooms, int epi) {
        this.rooms = rooms;
        this.epi = epi;
    }

    public int getEpi() {
        return epi;
    }

    public void setEpi(int epi) {
        this.epi = epi;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }
}
