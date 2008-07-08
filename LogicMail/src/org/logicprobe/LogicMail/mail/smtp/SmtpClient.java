/*-
 * Copyright (c) 2006, Derek Konigsberg
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

package org.logicprobe.LogicMail.mail.smtp;

import java.io.IOException;
import java.util.Calendar;
import org.logicprobe.LogicMail.AppInfo;
import org.logicprobe.LogicMail.conf.ConnectionConfig;
import org.logicprobe.LogicMail.conf.GlobalConfig;
import org.logicprobe.LogicMail.conf.MailSettings;
import org.logicprobe.LogicMail.conf.MailSettingsEvent;
import org.logicprobe.LogicMail.conf.MailSettingsListener;
import org.logicprobe.LogicMail.conf.OutgoingConfig;
import org.logicprobe.LogicMail.mail.MailException;
import org.logicprobe.LogicMail.mail.OutgoingMailClient;
import org.logicprobe.LogicMail.message.Message;
import org.logicprobe.LogicMail.message.MessageEnvelope;
import org.logicprobe.LogicMail.message.MessageMimeConverter;
import org.logicprobe.LogicMail.util.Connection;
import org.logicprobe.LogicMail.util.StringParser;

/**
 * Implements an SMTP client
 */
public class SmtpClient implements OutgoingMailClient {
    private OutgoingConfig outgoingConfig;
    private Connection connection;
    private SmtpProtocol smtpProtocol;
    private boolean isFresh;
    private boolean openStarted;
    private String username;
    private String password;
    private boolean configChanged;
    private static String strCRLF = "\r\n";
    
    /** Creates a new instance of SmtpClient */
    public SmtpClient(GlobalConfig globalConfig, OutgoingConfig outgoingConfig) {
        this.outgoingConfig = outgoingConfig;
        connection = new Connection(
            outgoingConfig.getServerName(),
            outgoingConfig.getServerPort(),
            outgoingConfig.getServerSSL(),
            outgoingConfig.getDeviceSide());
        smtpProtocol = new SmtpProtocol(connection);

        if(outgoingConfig.getUseAuth() > 0) {
            username = outgoingConfig.getServerUser();
            password = outgoingConfig.getServerPass();
        }
        else {
            username = null;
            password = null;
        }
        openStarted = false;
        configChanged = false;
        MailSettings.getInstance().addMailSettingsListener(mailSettingsListener);
    }
    private MailSettingsListener mailSettingsListener = new MailSettingsListener() {
		public void mailSettingsSaved(MailSettingsEvent e) {
			mailSettings_MailSettingsSaved(e);
		}
    };
    
    private void mailSettings_MailSettingsSaved(MailSettingsEvent e) {
		if(MailSettings.getInstance().containsOutgoingConfig(outgoingConfig)) {
			// Refresh authentication information from the configuration
	        username = outgoingConfig.getServerUser();
	        password = outgoingConfig.getServerPass();
	        
	        if(!isConnected()) {
	        	// Rebuild the connection to include new settings
	            connection = new Connection(
	            		outgoingConfig.getServerName(),
	            		outgoingConfig.getServerPort(),
	            		outgoingConfig.getServerSSL(),
	            		outgoingConfig.getDeviceSide());
	            smtpProtocol = new SmtpProtocol(connection);
	        }
	        else {
		        // Set a flag to make sure we rebuild the Connection object
		        // the next time we close the connection.
		        configChanged = true;
	        }
		}
		else {
			// We have been deleted, so unregister to make sure we
			// no longer affect the system and can be garbage collected.
			MailSettings.getInstance().removeMailSettingsListener(mailSettingsListener);
		}
    }

    public boolean open() throws IOException, MailException {
        if(!openStarted) {
            connection.open();

            // Eat the initial server response
            connection.receive();
            String hostname = System.getProperty("microedition.hostname");
            if(hostname == null) {
                hostname = "localhost";
            }

            smtpProtocol.executeExtendedHello(hostname);
            openStarted = true;
        }
        if(outgoingConfig.getUseAuth() > 0) {
            boolean result = smtpProtocol.executeAuth(outgoingConfig.getUseAuth(), username, password);
            if(!result) {
                return false;
            }
        }
        isFresh = true;
        openStarted = false;
        return true;
    }

    public void close() throws IOException, MailException {
        try {
           smtpProtocol.executeQuit();
        } catch (Exception exp) { }
        connection.close();
        
        if(configChanged) {
        	// Rebuild the connection to include new settings
            connection = new Connection(
            		outgoingConfig.getServerName(),
            		outgoingConfig.getServerPort(),
            		outgoingConfig.getServerSSL(),
            		outgoingConfig.getDeviceSide());
            smtpProtocol = new SmtpProtocol(connection);
            configChanged = false;
        }
    }

    public ConnectionConfig getConnectionConfig() {
		return outgoingConfig;
	}

    public boolean isConnected() {
        return connection.isConnected();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String sendMessage(Message message) throws IOException, MailException {
        if(!isFresh) {
            smtpProtocol.executeReset();
        }
        isFresh = false;
        
        // serialize the message
        MessageMimeConverter messageMime = new MessageMimeConverter();
        message.getBody().accept(messageMime);
        String mimeStr = messageMime.toMimeString();
        
        MessageEnvelope env = message.getEnvelope();
        StringBuffer buffer = new StringBuffer();

        // Create the message headers
        buffer.append("From: " + makeCsvString(env.from) + strCRLF);
        buffer.append("To: " + makeCsvString(env.to) + strCRLF);
        if(env.cc != null && env.cc.length > 0) {
            buffer.append("Cc: " + makeCsvString(env.cc) + strCRLF);
        }
        if(env.replyTo != null && env.replyTo.length > 0) {
            buffer.append("Reply-To: " + makeCsvString(env.replyTo) + strCRLF);
        }
        buffer.append("Date: " + StringParser.createDateString(Calendar.getInstance().getTime()) + strCRLF);
        buffer.append("User-Agent: "+AppInfo.getName()+"/"+AppInfo.getVersion() + strCRLF);
        buffer.append("Subject: " + env.subject + strCRLF);
        
        if(env.inReplyTo != null) {
            buffer.append("In-Reply-To: " + env.inReplyTo + strCRLF);
        }
        
        // Add the body
        buffer.append(mimeStr);

        // Send the message
        if(!smtpProtocol.executeMail(stripEmail(env.from[0]))) {
            throw new MailException("Error with sender");
        }
        for(int i=0; i<env.to.length; i++) {
            if(!smtpProtocol.executeRecipient(stripEmail(env.to[i]))) {
                throw new MailException("Error with recipient");
            }
        }
        if(env.cc != null) {
            for(int i=0; i<env.cc.length; i++) {
                if(!smtpProtocol.executeRecipient(stripEmail(env.cc[i]))) {
                    throw new MailException("Error with recipient");
                }
            }
        }
        if(env.bcc != null) {
            for(int i=0; i<env.bcc.length; i++) {
                if(!smtpProtocol.executeRecipient(stripEmail(env.bcc[i]))) {
                    throw new MailException("Error with recipient");
                }
            }
        }
        
        String rawMessage = buffer.toString();
        if(!smtpProtocol.executeData(rawMessage)) {
            throw new MailException("Error sending message");
        }
        
        return rawMessage;
    }
    
    private static String makeCsvString(String[] input) {
        if(input == null || input.length == 0)
            return "";
        else if(input.length == 1)
            return input[0];
        else {
            StringBuffer buffer = new StringBuffer();
            for(int i=0; i<input.length-1; i++) {
                buffer.append(input[i]);
                buffer.append(", ");
            }
            buffer.append(input[input.length-1]);
            return buffer.toString();
        }
    }
    
    private static String stripEmail(String input) {
        int p = input.indexOf('<');
        int q = input.indexOf('>');
        if(p == -1 || q == -1 || q <= p)
            return input;
        else
            return input.substring(p+1, q);
    }
}
