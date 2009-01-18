/*-
 * Copyright (c) 2009, Derek Konigsberg
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
package org.logicprobe.LogicMail.ui;

import java.util.Calendar;

import org.logicprobe.LogicMail.message.MessageEnvelope;
import org.logicprobe.LogicMail.model.MailboxNode;
import org.logicprobe.LogicMail.model.MessageNode;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

/**
 * Field to represent mailbox items on the mailbox screen.
 */
public class MailboxMessageField extends Field {
	private MailboxNode mailboxNode;
	private MessageNode messageNode;
	private int lineHeight;
    private int maxWidth;
	
	/**
	 * Instantiates a new mailbox message field.
	 * 
	 * @param mailboxNode The mailbox node containing the message
	 * @param messageNode The message node representing the message to display
	 */
	public MailboxMessageField(MailboxNode mailboxNode, MessageNode messageNode) {
		this.mailboxNode = mailboxNode;
		this.messageNode = messageNode;
	}

	/**
	 * Instantiates a new mailbox message field.
	 * 
	 * @param mailboxNode The mailbox node containing the message
	 * @param messageNode The message node representing the message to display
	 * @param style Combination of field style bits to specify display attributes
	 */
	public MailboxMessageField(MailboxNode mailboxNode, MessageNode messageNode, long style) {
		super(style);
		this.mailboxNode = mailboxNode;
		this.messageNode = messageNode;
	}
	
	/**
	 * Gets the displayed message node.
	 * 
	 * @return The message node
	 */
	public MessageNode getMessageNode() {
		return this.messageNode;
	}
	
	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Field#layout(int, int)
	 */
	protected void layout(int width, int height) {
        maxWidth = width;
        //dateWidth = Font.getDefault().getAdvance("00/00/0000");
        //senderWidth = maxWidth - dateWidth - 20;
        lineHeight = getPreferredHeight() / 2;
		setExtent(width, getPreferredHeight());
	}

	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Field#getPreferredHeight()
	 */
	public int getPreferredHeight() {
		return (Font.getDefault().getHeight() * 2);
	};
	
	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Field#invalidate()
	 */
	public void invalidate() {
		super.invalidate();
	}
	
	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Field#onUnfocus()
	 */
	protected void onUnfocus() {
		super.invalidate();
		super.onUnfocus();
	}
	
	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Field#paint(net.rim.device.api.ui.Graphics)
	 */
	protected void paint(Graphics graphics) {
        MessageEnvelope envelope = messageNode.getFolderMessage().getEnvelope();
        String senderText = createSenderText();
    	String dateString = createDisplayDate();

    	boolean isFocus = this.isFocus();
        int width = this.getWidth();
        int originalColor = graphics.getColor();

        int dateWidth = Font.getDefault().getAdvance(dateString);
        int senderWidth = maxWidth - dateWidth - 20;
        
        // Draw the separator line
        graphics.setColor(Color.DARKGRAY);
        graphics.drawLine(0, lineHeight * 2 - 1, width, lineHeight * 2 - 1);
        graphics.setColor(originalColor);
        
        // Draw the message icon
        Bitmap messageIcon = NodeIcons.getIcon(messageNode);
        graphics.drawBitmap(1, (lineHeight / 2) - (messageIcon.getHeight() / 2), 20, lineHeight*2, messageIcon, 0, 0);

        // Draw the sender text
        if(senderText != null) {
            graphics.drawText(
	    		senderText, 20, 0,
	    		DrawStyle.ELLIPSIS,
	    		senderWidth);
        }
        
        // Draw the subject text
        if(envelope.subject != null) {
            if(!isFocus) { graphics.setColor(0x7B7B7B); }
            graphics.drawText((String)envelope.subject, 20, lineHeight,
                              DrawStyle.ELLIPSIS,
                              maxWidth-20);
            if(!isFocus) { graphics.setColor(originalColor); }
        }
        
        // Draw the message date text
        if(dateString != null) {
            graphics.drawText(
	    		dateString, senderWidth+20, 0,
	    		DrawStyle.ELLIPSIS,
	    		dateWidth);
        }
	}
	
	/**
	 * Creates the sender text to display.
	 * This is normally the sender of the message.  However, if this
	 * message is contained within a Sent folder, it is the first
	 * recipient of the message.
	 * 
	 * @return Sender display text
	 */
	private String createSenderText() {
        MessageEnvelope envelope = messageNode.getFolderMessage().getEnvelope();
        String senderText = null;
        if(mailboxNode.getType() == MailboxNode.TYPE_SENT) {
            if(envelope.to != null && envelope.to.length > 0) {
            	senderText = envelope.to[0];
            }
        }
        else {
            if(envelope.from != null && envelope.from.length > 0) {
            	senderText = envelope.from[0];
            }
        }
        
        // Chop the sender text to only show the full name, if the
        // sender string contains both the name and the E-Mail address.
        if(senderText != null) {
        	int p = senderText.indexOf('<');
        	int q = senderText.indexOf('>');
        	if(p != -1 && q != -1 && p < q && p > 0 && senderText.charAt(p - 1) == ' ') {
        		senderText = senderText.substring(0, p - 1);
        	}
        }
        
        return senderText;
	}
	
	/**
	 * Creates the date string to display.
	 * Determines which version of the date to display based on the relationship
	 * between the current date and the message date.
	 * 
	 * @return The date string to display
	 */
	private String createDisplayDate() {
        MessageEnvelope envelope = messageNode.getFolderMessage().getEnvelope();
        if(envelope.date == null) {
        	return null;
        }
        
        Calendar nowCal = Calendar.getInstance();
        Calendar dispCal = Calendar.getInstance();
        DateFormat dateFormat;

        dispCal.setTime(envelope.date);

        // Determine the date format to display,
        // based on the distance from the current time
        if(nowCal.get(Calendar.YEAR) == dispCal.get(Calendar.YEAR))
            if((nowCal.get(Calendar.MONTH) == dispCal.get(Calendar.MONTH)) &&
            (nowCal.get(Calendar.DAY_OF_MONTH) == dispCal.get(Calendar.DAY_OF_MONTH))) {
            	// Show just the time
                dateFormat = DateFormat.getInstance(DateFormat.TIME_MEDIUM);
            }
            else {
                dateFormat = DateFormat.getInstance(DateFormat.DATE_SHORT);
            }
        else {
            dateFormat = DateFormat.getInstance(DateFormat.DATE_SHORT);
        }
    
        StringBuffer buffer = new StringBuffer();
        dateFormat.format(dispCal, buffer, null);
        return buffer.toString();
	}
}