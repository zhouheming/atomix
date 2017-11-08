/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.rest.impl;

import io.atomix.primitives.counter.AsyncAtomicCounter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Atomic counter resource.
 */
public class AtomicCounterResource extends AbstractRestResource {
  private final AsyncAtomicCounter counter;

  public AtomicCounterResource(AsyncAtomicCounter counter) {
    this.counter = counter;
  }

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public void get(@Suspended AsyncResponse response) {
    counter.get().whenComplete((result, error) -> {
      if (error == null) {
        response.resume(Response.status(Status.OK)
            .entity(result)
            .build());
      } else {
        response.resume(Response.status(Status.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  @POST
  @Path("/")
  @Consumes(MediaType.TEXT_PLAIN)
  public void set(Long value, @Suspended AsyncResponse response) {
    counter.set(value).whenComplete((result, error) -> {
      if (error == null) {
        response.resume(Response.status(Status.OK).build());
      } else {
        response.resume(Response.status(Status.INTERNAL_SERVER_ERROR).build());
      }
    });
  }

  @POST
  @Path("/increment")
  @Produces(MediaType.APPLICATION_JSON)
  public void incrementAndGet(@Suspended AsyncResponse response) {
    counter.incrementAndGet().whenComplete((result, error) -> {
      if (error == null) {
        response.resume(Response.status(Status.OK)
            .entity(result)
            .build());
      } else {
        response.resume(Response.status(Status.INTERNAL_SERVER_ERROR).build());
      }
    });
  }
}
