package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.InventoryInfoDTO;
import com.parasoft.demoapp.dto.InventoryOperation;
import com.parasoft.demoapp.dto.InventoryOperationRequestMessageDTO;
import com.parasoft.demoapp.dto.InventoryOperationResultMessageDTO;
import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import com.parasoft.demoapp.repository.industry.ItemInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.parasoft.demoapp.dto.InventoryOperation.DECREASE;
import static com.parasoft.demoapp.dto.InventoryOperation.INCREASE;
import static com.parasoft.demoapp.dto.InventoryOperation.NONE;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.FAIL;
import static com.parasoft.demoapp.dto.InventoryOperationStatus.SUCCESS;

@Service
public class ItemInventoryService {

    @Autowired
    private ItemInventoryRepository itemInventoryRepository;

    @Transactional
    public InventoryOperationResultMessageDTO handleMessageFromRequestQueue(InventoryOperationRequestMessageDTO
                                                                                        requestMessage) {
        InventoryOperation operation = requestMessage.getOperation();
        if (operation == NONE) {
            return null;
        }

        InventoryOperationResultMessageDTO resultMessage = new InventoryOperationResultMessageDTO();
        resultMessage.setOperation(operation);
        resultMessage.setOrderNumber(requestMessage.getOrderNumber());
        List<InventoryInfoDTO> requestedItems = requestMessage.getInventoryInfos();
        if (CollectionUtils.isEmpty(requestedItems)) {
            return null;
        }

        if (operation == DECREASE) {
            return decrease(requestedItems, resultMessage);
        }

        if (operation == INCREASE) {
            return increase(requestedItems, resultMessage);
        }

        return null;
    }

    private synchronized InventoryOperationResultMessageDTO decrease(List<InventoryInfoDTO> requestedItems,
                                                        InventoryOperationResultMessageDTO resultMessage) {
        String errorMessage = validateItemStockBeforeDecrease(requestedItems);
        if (errorMessage.isEmpty()) {
            requestedItems.forEach(this::decreaseItemStock);
            resultMessage.setStatus(SUCCESS);
        } else {
            resultMessage.setInfo(errorMessage);
            resultMessage.setStatus(FAIL);
        }
        return resultMessage;
    }

    private synchronized InventoryOperationResultMessageDTO increase(List<InventoryInfoDTO> requestedItems,
                                                                     InventoryOperationResultMessageDTO resultMessage) {
        String errorMessage = validateItemStockBeforeIncrease(requestedItems);
        if (errorMessage.isEmpty()) {
            requestedItems.forEach(this::increaseItemStock);
            resultMessage.setStatus(SUCCESS);
        } else {
            resultMessage.setInfo(errorMessage);
            resultMessage.setStatus(FAIL);
        }
        return resultMessage;
    }

    private void decreaseItemStock(InventoryInfoDTO requestedItem) {
        ItemInventoryEntity inventoryItem = itemInventoryRepository.findByItemId(requestedItem.getItemId());
        inventoryItem.setInStock(inventoryItem.getInStock() - requestedItem.getQuantity());
        itemInventoryRepository.save(inventoryItem);
    }

    private void increaseItemStock(InventoryInfoDTO requestedItem) {
        ItemInventoryEntity inventoryItem = itemInventoryRepository.findByItemId(requestedItem.getItemId());
        inventoryItem.setInStock(inventoryItem.getInStock() + requestedItem.getQuantity());
        itemInventoryRepository.save(inventoryItem);
    }

    private String validateItemStockBeforeDecrease(List<InventoryInfoDTO> requestedItems) {
        for (InventoryInfoDTO requestedItem : requestedItems) {
            Long itemId = requestedItem.getItemId();
            ItemInventoryEntity inventoryItem = itemInventoryRepository.findByItemId(itemId);
            if (inventoryItem == null) {
                return String.format("Inventory item with id %d doesn't exist.", itemId);
            } else if (inventoryItem.getInStock() < requestedItem.getQuantity()) {
                return String.format("Inventory item with id %d is out of stock.", itemId);
            }
        }
        return "";
    }

    private String validateItemStockBeforeIncrease(List<InventoryInfoDTO> requestedItems) {
        for (InventoryInfoDTO requestedItem : requestedItems) {
            Long itemId = requestedItem.getItemId();
            ItemInventoryEntity inventoryItem = itemInventoryRepository.findByItemId(itemId);
            if (inventoryItem == null) {
                return String.format("Inventory item with id %d doesn't exist.", itemId);
            }
        }
        return "";
    }
}
