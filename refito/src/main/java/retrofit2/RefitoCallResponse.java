package retrofit2;

public final class RefitoCallResponse {
  private final String key;
  private final Object value;

  private RefitoCallResponse(String key, Object value) {
    this.key = key;
    this.value = value;
  }

  static RefitoCallResponse create(String key, Object value) {
    return new RefitoCallResponse(key, value);
  }

  public Object getValue() {
    return value;
  }

  public String getKey() {
    return key;
  }
}
