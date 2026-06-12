package com.blps.blps.workers.clientFlow;

import com.blps.blps.entity.Address;
import com.blps.blps.entity.User;
import com.blps.blps.service.UserService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.exception.BpmnError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DetermineDeliveryAddressWorker {

    private final UserService userService;

    @JobWorker(type = "determine-delivery-address")
    public Map<String, Object> determineDeliveryAddress(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Object clientIdObj = vars.get("client_id");

        Long clientId = Long.parseLong(clientIdObj.toString());
        User user = userService.getUserById(clientId);

        Address userAddress = user.getAddress();
        if (userAddress == null) {
            throw new BpmnError("BusinessException", "У пользователя не указан адрес доставки");
        }
        return Map.of("deliveryAddress", userAddress);
    }
}