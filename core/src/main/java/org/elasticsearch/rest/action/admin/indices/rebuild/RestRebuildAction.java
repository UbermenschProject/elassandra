/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.rest.action.admin.indices.rebuild;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;
import static org.elasticsearch.rest.RestStatus.OK;
import static org.elasticsearch.rest.action.support.RestActions.buildBroadcastShardsHeader;

import org.elassandra.action.admin.indices.rebuild.RebuildRequest;
import org.elassandra.action.admin.indices.rebuild.RebuildResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.action.support.RestBuilderListener;

/**
 *
 */
public class RestRebuildAction extends BaseRestHandler {

    @Inject
    public RestRebuildAction(Settings settings, RestController controller, Client client) {
        super(settings, controller, client);
        controller.registerHandler(POST, "/_rebuild", this);
        controller.registerHandler(POST, "/{index}/_rebuild", this);

        controller.registerHandler(GET, "/_rebuild", this);
        controller.registerHandler(GET, "/{index}/_rebuild", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel, final Client client) {
        RebuildRequest rebuildRequest = new RebuildRequest(Strings.splitStringByCommaToArray(request.param("index")));
        rebuildRequest.indicesOptions(IndicesOptions.fromRequest(request, rebuildRequest.indicesOptions()));
        rebuildRequest.numThreads(request.paramAsInt("num_threads", 1));
        //rebuildRequest.waitIfOngoing(request.paramAsBoolean("wait_if_ongoing", rebuildRequest.waitIfOngoing()));
        
        client.admin().indices().rebuild(rebuildRequest, new RestBuilderListener<RebuildResponse>(channel) {
            @Override
            public RestResponse buildResponse(RebuildResponse response, XContentBuilder builder) throws Exception {
                builder.startObject();
                buildBroadcastShardsHeader(builder, request, response);
                builder.endObject();
                return new BytesRestResponse(OK, builder);
            }
        });
    }
}
