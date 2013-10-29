/*
 * Copyright 2011 DBpedia Spotlight Development Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Check our project website for information on how to acknowledge the authors and how to contribute to the project: http://spotlight.dbpedia.org
 */

package org.dbpedia.spotlight.web.rest.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbpedia.spotlight.web.rest.Server;
import org.dbpedia.spotlight.web.rest.ServerUtils;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.dbpedia.spotlight.io.feedback.FeedbackMultiStore;
import org.dbpedia.spotlight.io.feedback.FeedbackValidator;
import org.dbpedia.spotlight.io.feedback.StandardFeedback;
import org.dbpedia.spotlight.io.feedback.TSVFeedbackStore;
import org.dbpedia.spotlight.io.feedback.CSVFeedbackStore;

import java.io.File;

/**
 * REST Web Service for feedback at http://<rest_url_setted_in_Server.java>/feedback //Default: http://localhost:2222/rest/feedback/
 * Send the feed back by a GET using file request is obligatory, e.g.: curl -X POST -d @/home/alexandre/Projects/feedbackIncorrect http://localhost:2222/rest/feedback/
 *
 *
 * TODO bulk feedback: users can post a gzip with json or xml encoded feedback
 *
 * @author pablomendes
 * @author Alexandre Cançado Cardoso - accardoso
 */

@ApplicationPath(Server.APPLICATION_PATH)
@Path("/feedback")
@Consumes("text/plain")
public class Feedback {

    Log LOG = LogFactory.getLog(this.getClass());

    @Context
    private UriInfo context;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.TEXT_XML,MediaType.APPLICATION_XML})
    public Response postXML(@DefaultValue("") @FormParam("key") String key,
                           @DefaultValue("") @FormParam("text") String text,
                           @DefaultValue("") @FormParam("url") String docUrlString,                          //Optional
                           @DefaultValue("") @FormParam("discourse_type") String discourseType,              //Optional
                           @DefaultValue("") @FormParam("entity_uri") String entityUri,
                           @DefaultValue("") @FormParam("right_entity") String entityUriSuggestion,          //Optional
                           @DefaultValue("") @FormParam("surface_form") String surfaceForm,
                           @DefaultValue("0") @FormParam("offset") int offset,
                           @DefaultValue("") @FormParam("feedback") String feedback,
                           @DefaultValue("") @FormParam("systems") String systemIds,
                           @DefaultValue("") @FormParam("is_manual_feedback") boolean isManualFeedback,
                           @DefaultValue("") @FormParam("language") String language,                         //Optional
                           @Context HttpServletRequest request) throws Exception {

        try {
            String clientIp = request.getRemoteAddr();

            Authentication.authenticate(clientIp, key);

            StandardFeedback standardFeedback =  FeedbackValidator.validateAndStandardize(text, docUrlString, discourseType, entityUri, entityUriSuggestion, surfaceForm, offset, feedback, systemIds, isManualFeedback, language);

            String storageFolderPath = FeedbackMultiStore.createStorageFolder("feedback-warehouse");
            FeedbackMultiStore multiStore = new FeedbackMultiStore();
            multiStore.addStore(new TSVFeedbackStore(storageFolderPath));
            multiStore.addStore(new TSVFeedbackStore(storageFolderPath, "feedbackStoreBackup"));
            multiStore.addStore(new CSVFeedbackStore(storageFolderPath));
            multiStore.addStore(new CSVFeedbackStore(new File(storageFolderPath + File.separator + "feedbackStoreBackup.csv")));
            multiStore.addStore(new TSVFeedbackStore(System.out));

            multiStore.addFeedback(standardFeedback);
            multiStore.addFeedback(standardFeedback);

            return ServerUtils.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST). entity(ServerUtils.print(e)).type(MediaType.TEXT_HTML).build());
        }

    }

}
