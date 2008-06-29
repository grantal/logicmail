/*-
 * Copyright (c) 2008, Derek Konigsberg
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
package org.logicprobe.LogicMail.model;

import org.logicprobe.LogicMail.message.FolderMessage;
import org.logicprobe.LogicMail.message.Message;
import org.logicprobe.LogicMail.util.EventListenerList;

/**
 * Message node for the mail data model.
 * This node represents a mail message, and does
 * not contain any other nodes as children.
 */
public class MessageNode implements Node {
	private MailboxNode parent;
	private FolderMessage folderMessage;
	private Message message;
	private EventListenerList listenerList = new EventListenerList();
	private String name;

	MessageNode() {
	}
	
	MessageNode(String name) {
		this();
		this.name = name;
	}
	
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Sets the mailbox which is the parent of this node.
	 * @param parent The parent mailbox.
	 */
	void setParent(MailboxNode parent) {
		this.parent = parent;
	}
	
	/**
	 * Gets the mailbox which is the parent of this node.
	 * 
	 * @return The mailbox.
	 */
	public MailboxNode getParent() {
		return this.parent;
	}
	
	/**
	 * Sets the folder message data associated with this node.
	 * 
	 * @param folderMessage Folder message data.
	 */
	void setFolderMessage(FolderMessage folderMessage) {
		this.folderMessage = folderMessage;
	}
	
	/**
	 * Gets the folder message data associated with this node.
	 * This data should always be available when this node is visible.
	 * 
	 * @return Folder message data.
	 */
	public FolderMessage getFolderMessage() {
		return this.folderMessage;
	}
	
	/**
	 * Sets the message data for this node.
	 * 
	 * @param message The message.
	 */
	void setMessage(Message message) {
		this.message = message;
	}
	
	/**
	 * Gets the message data for this node.
	 * The message data will be null unless it has been explicitly loaded.
	 * 
	 * @return The message.
	 */
	public Message getMessage() {
		return this.message;
	}
	
	/**
	 * Sets the name of this message, which should
	 * be set to the subject text.
	 * 
	 * @param name The name.
	 */
	void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name of this message, which should
	 * be set to the subject text.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
     * Adds a <tt>MessageNodeListener</tt> to the message node.
     * 
     * @param l The <tt>MessageNodeListener</tt> to be added.
     */
    public void addMessageNodeListener(MessageNodeListener l) {
        listenerList.add(MessageNodeListener.class, l);
    }

    /**
     * Removes a <tt>MessageNodeListener</tt> from the message node.
     * 
     * @param l The <tt>MessageNodeListener</tt> to be removed.
     */
    public void removeMessageNodeListener(MessageNodeListener l) {
        listenerList.remove(MessageNodeListener.class, l);
    }
    
    /**
     * Returns an array of all <tt>MessageNodeListener</tt>s
     * that have been added to this message node.
     * 
     * @return All the <tt>MessageNodeListener</tt>s that have been added,
     * or an empty array if no listeners have been added.
     */
    public MessageNodeListener[] getMessageNodeListeners() {
        return (MessageNodeListener[])listenerList.getListeners(MessageNodeListener.class);
    }
    
    /**
     * Notifies all registered <tt>MessageNodeListener</tt>s that
     * the message status has changed. 
     */
    protected void fireMessageStatusChanged() {
        Object[] listeners = listenerList.getListeners(MessageNodeListener.class);
        MessageNodeEvent e = null;
        for(int i=0; i<listeners.length; i++) {
            if(e == null) {
                e = new MessageNodeEvent(this);
            }
            ((MessageNodeListener)listeners[i]).messageStatusChanged(e);
        }
    }
}
