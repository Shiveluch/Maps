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
    serialized.nickname=val.isNickname();
    serialized.team=val.isTeam();
    serialized.side=val.isSide();
    serialized.radiaton=val.isRadiation();
    serialized.latitude=val.isLat();
    serialized.longitude=val.isLon();

    return serialized;

}

}
