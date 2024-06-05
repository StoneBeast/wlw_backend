package at.along.com.utils;

import java.util.UUID;

public class Authcodeutil {

    public static String getUUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[1];
    }

}
