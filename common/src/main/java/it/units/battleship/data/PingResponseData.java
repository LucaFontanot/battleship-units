package it.units.battleship.data;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PingResponseData {
    long serverTime;
    String serverVersion;
}
