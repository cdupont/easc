package eu.dc4cities.easc.monitoring.iaas;

import java.util.HashMap;
import java.util.Map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class ConstantsOne {

    public ConstantsOne() {
    }

    public static final String ONE_API_URL = "http://sunstone:2633/RPC2";
    public static final String one_user = "user";
    public static final String one_password = "password";
    public static Map<String,Integer> idsone = new HashMap<String, Integer>();
    public static boolean initialize=false;

        public static void init(){
                if (!initialize){
                        idsone.put("cluster00",new Integer(3));
                        idsone.put("cluster01",new Integer(6));
                        idsone.put("cluster02",new Integer(2));
                        idsone.put("cluster03",new Integer(5));
			initialize=true;
                }
        }

}
