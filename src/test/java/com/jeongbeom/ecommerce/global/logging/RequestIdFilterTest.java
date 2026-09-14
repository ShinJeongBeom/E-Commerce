package com.jeongbeom.ecommerce.global.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestIdFilterTest {

    private final RequestIdFilter requestIdFilter = new RequestIdFilter();

    @Test
    void 전달받은_정상_RequestId를_로그와_응답에_사용한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> capturedRequestId = new AtomicReference<>();

        request.addHeader(
                RequestIdFilter.REQUEST_ID_HEADER,
                "order-request-123"
        );

        requestIdFilter.doFilter(
                request,
                response,
                (servletRequest, servletResponse) ->
                        capturedRequestId.set(MDC.get("requestId"))
        );

        assertEquals(
                "order-request-123",
                capturedRequestId.get()
        );
        assertEquals(
                "order-request-123",
                response.getHeader(RequestIdFilter.REQUEST_ID_HEADER)
        );
        assertNull(MDC.get("requestId"));
    }

    @Test
    void 잘못된_RequestId는_UUID로_교체한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> capturedRequestId = new AtomicReference<>();

        request.addHeader(
                RequestIdFilter.REQUEST_ID_HEADER,
                "invalid request id"
        );

        requestIdFilter.doFilter(
                request,
                response,
                (servletRequest, servletResponse) ->
                        capturedRequestId.set(MDC.get("requestId"))
        );

        String generatedRequestId = capturedRequestId.get();

        assertNotNull(generatedRequestId);
        assertNotEquals("invalid request id", generatedRequestId);
        UUID.fromString(generatedRequestId);

        assertEquals(
                generatedRequestId,
                response.getHeader(RequestIdFilter.REQUEST_ID_HEADER)
        );
        assertNull(MDC.get("requestId"));
    }
}
