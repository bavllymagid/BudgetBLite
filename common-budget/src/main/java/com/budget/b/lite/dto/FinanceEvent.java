package com.budget.b.lite.dto;

import com.budget.b.lite.dto.constants.EntityType;
import com.budget.b.lite.dto.constants.EventAction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FinanceEvent {
    private String userId;
    private Long expenseId;
    private EntityType entityType;
    private EventAction action;
    private LocalDateTime timestamp;

    private ExpenseChange expenseChange;

    public FinanceEvent(String userId, EntityType entityType, EventAction action, LocalDateTime timestamp) {
        this.userId = userId;
        this.entityType = entityType;
        this.action = action;
        this.timestamp = timestamp;
    }

}