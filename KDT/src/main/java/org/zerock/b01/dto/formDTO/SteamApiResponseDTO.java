package org.zerock.b01.dto.formDTO;

import com.nimbusds.oauth2.sdk.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteamApiResponseDTO {
    private Response response;

    @Data
    public static class Response {
        private List<Player> players;
    }

    @Data
    public static class Player {
        private String steamid;
        private String personaname;
        private String avatarfull;
        private String profileurl;
    }
}
