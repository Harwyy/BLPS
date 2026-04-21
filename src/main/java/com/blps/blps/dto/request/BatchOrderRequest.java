package com.blps.blps.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class BatchOrderRequest {
    private List<Long> orderIds;
}