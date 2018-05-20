import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelloWorldTest {
    
    @Test
    void getHello() {
        assertEquals("Hello", HelloWorld.getInstance().getHello());
    }
    
    @Test
    void getHello1() {
        HelloWorld.main(null);
    }
}
