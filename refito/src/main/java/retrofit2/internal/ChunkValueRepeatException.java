package retrofit2.internal;

import retrofit2.http.Chunk;

public class ChunkValueRepeatException extends RuntimeException {
  public ChunkValueRepeatException(Chunk chunk) {
    super("the value of @Chunk is " + chunk.value() + " can only use once");
  }
}
