/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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

package io.quarkus.keycloak.admin.client.reactive.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.keycloak.representations.idm.RoleRepresentation;

/**
 * @author rodrigo.sasaki@icarros.com.br
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RoleScopeResource {

    @GET
    List<RoleRepresentation> listAll();

    @GET
    @Path("available")
    List<RoleRepresentation> listAvailable();

    @GET
    @Path("composite")
    List<RoleRepresentation> listEffective();

    @GET
    @Path("composite")
    List<RoleRepresentation> listEffective(
            @QueryParam("briefRepresentation") @DefaultValue("true") boolean briefRepresentation);

    @POST
    void add(List<RoleRepresentation> rolesToAdd);

    @DELETE
    void remove(List<RoleRepresentation> rolesToRemove);

}
