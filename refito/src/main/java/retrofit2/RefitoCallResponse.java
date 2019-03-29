package retrofit2;

public final class RefitoCallResponse {
  private final String chunk;
  private final Object body;

  private RefitoCallResponse(String chunk, Object body) {
    this.chunk = chunk;
    this.body = body;
  }

  static RefitoCallResponse create(String key, Object value) {
    return new RefitoCallResponse(key, value);
  }

  public Object getBody() {
    return body;
  }

  public String getChunk() {
    return chunk;
  }
}
