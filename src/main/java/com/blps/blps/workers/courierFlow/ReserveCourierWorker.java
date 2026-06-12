package com.blps.blps.workers.courierFlow;

import com.blps.blps.entity.Courier;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.repository.CourierRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.exception.BpmnError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveCourierWorker {

    private final CourierRepository courierRepository;
    private final ObjectMapper objectMapper;

    @JobWorker(type = "reserve-courier")
    @Transactional
    public Map<String, Object> reserveCourier(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Object bestCourierObj = vars.get("bestCourier");

        Courier bestCourier = objectMapper.convertValue(bestCourierObj, Courier.class);

        Long courierId = bestCourier.getId();
        if (courierId == null) {
            throw new BpmnError("BusinessException", "У bestCourier отсутствует id");
        }

        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new BpmnError("BusinessException", "Курьер не найден: " + courierId));

        courier.setStatus(CourierStatus.BUSY);
        courier.setActiveOrdersCount(courier.getActiveOrdersCount() + 1);
        Courier savedCourier = courierRepository.save(courier);

        return Map.of("reserved_courier", savedCourier);
    }
}