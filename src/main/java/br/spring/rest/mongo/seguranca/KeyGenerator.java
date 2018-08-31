/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.spring.rest.mongo.seguranca;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Renan
 */
public class KeyGenerator {

    public Key generateKey() {
        String keyString = "GyhHEYyLLJUKJeABcqo0YO+QUd6ERarphT2go9S2i94=X2XaaK";
        Key key = new SecretKeySpec(keyString.getBytes(),
                0, keyString.getBytes().length, "HmacSHA256");
        return key;
    }

}
