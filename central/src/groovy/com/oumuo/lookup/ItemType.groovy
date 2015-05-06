/*
 *  Copyright (C) 2015 OUMUO LLC - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential information needs prior approval for any use.
 *  Developed by R. Gopal <rgopal@oumuo.com>
 */

package com.oumuo.lookup

/**
 *
 * @author rgopal
 */
public enum ItemType implements org.springframework.context.MessageSourceResolvable {
 
    TEXT, PICTURE, URL, AUDIO, VIDEO
    public Object[] getArguments() { [] as Object[] }
    public String[] getCodes() { [ name() ] }
    public String getDefaultMessage() { name() }    // default is name itself
}
	