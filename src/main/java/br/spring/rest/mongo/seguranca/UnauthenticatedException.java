/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.spring.rest.mongo.seguranca;

/**
 *
 * @author Renan
 */
public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException(String s) {
        super(s);
    }

}
