/*-
 * Copyright (c) 2007, Derek Konigsberg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution. 
 * 3. Neither the name of the project nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.logicprobe.LogicMail.conf;

import java.io.DataInput;

import org.logicprobe.LogicMail.util.SerializableHashtable;

/**
 * Configuration object to store settings for
 * IMAP mail accounts.
 */
public class ImapConfig extends AccountConfig {
    private String folderPrefix;
    private int maxMessageSize;
    private int maxFolderDepth;
    private boolean onlySubscribedFolders;

    /**
     * Instantiates a new connection configuration with defaults.
     */
    public ImapConfig() {
        super();
    }

    /**
     * Instantiates a new connection configuration from serialized data.
     * 
     * @param input The input stream to deserialize from
     */
    public ImapConfig(DataInput input) {
        super(input);
    }

    /* (non-Javadoc)
     * @see org.logicprobe.LogicMail.conf.AccountConfig#setDefaults()
     */
    protected void setDefaults() {
        super.setDefaults();
        setServerPort(143);
        this.maxMessageSize = 32768;
        this.maxFolderDepth = 4;
        this.folderPrefix = "";
        this.onlySubscribedFolders = true;
    }

    /* (non-Javadoc)
     * @see org.logicprobe.LogicMail.conf.AccountConfig#toString()
     */
    public String toString() {
        String text = getAcctName().concat(" (IMAP)");
        return text;
    }

    /**
     * Gets the folder prefix.
     * 
     * @return The folder prefix
     */
    public String getFolderPrefix() {
        return folderPrefix;
    }

    /**
     * Sets the folder prefix.
     * 
     * @param folderPrefix The new folder prefix
     */
    public void setFolderPrefix(String folderPrefix) {
        if(!this.folderPrefix.equals(folderPrefix)) {
            this.folderPrefix = folderPrefix;
            changeType |= CHANGE_TYPE_ADVANCED;
        }
    }

    /**
     * Gets the maximum message size.
     * 
     * @return The maximum message size
     */
    public int getMaxMessageSize() {
        return this.maxMessageSize;
    }

    /**
     * Sets the maximum message size.
     * 
     * @param maxMessageSize The new maximum message size
     */
    public void setMaxMessageSize(int maxMessageSize) {
        if(this.maxMessageSize != maxMessageSize) {
            this.maxMessageSize = maxMessageSize;
            changeType |= CHANGE_TYPE_LIMITS;
        }
    }

    /**
     * Gets the maximum folder depth.
     * 
     * @return The maximum folder depth
     */
    public int getMaxFolderDepth() {
        return maxFolderDepth;
    }

    /**
     * Sets the maximum folder depth.
     * 
     * @param maxFolderDepth The new maximum folder depth
     */
    public void setMaxFolderDepth(int maxFolderDepth) {
        if(this.maxFolderDepth != maxFolderDepth) {
            this.maxFolderDepth = maxFolderDepth;
            changeType |= CHANGE_TYPE_LIMITS;
        }
    }

    /**
     * Gets whether to only load subscribed folders.
     * 
     * @return whether to load only subscribed folders
     */
    public boolean getOnlySubscribedFolders() {
        return onlySubscribedFolders;
    }

    /**
     * Sets whether to only load subscribed folders.
     * 
     * @param onlySubscribedFolders true, if only subscribed folders should be loaded
     */
    public void setOnlySubscribedFolders(boolean onlySubscribedFolders) {
        if(this.onlySubscribedFolders != onlySubscribedFolders) {
            this.onlySubscribedFolders = onlySubscribedFolders;
            changeType |= CHANGE_TYPE_ADVANCED;
        }
    }

    /* (non-Javadoc)
     * @see org.logicprobe.LogicMail.conf.AccountConfig#writeConfigItems(org.logicprobe.LogicMail.util.SerializableHashtable)
     */
    public void writeConfigItems(SerializableHashtable table) {
        super.writeConfigItems(table);
        if(folderPrefix != null) {
            table.put("account_imap_folderPrefix", folderPrefix);
        }
        else {
            table.put("account_imap_folderPrefix", "");
        }
        table.put("account_imap_maxMessageSize", new Integer(maxMessageSize));
        table.put("account_imap_maxFolderDepth", new Integer(maxFolderDepth));
        table.put("account_imap_onlySubscribedFolders", new Boolean(onlySubscribedFolders));
    }

    /* (non-Javadoc)
     * @see org.logicprobe.LogicMail.conf.AccountConfig#readConfigItems(org.logicprobe.LogicMail.util.SerializableHashtable)
     */
    public void readConfigItems(SerializableHashtable table) {
        super.readConfigItems(table);
        Object value;

        value = table.get("account_imap_folderPrefix");
        if(value instanceof String) {
            folderPrefix = (String)value;
        }
        value = table.get("account_imap_maxMessageSize");
        if (value instanceof Integer) {
            maxMessageSize = ((Integer) value).intValue();
        }

        value = table.get("account_imap_maxFolderDepth");
        if (value instanceof Integer) {
            maxFolderDepth = ((Integer) value).intValue();
        }
        value = table.get("account_imap_onlySubscribedFolders");
        if(value instanceof Boolean) {
            onlySubscribedFolders = ((Boolean)value).booleanValue();
        }
    }
}
