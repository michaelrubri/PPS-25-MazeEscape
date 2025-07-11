package view.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    private static final List<Consumer<Object>> listeners = new ArrayList<>();

    // Registers a listener of the event
    public static void subscribe(Consumer<Object> listener) {
        listeners.add(listener);
    }

    // Notifies the event
    public static void publish(Object event) {
        listeners.forEach(listener -> listener.accept(event));
    }
}
