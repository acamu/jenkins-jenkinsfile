public class HelloWorld {
     /**
     * Instance unique pré-initialisée
     */
    private static HelloWorld INSTANCE = new HelloWorld();
    
    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal
        System.out.println("Hello, World");
    }

    /**
     * Constructeur privé
     */
    private HelloWorld() {
    }

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    public static HelloWorld getInstance() {
        return INSTANCE;
    }

    public String getHello() {
        return "Hello";
    }
}
