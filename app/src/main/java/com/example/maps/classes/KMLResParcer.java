package com.example.maps.classes;

import com.example.maps.KMLRes;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class KMLResParcer {
    private ArrayList<KMLRes> coords;

    public KMLResParcer(){
        coords = new ArrayList<>();
    }

    public ArrayList<KMLRes> getCoords(){
        return  coords;
    }

    public boolean parse(XmlPullParser xpp){
        boolean status = true;
        KMLRes currentCoord = null;
        boolean inEntry = false;
        String textValue = "";

        try{
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){

                String tagName = xpp.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if("LatLonBox".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentCoord = new KMLRes();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(inEntry){
                            if("LatLonBox".equalsIgnoreCase(tagName)){
                                coords.add(currentCoord);
                                inEntry = false;
                            } else if("north".equalsIgnoreCase(tagName)){
                                currentCoord.setNorth(textValue);
                            } else if("south".equalsIgnoreCase(tagName)){
                                currentCoord.setSouth(textValue);
                            }
                        } else if("east".equalsIgnoreCase(tagName)){
                            currentCoord.setEast(textValue);
                        }

               else if("west".equalsIgnoreCase(tagName)){
                    currentCoord.setWest(textValue);
                }

                        break;
                    default:
                }
                eventType = xpp.next();
            }
        }
        catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        return  status;
    }

}
