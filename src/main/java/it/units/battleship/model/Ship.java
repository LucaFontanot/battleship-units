package it.units.battleship.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Ship {

    private List<Coordinate> hitParts = new ArrayList<>();

    private List<Coordinate> coordinates;

}

