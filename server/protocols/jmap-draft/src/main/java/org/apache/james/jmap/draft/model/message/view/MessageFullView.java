/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.jmap.draft.model.message.view;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.james.jmap.api.preview.Preview;
import org.apache.james.jmap.draft.methods.JmapResponseWriterImpl;
import org.apache.james.jmap.draft.model.Attachment;
import org.apache.james.jmap.draft.model.BlobId;
import org.apache.james.jmap.draft.model.Emailer;
import org.apache.james.jmap.draft.model.Keywords;
import org.apache.james.jmap.draft.model.Number;
import org.apache.james.jmap.draft.model.PreviewDTO;
import org.apache.james.mailbox.model.MailboxId;
import org.apache.james.mailbox.model.MessageId;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@JsonDeserialize(builder = MessageFullView.Builder.class)
@JsonFilter(JmapResponseWriterImpl.PROPERTIES_FILTER)
public class MessageFullView extends MessageHeaderView {

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends MessageHeaderView.Builder<MessageFullView.Builder> {
        private Optional<Preview> preview;
        private Optional<String> textBody = Optional.empty();
        private Optional<String> htmlBody = Optional.empty();
        private final ImmutableList.Builder<Attachment> attachments;
        private final ImmutableMap.Builder<BlobId, SubMessage> attachedMessages;

        private Builder() {
            super();
            attachments = ImmutableList.builder();
            attachedMessages = ImmutableMap.builder();
        }

        public Builder preview(Preview preview) {
            this.preview = Optional.of(preview);
            return this;
        }

        public Builder preview(Optional<Preview> preview) {
            this.preview = preview;
            return this;
        }

        public Builder textBody(Optional<String> textBody) {
            this.textBody = textBody;
            return this;
        }

        public Builder htmlBody(Optional<String> htmlBody) {
            this.htmlBody = htmlBody;
            return this;
        }

        public Builder attachments(List<Attachment> attachments) {
            this.attachments.addAll(attachments);
            return this;
        }

        public Builder attachedMessages(Map<BlobId, SubMessage> attachedMessages) {
            this.attachedMessages.putAll(attachedMessages);
            return this;
        }

        public MessageFullView build() {
            ImmutableList<Attachment> attachments = this.attachments.build();
            ImmutableMap<BlobId, SubMessage> attachedMessages = this.attachedMessages.build();
            checkState(attachments, attachedMessages);
            boolean hasAttachment = hasAttachment(attachments);

            return new MessageFullView(id, blobId, threadId, mailboxIds, Optional.ofNullable(inReplyToMessageId),
                hasAttachment, headers, Optional.ofNullable(from),
                to.build(), cc.build(), bcc.build(), replyTo.build(), subject, date, size, PreviewDTO.from(preview), textBody, htmlBody, attachments, attachedMessages,
                keywords.orElse(Keywords.DEFAULT_VALUE));
        }

        public void checkState(ImmutableList<Attachment> attachments, ImmutableMap<BlobId, SubMessage> attachedMessages) {
            super.checkState();
            Preconditions.checkState(preview != null, "'preview' is mandatory");
            Preconditions.checkState(areAttachedMessagesKeysInAttachments(attachments, attachedMessages), "'attachedMessages' keys must be in 'attachements'");
        }
    }

    protected static boolean areAttachedMessagesKeysInAttachments(ImmutableList<Attachment> attachments, ImmutableMap<BlobId, SubMessage> attachedMessages) {
        return attachedMessages.isEmpty() || attachedMessages.keySet().stream()
                .anyMatch(inAttachments(attachments));
    }

    private static Predicate<BlobId> inAttachments(ImmutableList<Attachment> attachments) {
        return (key) -> attachments.stream()
            .map(Attachment::getBlobId)
            .anyMatch(blobId -> blobId.equals(key));
    }

    private static boolean hasAttachment(List<Attachment> attachments) {
        return attachments.stream()
                .anyMatch(attachment -> !attachment.isInlinedWithCid());
    }

    private final boolean hasAttachment;
    private final PreviewDTO preview;
    private final Optional<String> textBody;
    private final Optional<String> htmlBody;
    private final ImmutableList<Attachment> attachments;
    private final ImmutableMap<BlobId, SubMessage> attachedMessages;

    @VisibleForTesting
    MessageFullView(MessageId id,
                    BlobId blobId,
                    String threadId,
                    ImmutableList<MailboxId> mailboxIds,
                    Optional<String> inReplyToMessageId,
                    boolean hasAttachment,
                    ImmutableMap<String, String> headers,
                    Optional<Emailer> from,
                    ImmutableList<Emailer> to,
                    ImmutableList<Emailer> cc,
                    ImmutableList<Emailer> bcc,
                    ImmutableList<Emailer> replyTo,
                    String subject,
                    Instant date,
                    Number size,
                    PreviewDTO preview,
                    Optional<String> textBody,
                    Optional<String> htmlBody,
                    ImmutableList<Attachment> attachments,
                    ImmutableMap<BlobId, SubMessage> attachedMessages,
                    Keywords keywords) {
        super(id, blobId, threadId, mailboxIds, inReplyToMessageId, headers, from, to, cc, bcc, replyTo, subject, date, size, keywords);
        this.hasAttachment = hasAttachment;
        this.preview = preview;
        this.textBody = textBody;
        this.htmlBody = htmlBody;
        this.attachments = attachments;
        this.attachedMessages = attachedMessages;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public PreviewDTO getPreview() {
        return preview;
    }

    public Optional<String> getTextBody() {
        return textBody;
    }

    public Optional<String> getHtmlBody() {
        return htmlBody;
    }

    public ImmutableList<Attachment> getAttachments() {
        return attachments;
    }

    public ImmutableMap<BlobId, SubMessage> getAttachedMessages() {
        return attachedMessages;
    }
}
