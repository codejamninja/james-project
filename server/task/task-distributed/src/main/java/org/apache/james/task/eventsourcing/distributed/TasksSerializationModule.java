/** **************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 * http://www.apache.org/licenses/LICENSE-2.0                   *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 * ***************************************************************/

package org.apache.james.task.eventsourcing.distributed;

import java.util.Set;
import java.util.stream.Stream;

import org.apache.james.eventsourcing.eventstore.cassandra.dto.EventDTOModule;
import org.apache.james.json.DTOConverter;
import org.apache.james.server.task.json.JsonTaskSerializer;
import org.apache.james.server.task.json.dto.AdditionalInformationDTO;
import org.apache.james.task.TaskExecutionDetails;
import org.apache.james.task.eventsourcing.AdditionalInformationUpdated;
import org.apache.james.task.eventsourcing.CancelRequested;
import org.apache.james.task.eventsourcing.Cancelled;
import org.apache.james.task.eventsourcing.Completed;
import org.apache.james.task.eventsourcing.Created;
import org.apache.james.task.eventsourcing.Failed;
import org.apache.james.task.eventsourcing.Started;

import com.github.steveash.guavate.Guavate;

public interface TasksSerializationModule {
    @FunctionalInterface
    interface TaskSerializationModuleFactory {
        EventDTOModule<?, ?> create(JsonTaskSerializer taskSerializer, DTOConverter<TaskExecutionDetails.AdditionalInformation, AdditionalInformationDTO> additionalInformationConverter);
    }

    TaskSerializationModuleFactory CREATED = (jsonTaskSerializer, additionalInformationConverter) -> EventDTOModule
        .forEvent(Created.class)
        .convertToDTO(CreatedDTO.class)
        .toDomainObjectConverter(dto -> dto.toDomainObject(jsonTaskSerializer))
        .toDTOConverter((event, typeName) -> CreatedDTO.fromDomainObject(event, typeName, jsonTaskSerializer))
        .typeName("task-manager-created")
        .withFactory(EventDTOModule::new);

    TaskSerializationModuleFactory STARTED = (jsonTaskSerializer, additionalInformationConverter) -> EventDTOModule
        .forEvent(Started.class)
        .convertToDTO(StartedDTO.class)
        .toDomainObjectConverter(StartedDTO::toDomainObject)
        .toDTOConverter(StartedDTO::fromDomainObject)
        .typeName("task-manager-started")
        .withFactory(EventDTOModule::new);

    TaskSerializationModuleFactory CANCEL_REQUESTED = (jsonTaskSerializer, additionalInformationConverter) -> EventDTOModule
        .forEvent(CancelRequested.class)
        .convertToDTO(CancelRequestedDTO.class)
        .toDomainObjectConverter(CancelRequestedDTO::toDomainObject)
        .toDTOConverter(CancelRequestedDTO::fromDomainObject)
        .typeName("task-manager-cancel-requested")
        .withFactory(EventDTOModule::new);

    TaskSerializationModuleFactory COMPLETED = (jsonTaskSerializer, additionalInformationConverter) -> EventDTOModule
        .forEvent(Completed.class)
        .convertToDTO(CompletedDTO.class)
        .toDomainObjectConverter(dto -> dto.toDomainObject(additionalInformationConverter))
        .toDTOConverter((event, typeName) -> CompletedDTO.fromDomainObject(additionalInformationConverter, event, typeName))
        .typeName("task-manager-completed")
        .withFactory(EventDTOModule::new);

    TaskSerializationModuleFactory FAILED = (jsonTaskSerializer, additionalInformationConverter) -> EventDTOModule
        .forEvent(Failed.class)
        .convertToDTO(FailedDTO.class)
        .toDomainObjectConverter(dto -> dto.toDomainObject(additionalInformationConverter))
        .toDTOConverter((event, typeName) -> FailedDTO.fromDomainObject(additionalInformationConverter, event, typeName))
        .typeName("task-manager-failed")
        .withFactory(EventDTOModule::new);

    TaskSerializationModuleFactory CANCELLED = (jsonTaskSerializer, additionalInformationConverter) -> EventDTOModule
        .forEvent(Cancelled.class)
        .convertToDTO(CancelledDTO.class)
        .toDomainObjectConverter(dto -> dto.toDomainObject(additionalInformationConverter))
        .toDTOConverter((event, typeName) -> CancelledDTO.fromDomainObject(additionalInformationConverter, event, typeName))
        .typeName("task-manager-cancelled")
        .withFactory(EventDTOModule::new);

    TaskSerializationModuleFactory UPDATED = (jsonTaskSerializer, additionalInformationConverter) -> EventDTOModule
        .forEvent(AdditionalInformationUpdated.class)
        .convertToDTO(AdditionalInformationUpdatedDTO.class)
        .toDomainObjectConverter(dto -> dto.toDomainObject(additionalInformationConverter))
        .toDTOConverter((event, typeName) -> AdditionalInformationUpdatedDTO.fromDomainObject(additionalInformationConverter, event, typeName))
        .typeName("task-manager-updated")
        .withFactory(EventDTOModule::new);

    static Set<EventDTOModule<?, ?>> list(JsonTaskSerializer jsonTaskSerializer, DTOConverter<TaskExecutionDetails.AdditionalInformation, AdditionalInformationDTO> additionalInformationConverter) {
        return Stream
            .of(CREATED, STARTED, CANCEL_REQUESTED, CANCELLED, COMPLETED, FAILED, UPDATED)
            .map(moduleFactory -> moduleFactory.create(jsonTaskSerializer, additionalInformationConverter))
            .collect(Guavate.toImmutableSet());
    }
}
