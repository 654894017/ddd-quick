package com.damon.demo.application.order.command;

import com.damon.demo.domain.gateway.inventory.IInventoryGateway;
import com.damon.demo.domain.order.entity.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class OrderSubmitStatusCheckCmdExeTest {
    @Mock
    private OrderLog orderLog;
    @Mock
    private IInventoryGateway inventoryGateway;
    @Mock
    private OrderLogDomainService orderLogDomainService;
    @InjectMocks
    private OrderSubmitStatusCheckCmdExe orderSubmitStatusCheckCmdExe;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteOrderLogNotFound() {
        when(orderLogDomainService.get(any(OrderId.class))).thenReturn(null);

        OrderSubmitStatusCheckCmd cmd = new OrderSubmitStatusCheckCmd(123L);

        orderSubmitStatusCheckCmdExe.execute(cmd);
        verify(orderLogDomainService, times(1)).get(any(OrderId.class));
        verifyNoMoreInteractions(inventoryGateway);
    }

    @Test
    public void testExecuteOrderLogFinished() {
        OrderLog orderLog = new OrderLog(123L, 123L, 5);
        when(orderLogDomainService.get(any(OrderId.class))).thenReturn(orderLog);

        OrderSubmitStatusCheckCmd cmd = new OrderSubmitStatusCheckCmd(123L);

        orderSubmitStatusCheckCmdExe.execute(cmd);

        verifyNoMoreInteractions(inventoryGateway);
    }

    @Test
    public void testExecuteOrderLogSubmitted() {
        //OrderLog orderLog = new OrderLog();
        when(orderLogDomainService.get(any(OrderId.class))).thenReturn(orderLog);
        when(orderLog.isFinished()).thenReturn(false);
        when(orderLog.isRollbacked()).thenReturn(false);
        when(orderLog.isSubmitted()).thenReturn(true);
        doThrow(new RuntimeException("abc123")).when(inventoryGateway).commitDeduction(123L);
        when(orderLogDomainService.finishLog(any(OrderLog.class))).thenReturn(true);

        OrderSubmitStatusCheckCmd cmd = new OrderSubmitStatusCheckCmd(123l);

        orderSubmitStatusCheckCmdExe.execute(cmd);

        verify(inventoryGateway).commitDeduction(cmd.getOrderId());
        verify(orderLogDomainService).finishLog(orderLog);
        // verifyNoMoreInteractions(inventoryGateway, orderLogService);
    }

    @Test
    public void testExecuteOrderLogSubmitted2() {
        //OrderLog orderLog = new OrderLog();
        when(orderLogDomainService.get(any(OrderId.class))).thenReturn(orderLog);
        when(orderLog.isFinished()).thenReturn(false);
        when(orderLog.isRollbacked()).thenReturn(false);
        when(orderLog.isSubmitted()).thenReturn(true);
        doThrow(new RuntimeException("abc123")).when(inventoryGateway).commitDeduction(123L);
        when(orderLogDomainService.finishLog(any(OrderLog.class))).thenReturn(true);

        OrderSubmitStatusCheckCmd cmd = new OrderSubmitStatusCheckCmd(123l);
        try {
            orderSubmitStatusCheckCmdExe.execute(cmd);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                if (e.getMessage().equals("abc123")) {
                    throw new RuntimeException(e);
                }
            }
        }

        verify(inventoryGateway).commitDeduction(cmd.getOrderId());
    }

    // Add more test methods for other scenarios...

    // Make sure to test all branches of the execute method.
}
