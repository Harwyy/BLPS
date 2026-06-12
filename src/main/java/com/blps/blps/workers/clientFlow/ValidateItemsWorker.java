package com.blps.blps.workers.clientFlow;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.exception.BpmnError;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ValidateItemsWorker {

    @JobWorker(type = "validate-items")
    public void validateItems(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        Object itemsObj = variables.get("items");

        if (itemsObj == null) {
            throw new BpmnError("BusinessException", "Заказ не может быть пустым. Переменная 'items' отсутствует.");
        }

        List<?> items = (List<?>) itemsObj;;

        if (items.isEmpty()) {
            throw new BpmnError("BusinessException", "Заказ не может быть пустым. Добавьте хотя бы одно блюдо.");
        }

        for (Object itemObj : items) {
            Map<?, ?> item = (Map<?, ?>) itemObj;

            Object productId = item.get("productId");
            if (productId == null) {
                throw new BpmnError("BusinessException", "У одного из товаров отсутствует productId (productId = null).");
            }
            Object quantityObj = item.get("quantity");
            if (quantityObj == null) {
                throw new BpmnError("BusinessException", "У одного из товаров отсутствует quantity (quantity = null).");
            }
            int quantity;
            try {
                quantity = ((Number) quantityObj).intValue();
            } catch (ClassCastException e) {
                throw new BpmnError("BusinessException", "quantity должно быть числом.");
            }
            if (quantity <= 0) {
                throw new BpmnError("BusinessException", "Количество товара должно быть больше 0.");
            }
        }
    }
}