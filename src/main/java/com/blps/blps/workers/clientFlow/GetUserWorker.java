package com.blps.blps.workers.clientFlow;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class GetUserWorker {

    @JobWorker(type = "get-user")
    public Map<String, Object> getUser(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        Object clientIdObj = vars.get("client_id");

        long rawId = Long.parseLong(clientIdObj.toString());
        String newClientId = (rawId == -1L) ? "1" : clientIdObj.toString();

        return Map.of("client_id", newClientId);
    }
}