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

package org.apache.james.mailbox.model;

import static org.apache.james.mailbox.model.FetchGroup.NO_MASK;

import java.util.Objects;

/**
 * Describes the contents to be fetched for a mail part. All
 * implementations MUST implement equals. Two implementations are equal
 * if and only if their paths are equal.
 */
public class PartContentDescriptor {
    private final int content;
    private final MimePath path;

    public PartContentDescriptor(MimePath path) {
        this(0, path);
    }

    public PartContentDescriptor(int content, MimePath path) {
        this.content = content;
        this.path = path;
    }

    public PartContentDescriptor or(int content) {
        return new PartContentDescriptor(this.content | content, path);
    }

    /**
     * Contents to be fetched. Composed bitwise.
     *
     * @return bitwise descripion
     * @see FetchGroup#MINIMAL_MASK
     * @see FetchGroup#MIME_DESCRIPTOR_MASK
     * @see FetchGroup#HEADERS_MASK
     * @see FetchGroup#FULL_CONTENT_MASK
     * @see FetchGroup#BODY_CONTENT_MASK
     * @see FetchGroup#MIME_HEADERS_MASK
     * @see FetchGroup#MIME_CONTENT_MASK
     */
    public int content() {
        return content;
    }

    /**
     * Path describing the part to be fetched.
     *
     * @return path describing the part, not null
     */
    public MimePath path() {
        return path;
    }

    public boolean hasMask(int mask) {
        return (content & mask) > NO_MASK;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof PartContentDescriptor) {
            PartContentDescriptor that = (PartContentDescriptor) obj;
            return Objects.equals(this.path, that.path);
        }
        return false;
    }

}
