/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.ApiResponse
 */
package com.etas.vaas.admin;

import java.io.Serializable;

public interface ApiResponse
extends Serializable {
    public Integer getCode();

    public String getMsg();
}

