package com.example.arkanoid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Map {

    List<String> mapa = new ArrayList<String>();

    Map(){
       /* mapa.add("000000000000000001000000000000000000000000000000000000000000");
        mapa.add("000000000000000001000000000000000000000000000000000000000000");
        mapa.add("000000000000000001000000000000000000000000000000000000000000");
        mapa.add("000000000000000001000000000000000000000000000000000000000000");*/
        mapa.add("555555555544444444443333333333222222222211111111110000000000"); // čáry Level 1
        mapa.add("000200200000111111000114114110111111111110100001010001001000"); // klasika Level 2
        mapa.add("030000003033302023330300020030333042433304002220400400424040"); // stromy s darky Level 3
        mapa.add("000044000000044440000044444400001111110000151512000011111200"); // dum Level 4
        mapa.add("050052005005043430500500250050323044032324250052423230440323"); // Level 5
        mapa.add("000000202002020202022220202020020202020220202020200000020202"); // Level 6
        mapa.add("000555000000554550000554245500554232455054230324555423032455"); // duha Level 7
        mapa.add("113111155521212155551242144444114114344414441323131212113111"); // človek s autem Level 8
        mapa.add("004000400004440444004444444440444444444004444444000004440000"); // srdce Level 9
        mapa.add("555115551551555545451115544445555515415555551514155515555155"); // letadlo Level 10

    }

    public List<String> getMap(){
        return mapa;
    }
}
