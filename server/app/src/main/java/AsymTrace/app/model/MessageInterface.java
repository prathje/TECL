package AsymTrace.app.model;

public interface MessageInterface<T> {

    byte[] toBytes();
    //static T fromBytes(byte[] bytes);
}
