package io.polyrepo.constant;

import org.springframework.stereotype.Component;

@Component
public class ApplicationConstants {

    private static ApplicationConstants constants = null;
    private String token;


    private ApplicationConstants() {}

    public static ApplicationConstants getInstance(){
        if(constants==null){
            constants = new ApplicationConstants();
        }
        return constants;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
