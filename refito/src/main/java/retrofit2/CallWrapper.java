package retrofit2;

public class CallWrapper {
    private Call call;
    private String chunk;

    public CallWrapper(Call call, String chunk) {
        this.call = call;
        this.chunk = chunk;
    }

    public Call getCall() {
        return call;
    }

    public String getChunk() {
        return chunk;
    }
}
