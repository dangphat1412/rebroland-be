package vn.edu.fpt.rebroland.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
//    private boolean isAdmin;
    public JWTAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

//    public JWTAuthResponse(String accessToken, boolean isAdmin) {
//        this.accessToken = accessToken;
//        this.isAdmin = isAdmin;
//    }
}
