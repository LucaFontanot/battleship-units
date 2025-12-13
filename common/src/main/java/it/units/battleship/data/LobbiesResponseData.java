package it.units.battleship.data;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbiesResponseData {
    private int count;
    private List<? extends LobbyData> results;
}
