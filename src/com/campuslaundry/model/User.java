/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.campuslaundry.model;

/**
 *
 * @author jaz
 */
public abstract class User {
private String fullName;
private String email;
private String passwordHash;
private String phoneNumber;

protected User(String fullName, String email, String passwordHash, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
    }

}
