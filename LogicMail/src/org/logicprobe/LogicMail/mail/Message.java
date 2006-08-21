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

package org.logicprobe.LogicMail.mail;

import java.util.Date;

/**
 * This class encapsulates all the data to represent an E-Mail message.
 */
public class Message {
    /**
     * Since this class exists only as a data
     * structure namespace, its constructor
     * is private to prevent instantiation.
     */
    private Message() { }
    
    /**
     * Relevant header fields for a message.
     */
    public static class Envelope {
        // official envelope fields
        public Date date;
        public String subject;
        public String[] from;
        public String[] sender;
        public String[] replyTo;
        public String[] to;
        public String[] cc;
        public String[] bcc;
        public String inReplyTo;
        public String messageId;
        // other useful tidbits
        public int index;
        public boolean isOpened;
    }

    /**
     * Message body section
     */
    public static class Section {
        public String type;
        public String subtype;
        public String encoding;
        public int size;
    }

    /**
     * Message body structure
     */
    public static class Structure {
        public Section[] sections;
    }
}
