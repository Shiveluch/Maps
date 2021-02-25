package com.example.maps.classes;
import com.example.maps.MapService;

import com.example.maps.Initializator;
import com.example.maps.classes.PlayerChracteristics;

import java.io.Serializable;
public class DataPack implements Serializable {

    public String nickname, team,side;
public int radiaton;
public double latitude,longitude;
public static DataPack GetDataPack(MapService service)
{

    PlayerChracteristics val=service.playerChracteristics;
    DataPack serialized=new DataPack();
    /*
    serialized.nickname=val.getNickname();
    serialized.team=val.getTeam();
    serialized.side=val.getSide();
    serialized.radiaton=val.getRadiation();

     */
    serialized.latitude=val.getLatitude();
    serialized.longitude=val.getLongitude();

    return serialized;

}

}
