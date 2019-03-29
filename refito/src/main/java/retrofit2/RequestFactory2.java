package retrofit2;

final class RequestFactory2 {
    private RequestFactory requestFactory;
    private Object[] args;
    private String chunk;
    private Converter converter;

    public static RequestFactory2 create(RequestFactory requestFactory, Object[] args, String chunk, Converter converter) {
        return new RequestFactory2(requestFactory, args, chunk, converter);
    }

    private RequestFactory2(RequestFactory requestFactory, Object[] args, String chunk, Converter converter) {
        this.requestFactory = requestFactory;
        this.args = args;
        this.chunk = chunk;
        this.converter = converter;
    }

    public Converter getConverter() {
        return converter;
    }

    public Object[] getArgs() {
        return args;
    }

    public RequestFactory getRequestFactory() {
        return requestFactory;
    }

    public String getChunk() {
        return chunk;
    }
}
