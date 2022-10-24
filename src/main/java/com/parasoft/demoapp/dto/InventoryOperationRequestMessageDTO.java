package com.parasoft.demoapp.dto;

import com.parasoft.demoapp.model.industry.InventoryOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryOperationRequestMessageDTO {
    private InventoryOperation operation;
    private String orderNumber;
    private List<InventoryInfoDTO> inventoryInfos;
    private String info;
}
