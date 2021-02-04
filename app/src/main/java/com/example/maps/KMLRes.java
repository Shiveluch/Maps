package com.example.maps;

public class KMLRes {
    private String north;
    private String south;
    private String east;
    private String west;

    public String getNortn(){
        return north;
    }
    public String getSouth(){
        return south;
    }

    public String getEast(){
        return east;
    }
    public String getWest(){
        return west;
    }

    public void setNorth(String north){
        this.north=north;
    }
    public void setSouth(String south){
        this.south=south;
    }

    public void setEast (String east)
    {
        this.east=east;

    }

    public void setWest (String west)
    {
        this.west=west;
    }



    public String toString(){
        return  "Coordinates: " + north +", " +east+"\n" + south+", "+west;
    }

}
