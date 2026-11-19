package com.procura.shared;

import com.procura.shared.exception.ApiErrorCode;
import com.procura.shared.exception.BusinessRuleException;
import com.procura.shared.money.MoneyUtils;
import com.procura.shared.response.ApiResponse;
import com.procura.shared.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SharedModelTests {

    @Test
    @DisplayName("MoneyUtils rounds to 2 decimals using HALF_UP")
    void testMoneyUtilsRounding() {
        BigDecimal val = new BigDecimal("100.456");
        BigDecimal rounded = MoneyUtils.round(val);
        assertThat(rounded).isEqualByComparingTo("100.46");
    }

    @Test
    @DisplayName("MoneyUtils computes tax accurately")
    void testComputeTax() {
        BigDecimal subtotal = new BigDecimal("1000.00");
        BigDecimal taxRate = new BigDecimal("18.00");
        BigDecimal tax = MoneyUtils.computeTax(subtotal, taxRate);
        assertThat(tax).isEqualByComparingTo("180.00");
    }

    @Test
    @DisplayName("BusinessRuleException preserves ruleId and error code")
    void testBusinessRuleException() {
        BusinessRuleException ex = new BusinessRuleException("BR-13", "Received quantity cannot exceed ordered");
        assertThat(ex.getRuleId()).isEqualTo("BR-13");
        assertThat(ex.getErrorCode()).isEqualTo(ApiErrorCode.BUSINESS_RULE_VIOLATION);
    }

    @Test
    @DisplayName("ApiResponse formats standard envelope")
    void testApiResponseEnvelope() {
        ApiResponse<String> res = ApiResponse.ok("test-data", "OK");
        assertThat(res.status()).isEqualTo(200);
        assertThat(res.data()).isEqualTo("test-data");
        assertThat(res.message()).isEqualTo("OK");
        assertThat(res.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("ErrorResponse includes ruleId for business errors")
    void testErrorResponseWithRule() {
        ErrorResponse err = ErrorResponse.withRule(422, ApiErrorCode.BUSINESS_RULE_VIOLATION, "Rule breached", "BR-01", "/api/v1/requisitions/1/approve", "trace-123");
        assertThat(err.status()).isEqualTo(422);
        assertThat(err.ruleId()).isEqualTo("BR-01");
        assertThat(err.traceId()).isEqualTo("trace-123");
    }
}
