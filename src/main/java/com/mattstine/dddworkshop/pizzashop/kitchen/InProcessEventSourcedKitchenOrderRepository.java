package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.adapters.InProcessEventSourcedRepository;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;

import java.util.HashMap;
import java.util.Map;

final class InProcessEventSourcedKitchenOrderRepository extends InProcessEventSourcedRepository<KitchenOrderRef, KitchenOrder, KitchenOrder.OrderState, KitchenOrderEvent, KitchenOrderAddedEvent> implements KitchenOrderRepository {

    private final Map<OnlineOrderRef, KitchenOrderRef> onlineOrderRefToKitchenOrderRef = new HashMap<>();

    InProcessEventSourcedKitchenOrderRepository(EventLog eventLog, Topic topic) {
        super(eventLog,
                KitchenOrderRef.class,
                KitchenOrder.class,
                KitchenOrder.OrderState.class,
                KitchenOrderAddedEvent.class,
                topic);

        eventLog.subscribe(new Topic("kitchen_orders"), e -> {
           if (e instanceof KitchenOrderAddedEvent) {
               onlineOrderRefToKitchenOrderRef.put(
                       ((KitchenOrderAddedEvent) e).getState().getOnlineOrderRef(),
                       ((KitchenOrderAddedEvent) e).getRef());
           }
        });
    }

    @Override
    public KitchenOrder findByOnlineOrderRef(OnlineOrderRef onlineOrderRef) {
        KitchenOrderRef ref = onlineOrderRefToKitchenOrderRef.get(onlineOrderRef);
        if (ref != null) {
            return this.findByRef(ref);
        }
        return null;
    }
}
