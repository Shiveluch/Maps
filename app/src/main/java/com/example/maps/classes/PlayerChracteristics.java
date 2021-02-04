package com.example.maps.classes;

import android.content.Context;

import com.example.maps.MapService;


public final class PlayerChracteristics {
public String nickname,team,side;
public int radiation;
double latitude, longitude;
private MapService service;

public PlayerChracteristics (MapService value)
{
    service=value;
    nickname="";
    team="";
    side="";
    latitude=0.0;
    longitude=0.0;
    radiation=0;
}

    public PlayerChracteristics() {

    }

    public String isNickname() {
    return nickname;
    }
    public String isTeam() {
        return team;
    }
    public String isSide() {
        return side;
    }
    public double isLat() {return latitude;}
    public double isLon(){return longitude;}
    public int isRadiation() {
        return radiation;
    }

    public void setNickname(String nickname) {
        if(this.nickname==nickname)
            return;
        this.nickname = nickname;
        service.NotifyActivity("NICKNAME");

    }

    public void setTeam(String nickname) {
        if(this.team==team)
            return;
        this.team = team;
        service.NotifyActivity("TEAM");

    }

    public void setSide(String nickname) {
        if(this.side==side)
            return;
        this.side = side;
        service.NotifyActivity("SIDE");

    }

}
