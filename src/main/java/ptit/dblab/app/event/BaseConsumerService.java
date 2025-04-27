package ptit.dblab.app.event;

import java.util.Map;

public abstract class BaseConsumerService<T> {

  public abstract void receive(T data);

  public abstract void receive(T data, Map<String, Object> headers);
}
