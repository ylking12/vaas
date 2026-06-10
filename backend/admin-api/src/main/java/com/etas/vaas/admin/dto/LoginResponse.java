/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.dto.LoginResponse
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  lombok.Generated
 */
package com.etas.vaas.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    @JsonProperty(value="name")
    private String username;
    @JsonProperty(value="token")
    private String token;
    @JsonProperty(value="success")
    private boolean success;

    public LoginResponse() {
    }

    public String getUsername() {
        return this.username;
    }

    public String getToken() {
        return this.token;
    }

    public boolean isSuccess() {
        return this.success;
    }

    @JsonProperty(value="name")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty(value="token")
    public void setToken(String token) {
        this.token = token;
    }

    @JsonProperty(value="success")
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginResponse)) {
            return false;
        }
        LoginResponse other = (LoginResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$token = this.getToken();
        String other$token = other.getToken();
        return !(this$token == null ? other$token != null : !this$token.equals(other$token));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LoginResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $token = this.getToken();
        result = result * 59 + ($token == null ? 43 : $token.hashCode());
        return result;
    }

    public String toString() {
        return "LoginResponse(username=" + this.getUsername() + ", token=" + this.getToken() + ", success=" + this.isSuccess() + ")";
    }
}

