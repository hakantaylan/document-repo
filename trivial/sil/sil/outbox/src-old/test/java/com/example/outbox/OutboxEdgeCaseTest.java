package com.example.outbox;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.TransactionDefinition;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class OutboxEdgeCaseTest {

    @Autowired private PlatformTransactionManager txManager;
    @Autowired private EntityManager em;
    @Autowired private OrderService orderService;

    @Test
    public void testNestedTransactions() {
        TransactionTemplate tt = new TransactionTemplate(txManager);

        tt.execute(status -> {
            Order order = new Order();
            orderService.createAndShip(order); // outer tx

            TransactionTemplate nested = new TransactionTemplate(txManager);
            nested.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);
            nested.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
            nested.execute(innerStatus -> {
                Order nestedOrder = new Order();
                orderService.createAndShip(nestedOrder); // inner tx
                return null;
            });

            return null;
        });

        List<OutboxEntity> events = em.createQuery("from OutboxEntity", OutboxEntity.class).getResultList();
        assertTrue(events.size() >= 4);
    }

    @Test
    public void testDeepEntityGraph() {
        TransactionTemplate tt = new TransactionTemplate(txManager);
        tt.execute(status -> {
            Order order = new Order();
            order.addItem(new OrderItem("Item1"));
            order.addItem(new OrderItem("Item2"));

            orderService.createAndShip(order);
            return null;
        });

        List<OutboxEntity> events = em.createQuery("from OutboxEntity", OutboxEntity.class).getResultList();
        assertTrue(events.stream().anyMatch(e -> e.getEventType().equals("CREATED")));
        assertTrue(events.stream().anyMatch(e -> e.getEventType().equals("UPDATED")));
    }
}
