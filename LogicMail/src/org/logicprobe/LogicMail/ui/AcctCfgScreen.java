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

package org.logicprobe.LogicMail.ui;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.text.TextFilter;

import org.logicprobe.LogicMail.LogicMailResource;
import org.logicprobe.LogicMail.conf.AccountConfig;
import org.logicprobe.LogicMail.conf.IdentityConfig;
import org.logicprobe.LogicMail.conf.ImapConfig;
import org.logicprobe.LogicMail.conf.MailSettings;
import org.logicprobe.LogicMail.conf.OutgoingConfig;
import org.logicprobe.LogicMail.conf.PopConfig;
import org.logicprobe.LogicMail.model.AccountNode;
import org.logicprobe.LogicMail.model.MailManager;
import org.logicprobe.LogicMail.model.MailboxNode;

/**
 * Configuration screen
 */
public class AcctCfgScreen extends BaseCfgScreen {
    private BasicEditField acctNameField;
    private BasicEditField serverNameField;
    private CheckboxField serverSslField;
    private BasicEditField serverPortField;
    private BasicEditField serverUserField;
    private PasswordEditField serverPassField;
    private CheckboxField useMdsField;
    private ObjectChoiceField identityField;
    private ObjectChoiceField outgoingServerField;
    private ObjectChoiceField sentFolderChoiceField;
    private BasicEditField folderPrefixField;
    private ButtonField saveButton;
    
    private boolean acctSaved;
    private boolean createDefaultIdentity;
    private AccountConfig acctConfig;
    private IdentityConfig[] identityConfigs;
    private OutgoingConfig[] outgoingConfigs;
    private FieldChangeListener fieldChangeListener;
    
    private class NullOutgoingConfig extends OutgoingConfig {
        public String toString() {
            return "None";
        }
        public long getUniqueId() {
            return -1;
        }
    }

    /**
     * Simple container class to use for ObjectChoiceField items
     * where the desired display text is different from the item
     * object's toString result.
     */
    private class ObjectChoiceItem {
        private String text;
        private Object item;
        public ObjectChoiceItem(String text, Object item) {
            this.text = text;
            this.item = item;
        }
        public Object getItem() { return item; }
        public String toString() { return text; }
    }
    
    public AcctCfgScreen(AccountConfig acctConfig) {
        super("LogicMail - " + resources.getString(LogicMailResource.CONFIG_ACCOUNT_TITLE));
        
        this.acctConfig = acctConfig;
        this.acctSaved = false;
        
        MailSettings mailSettings = MailSettings.getInstance();

        int numIdentities = mailSettings.getNumIdentities();
        if(numIdentities > 0) {
            identityConfigs = new IdentityConfig[numIdentities];
            for(int i=0; i<numIdentities; ++i) {
                identityConfigs[i] = mailSettings.getIdentityConfig(i);
            }
            createDefaultIdentity = false;
        }
        else {
            identityConfigs = new IdentityConfig[1];
            identityConfigs[0] = new IdentityConfig();
            identityConfigs[0].setIdentityName("Default");
            createDefaultIdentity = true;
            this.setDirty(true);
        }
        
        int numOutgoing = mailSettings.getNumOutgoing();
        outgoingConfigs = new OutgoingConfig[numOutgoing+1];
        outgoingConfigs[0] = new NullOutgoingConfig();
        for(int i=0; i<numOutgoing; ++i) {
            outgoingConfigs[i+1] = mailSettings.getOutgoingConfig(i);
        }
        
        fieldChangeListener = new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
                AcctCfgScreen_fieldChanged(field, context);
            }};

        initFields();

        IdentityConfig selectedIdentityConfig = acctConfig.getIdentityConfig();
        if(selectedIdentityConfig != null) {
            identityField.setSelectedIndex(selectedIdentityConfig);
        }

        OutgoingConfig selectedOutgoingConfig = acctConfig.getOutgoingConfig();
        if(selectedOutgoingConfig != null) {
            outgoingServerField.setSelectedIndex(selectedOutgoingConfig);
        }
    }

    private void initFields() {
        acctNameField = new BasicEditField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_NAME) + " ", acctConfig.getAcctName());
        serverNameField = new BasicEditField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_SERVER) + " ", acctConfig.getServerName());
        
        serverSslField = new CheckboxField("SSL", acctConfig.getServerSSL());
        serverSslField.setChangeListener(fieldChangeListener);
        serverPortField = new BasicEditField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_PORT) + " ", Integer.toString(acctConfig.getServerPort()));
        serverPortField.setFilter(TextFilter.get(TextFilter.NUMERIC));
        serverUserField = new BasicEditField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_USERNAME) + " ", acctConfig.getServerUser());
        serverPassField = new PasswordEditField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_PASSWORD) + " ", acctConfig.getServerPass());
        
        useMdsField = new CheckboxField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_USEMDSPROXY), !acctConfig.getDeviceSide());
        identityField = new ObjectChoiceField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_IDENTITY) + " ", identityConfigs, 0);
        outgoingServerField = new ObjectChoiceField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_OUTGOING_SERVER) + " ", outgoingConfigs, 0);
        
        saveButton = new ButtonField(resources.getString(LogicMailResource.MENUITEM_SAVE), Field.FIELD_HCENTER);
        saveButton.setChangeListener(fieldChangeListener);
        
        add(acctNameField);
        add(new SeparatorField());

        if(acctConfig instanceof ImapConfig) {
            add(new RichTextField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_PROTOCOL) + " IMAP", Field.NON_FOCUSABLE));
        }
        else if(acctConfig instanceof PopConfig) {
            add(new RichTextField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_PROTOCOL) + " POP", Field.NON_FOCUSABLE));
        }

        add(new RichTextField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_INCOMING_SERVER), Field.NON_FOCUSABLE));
        add(serverNameField);
        
        add(serverSslField);
        add(serverPortField);
        add(serverUserField);
        add(serverPassField);
        add(useMdsField);
        add(new LabelField());
        add(identityField);
        add(outgoingServerField);
        add(new LabelField());

        if(acctConfig instanceof ImapConfig) {
            ImapConfig imapConfig = (ImapConfig)acctConfig;
            ObjectChoiceItem[] folderChoices = getFolderChoices();

            // Select the default choice based on the currently configured folder
            int defaultFolder = 0;
            String sentFolder = imapConfig.getSentFolder();
            if(sentFolder != null && folderChoices.length > 0) {
                for(int i=1; i<folderChoices.length; i++) {
                    if(sentFolder.equals(((MailboxNode)folderChoices[i].getItem()).getPath())) {
                        defaultFolder = i;
                        break;
                    }
                }
            }
            sentFolderChoiceField = new ObjectChoiceField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_SENT_MESSAGE_FOLDER) + " ", folderChoices, defaultFolder, Field.FIELD_LEFT);
            add(sentFolderChoiceField);
            
            folderPrefixField = new BasicEditField(resources.getString(LogicMailResource.CONFIG_ACCOUNT_FOLDER_PREFIX) + " ", imapConfig.getFolderPrefix());
            add(folderPrefixField);
        }
        
        add(new SeparatorField());
        add(new LabelField(null, Field.NON_FOCUSABLE));
        add(saveButton);
    }
    
    private ObjectChoiceItem[] getFolderChoices() {
        Vector choices = new Vector();
        choices.addElement(new ObjectChoiceItem("<" + resources.getString(LogicMailResource.CONFIG_ACCOUNT_NONE) + ">", null));
        
        // Find the account node matching this configuration, if it exists
        AccountNode[] accounts = MailManager.getInstance().getMailRootNode().getAccounts();
        AccountNode matchingAccount = null;
        for(int i=0; i<accounts.length; i++) {
        	if(accounts[i].getAccountConfig() == this.acctConfig) {
        		matchingAccount = accounts[i];
        		break;
        	}
        }
        
        // We found a matching account node, so build the choice list
        if(matchingAccount != null) {
        	MailboxNode rootMailbox = matchingAccount.getRootMailbox();
        	if(rootMailbox != null) {
        		MailboxNode[] children = rootMailbox.getMailboxes();
        		for(int i=0; i<children.length; i++) {
        			folderChoicesHelper(choices, 0, children[i]);
        		}
        	}
        }
        
        int size = choices.size();
        ObjectChoiceItem[] result = new ObjectChoiceItem[size];
        choices.copyInto(result);
        return result;
    }
    
    private void folderChoicesHelper(Vector choices, int level, MailboxNode currentNode) {
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<level; i++) {
            buf.append("  ");
        }
        buf.append(currentNode.getName());
        choices.addElement(new ObjectChoiceItem(buf.toString(), currentNode));
        MailboxNode[] children = currentNode.getMailboxes();
        for(int i=0; i<children.length; i++) {
            folderChoicesHelper(choices, level+1, children[i]);
        }
    }
    
    public void AcctCfgScreen_fieldChanged(Field field, int context) {
        if(field == saveButton) {
            field.setDirty(false);
            onClose();
        }
        else if(field == serverSslField) {
            if(acctConfig instanceof PopConfig) {
                if(serverSslField.getChecked()) {
                    serverPortField.setText("995");
                }
                else {
                    serverPortField.setText("110");
                }
            }
            else if(acctConfig instanceof ImapConfig) {
                if(serverSslField.getChecked()) {
                    serverPortField.setText("993");
                }
                else {
                    serverPortField.setText("143");
                }
            }
        }
    }

    protected boolean onSavePrompt() {
        if(acctNameField.getText().length() > 0 &&
           serverNameField.getText().length() > 0 &&
           serverPortField.getText().length() > 0) {
            return super.onSavePrompt();
        }
        else {
            int result =
            	Dialog.ask(resources.getString(LogicMailResource.CONFIG_PROMPT_INCOMPLETE),
            			new String[] {
            				resources.getString(LogicMailResource.MENUITEM_DISCARD),
            				resources.getString(LogicMailResource.MENUITEM_CANCEL) }, 0);
            if(result == 0) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    public void save() {
        this.acctConfig.setAcctName(acctNameField.getText());
        this.acctConfig.setServerName(serverNameField.getText());
        this.acctConfig.setServerSSL(serverSslField.getChecked());
        this.acctConfig.setServerPort(Integer.parseInt(serverPortField.getText()));
        this.acctConfig.setServerUser(serverUserField.getText());
        this.acctConfig.setServerPass(serverPassField.getText());
        this.acctConfig.setDeviceSide(!useMdsField.getChecked());
        
        IdentityConfig selectedIdentityConfig = (IdentityConfig)identityField.getChoice(identityField.getSelectedIndex());
        if(createDefaultIdentity) {
            String userName = serverUserField.getText();
            if(userName.length() == 0) {
                userName = "user";
            }
            selectedIdentityConfig.setEmailAddress(userName + "@" + serverNameField.getText());
            MailSettings.getInstance().addIdentityConfig(selectedIdentityConfig);
            createDefaultIdentity = false;
        }
        this.acctConfig.setIdentityConfig(selectedIdentityConfig);

        OutgoingConfig selectedOutgoingConfig = (OutgoingConfig)outgoingServerField.getChoice(outgoingServerField.getSelectedIndex());
        if(selectedOutgoingConfig.getUniqueId() == -1) {
            this.acctConfig.setOutgoingConfig(null);
        }
        else {
            this.acctConfig.setOutgoingConfig(selectedOutgoingConfig);
        }

        if(acctConfig instanceof ImapConfig) {
            ImapConfig imapConfig = (ImapConfig)acctConfig;
            ObjectChoiceItem sentFolderChoice =
                    (ObjectChoiceItem)sentFolderChoiceField.getChoice(sentFolderChoiceField.getSelectedIndex());
            if(sentFolderChoice.getItem() == null) {
                imapConfig.setSentFolder(null);
            }
            else {
                imapConfig.setSentFolder(((MailboxNode)sentFolderChoice.getItem()).getPath());
            }
            
            String folderPrefix = folderPrefixField.getText().trim();
            if(folderPrefix.length() == 0) {
                imapConfig.setFolderPrefix(null);
            }
            else {
                imapConfig.setFolderPrefix(folderPrefix);
            }
        }
        
        acctSaved = true;
    }
    
    public boolean acctSaved() {
        return acctSaved;
    }
}
