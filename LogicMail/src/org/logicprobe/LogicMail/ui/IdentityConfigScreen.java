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

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;

import org.logicprobe.LogicMail.LogicMailResource;
import org.logicprobe.LogicMail.conf.IdentityConfig;

/**
 * Identity configuration screen
 */
public class IdentityConfigScreen extends AbstractConfigScreen {
    private IdentityConfig identityConfig;
    private boolean configSaved;

    private BasicEditField identityNameField;
    private BasicEditField fullNameField;
    private EmailAddressEditField emailAddressField;
    private EmailAddressEditField replyToAddressField;
    private AutoTextEditField msgSignatureField;

    /**
     * Creates a new instance of IdentityConfigScreen
     */
    public IdentityConfigScreen(IdentityConfig identityConfig) {
        super("LogicMail - " + resources.getString(LogicMailResource.CONFIG_IDENTITY_TITLE));
        this.identityConfig = identityConfig;
        this.configSaved = false;
        initFields();
    }

    private void initFields() {
        identityNameField = new BasicEditField(resources.getString(LogicMailResource.CONFIG_IDENTITY_IDENTITY) + " ", identityConfig.getIdentityName());
        fullNameField = new BasicEditField(resources.getString(LogicMailResource.CONFIG_IDENTITY_FULL_NAME) + " ", identityConfig.getFullName());
        emailAddressField = new EmailAddressEditField(resources.getString(LogicMailResource.CONFIG_IDENTITY_EMAIL_ADDRESS) + " ", identityConfig.getEmailAddress());
        replyToAddressField = new EmailAddressEditField(resources.getString(LogicMailResource.CONFIG_IDENTITY_REPLYTO_ADDRESS) + " ", identityConfig.getReplyToAddress());
        msgSignatureField = new AutoTextEditField();
        msgSignatureField.setText(identityConfig.getMsgSignature());

        add(identityNameField);
        add(fullNameField);
        add(emailAddressField);
        add(replyToAddressField);
        add(new SeparatorField());
        add(new RichTextField(resources.getString(LogicMailResource.CONFIG_IDENTITY_SIGNATURE), Field.NON_FOCUSABLE));
        add(msgSignatureField);
    }

    protected boolean onSavePrompt() {
        if(identityNameField.getText().length() > 0 &&
                emailAddressField.getText().length() > 0) {
            return super.onSavePrompt();
        }
        else {
            int result = Dialog.ask(
                    resources.getString(LogicMailResource.CONFIG_PROMPT_INCOMPLETE),
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
        identityConfig.setIdentityName(identityNameField.getText());
        identityConfig.setFullName(fullNameField.getText());
        identityConfig.setEmailAddress(emailAddressField.getText());
        identityConfig.setReplyToAddress(replyToAddressField.getText());
        identityConfig.setMsgSignature(msgSignatureField.getText());
        configSaved = true;
    }

    public boolean configSaved() {
        return configSaved;
    }
}
