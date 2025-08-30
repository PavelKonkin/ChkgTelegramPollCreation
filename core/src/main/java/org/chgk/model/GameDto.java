package org.chgk.model;

import lombok.Data;

@Data
public class GameDto {
    private boolean selected;
    private String date;   // 23 августа (суббота)
    private String time;   // 12:30
    private String place;  // We Cidreria
    private String name;   // Кубок Суперлиги "60 секунд"
}
