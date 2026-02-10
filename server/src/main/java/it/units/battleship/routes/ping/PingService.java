package it.units.battleship.routes.ping;

import it.units.battleship.BuildConstants;
import it.units.battleship.data.PingResponseData;

/**
 * Service class for handling ping requests.
 */
public class PingService {

    /**
     * Generates a ping response containing the server time and version.
     *
     * @return PingResponseData object with server time and version
     */
    public PingResponseData getPingResponse() {
        return PingResponseData.builder()
                .serverTime(System.currentTimeMillis())
                .serverVersion(BuildConstants.VERSION)
                .build();
    }
}
