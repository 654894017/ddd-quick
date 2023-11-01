package test;

import com.damon.demo.domain.order.entity.Consignee;
import com.damon.demo.domain.order.entity.Order;
import com.damon.demo.domain.order.entity.OrderItem;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderItemPO;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderPO;
import com.damon.demo.infrastructure.gateway_impl.order.OrderFactory;
import com.damon.object_trace.Aggregate;
import com.damon.object_trace.AggregateFactory;
import com.damon.object_trace.ChangedEntity;
import com.damon.object_trace.ObjectComparator;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.DiffResult;

import java.util.List;

public class TestObjectTrace {

    public static void main(String[] args) {

        OrderItem item = new OrderItem(1L, 1L, 1L, "abc", 1, 5L);
        OrderItem item2 = new OrderItem(2l, 1L, 2L, "abc4", 1, 5L);
        OrderItem item3 = new OrderItem(3L, 1L, 3L, "abc", 1, 5L);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(1);
        order.setCreateTime(1L);
        order.setUpdateTime(2L);
        order.setOrderSubmitUserId(1111L);
        order.setCouponId(11L);
        order.setDeductionPoints(11L);
        order.setTotalMoney(100L);
        order.setActualPayMoney(98L);
        order.setDelete(0);
        order.setVersion(0);
        order.setConsignee(new Consignee("abc", "abc", "13850912276"));
        order.setOrderItems(Lists.newArrayList(item2, item));

        Aggregate<Order> aggregate = AggregateFactory.createAggregate(order);

        order.setConsignee(new Consignee("abc", "abc", "13850912277"));

        OrderPO newOrderPO = OrderFactory.convert(aggregate.getRoot());
        OrderPO oldOrderPO = OrderFactory.convert(aggregate.getSnapshot());
        System.out.println(ObjectComparator.findChangedFields(newOrderPO, oldOrderPO));
        item2.setPrice(50L);
        item2.setGoodsName("asdfsd");
        order.setOrderItems(Lists.newArrayList(item2, item, item3));

        List<ChangedEntity<OrderItemPO>> list = ObjectComparator.findChangedEntities(
                OrderFactory.convert(aggregate.getRoot().getOrderItems()),
                OrderFactory.convert(aggregate.getSnapshot().getOrderItems()),
                OrderItemPO::getId
        );
        System.out.println(list);
        list.forEach(entity -> {
            System.out.println("变更的实体属性：" + ObjectComparator.findChangedFields(entity.getNewEntity(), entity.getOldEntity()));
        });


        List<OrderItemPO> list2 = ObjectComparator.findNewEntities(
                OrderFactory.convert(aggregate.getRoot().getOrderItems()),
                OrderFactory.convert(aggregate.getSnapshot().getOrderItems()),
                OrderItemPO::getId
        );
        System.out.println("新增的实体：" + list2);
        System.out.println(aggregate.isChanged());
        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 1; i++) {
            DiffResult<Order> diffs = aggregate.getRoot().diff(aggregate.getSnapshot());
            System.out.println(diffs.getDiffs().get(0).getType());
            System.out.println(diffs.getDiffs().get(0).getFieldName());
            System.out.println(diffs.getDiffs().get(0).getLeft());
            System.out.println(diffs.getDiffs().get(0).getRight());
            System.out.println(diffs.getDiffs().get(0).getKey());
            System.out.println(diffs.getDiffs().get(0).getValue());
        }
        System.out.println(System.currentTimeMillis());

    }
}
