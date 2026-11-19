package com.procura.shared.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
    private final String ruleId;
    private final ApiErrorCode errorCode;

    public BusinessRuleException(String ruleId, String message) {
        super(message);
        this.ruleId = ruleId;
        this.errorCode = ApiErrorCode.BUSINESS_RULE_VIOLATION;
    }

    public BusinessRuleException(String ruleId, ApiErrorCode errorCode, String message) {
        super(message);
        this.ruleId = ruleId;
        this.errorCode = errorCode;
    }
}
