
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Linkedlist list = new Linkedlist();
        list.pushFront("Alice");
        list.pushFront("Bob");
        list.pushFront("Mathew");
        list.pushFront("Mark");
        list.printHead2Tail();
    }
}
