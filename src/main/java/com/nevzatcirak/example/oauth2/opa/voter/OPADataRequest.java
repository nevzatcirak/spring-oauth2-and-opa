package com.nevzatcirak.example.oauth2.opa.voter;

import java.util.Map;

/**
 * @author Nevzat ÇIRAK
 * @mail nevzatcirak17@gmail.com
 * Created by nevzatcirak at 17/07/2020
 */
public class OPADataRequest {

    Map<String, Object> input;

    public OPADataRequest(Map<String, Object> input) {
        this.input = input;
    }

    public Map<String, Object> getInput() {
        return this.input;
    }

}
