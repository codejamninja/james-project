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

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.james.jmap.draft.model.BlobId;
import org.apache.james.mailbox.BlobManager;
import org.apache.james.mailbox.exception.MailboxException;
import org.apache.james.mailbox.model.MailboxId;
import org.apache.james.mailbox.model.MessageResult;

import com.google.common.annotations.VisibleForTesting;

public class MessageMetadataViewFactory implements MessageViewFactory<MessageMetadataView> {
    private final BlobManager blobManager;

    @Inject
    @VisibleForTesting
    public MessageMetadataViewFactory(BlobManager blobManager) {
        this.blobManager = blobManager;
    }

    @Override
    public MessageMetadataView fromMessageResults(Collection<MessageResult> messageResults) throws MailboxException {
        assertOneMessageId(messageResults);

        MessageResult firstMessageResult = messageResults.iterator().next();
        List<MailboxId> mailboxIds = getMailboxIds(messageResults);

        return MessageMetadataView.messageMetadataBuilder()
            .id(firstMessageResult.getMessageId())
            .mailboxIds(mailboxIds)
            .blobId(BlobId.of(blobManager.toBlobId(firstMessageResult.getMessageId())))
            .threadId(firstMessageResult.getMessageId().serialize())
            .keywords(getKeywords(messageResults))
            .size(firstMessageResult.getSize())
            .build();
    }
}
