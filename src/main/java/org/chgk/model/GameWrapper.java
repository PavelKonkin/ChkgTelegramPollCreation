package org.chgk.model;

import lombok.Data;

import java.util.List;

@Data
public class GameWrapper {
    private List<GameDto> games;
}
