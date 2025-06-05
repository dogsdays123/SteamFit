package org.zerock.b01.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.zerock.b01.dto.UserByDTO;
import org.zerock.b01.dto.formDTO.SteamApiResponseDTO;

@Service
public class SteamService {

    @Value("${steam.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateSteamLoginUrl() {
        return "https://steamcommunity.com/openid/login"
                + "?openid.ns=http://specs.openid.net/auth/2.0"
                + "&openid.mode=checkid_setup"
                + "&openid.return_to=http://yourdomain.com/homePage/login/steam/callback"
                + "&openid.realm=http://yourdomain.com"
                + "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select"
                + "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select";
    }

    public UserByDTO getSteamUserProfile(String steamId) {
        String url = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/" +
                "?key=" + apiKey + "&steamids=" + steamId;

        String json = restTemplate.getForObject(url, String.class);

        try {
            SteamApiResponseDTO apiResponse = objectMapper.readValue(json, SteamApiResponseDTO.class);

            if (apiResponse.getResponse() != null && !apiResponse.getResponse().getPlayers().isEmpty()) {
                SteamApiResponseDTO.Player player = apiResponse.getResponse().getPlayers().get(0);

                // UserByDTO에 매핑 (regDate, modDate 등은 현재 시간으로 세팅)
                UserByDTO dto = UserByDTO.builder()
                        .uId(player.getSteamid())
                        .uName(player.getPersonaname())
                        .url(player.getAvatarfull())
                        .url2(player.getProfileurl())
                        .regDate(java.time.LocalDateTime.now())
                        .modDate(java.time.LocalDateTime.now())
                        .build();

                return dto;
            } else {
                throw new RuntimeException("Steam API 응답에 유저 정보가 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Steam API JSON 파싱 실패", e);
        }
    }

    public String extractSteamIdFromClaimedId(String claimedId) {
        // "https://steamcommunity.com/openid/id/76561198006409530"
        if (claimedId != null && claimedId.contains("/id/")) {
            return claimedId.substring(claimedId.lastIndexOf("/") + 1);
        }
        throw new IllegalArgumentException("Invalid claimed_id: " + claimedId);
    }

    public String getPlayerSummaries(String steamId) {
        String url = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?" +
                "key=" + apiKey +
                "&steamids=" + steamId;

        return restTemplate.getForObject(url, String.class);
    }

    public String getOwnedGames(String steamId) {
        String url = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?" +
                "key=" + apiKey +
                "&steamid=" + steamId +
                "&include_appinfo=true" +
                "&include_played_free_games=true";

        return restTemplate.getForObject(url, String.class);
    }
}
