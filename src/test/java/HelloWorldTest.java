import org.junit.Assert;
import org.junit.jupiter.api.Test;

class HelloWorldTest {

    @Test
    void getHello() {
        Assert.assertEquals("Hello", HelloWorld.getInstance().getHello());

    }
}