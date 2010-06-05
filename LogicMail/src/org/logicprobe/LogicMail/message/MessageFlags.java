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

package org.logicprobe.LogicMail.message;

/**
 * This class contains flags that may be associated with a message.
 * These flags are normally part of a <tt>FolderMessage</tt>, however
 * they sometimes need to be represented independently.
 */
public class MessageFlags {
    private boolean seen;
    private boolean answered;
    private boolean flagged;
    private boolean deleted;
    private boolean draft;
    private boolean recent;
    private boolean forwarded;
    private boolean junk;
    
	public MessageFlags() {
	}
	
	public MessageFlags(
		    boolean seen,
		    boolean answered,
		    boolean flagged,
		    boolean deleted,
		    boolean draft,
		    boolean recent,
		    boolean forwarded,
		    boolean junk) {
		this.seen = seen;
		this.answered = answered;
		this.flagged = flagged;
		this.deleted = deleted;
		this.draft = draft;
		this.recent = recent;
		this.forwarded = forwarded;
		this.junk = junk;
	}
	
    /**
     * Find out whether this message has been previously viewed
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * Set the flag indicating whether this message has been previously viewed
     */
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    /**
     * Find out whether this message has been replied to
     */
    public boolean isAnswered() {
        return answered;
    }

    /**
     * Set the flag indicating whether this message has been replied to
     */
    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    /**
     * Find out whether this message has been flagged
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * Set the flag indicating whether this message has been flagged
     */
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    /**
     * Find out whether this message has been marked as deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Set the flag indicating whether this message has been marked as deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Find out whether this message is a draft
     */
    public boolean isDraft() {
        return draft;
    }

    /**
     * Set the flag indicating whether this message is a draft
     */
    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    /**
     * Find out whether this message has recently arrived
     */
    public boolean isRecent() {
        return recent;
    }

    /**
     * Set the flag indicating whether this message has recently arrived
     */
    public void setRecent(boolean recent) {
        this.recent = recent;
    }
    
    /**
     * Find out whether this message has been forwarded
     */
    public boolean isForwarded() {
        return forwarded;
    }
    
    /**
     * Set the flag indicating whether this message has been forwarded
     */
    public void setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }
    
    /**
     * Find out whether this message has been flagged as junk
     */
    public boolean isJunk() {
        return junk;
    }

    /**
     * Set the flag indicating whether this message has been flagged as junk
     */
    public void setJunk(boolean junk) {
        this.junk = junk;
    }
}
