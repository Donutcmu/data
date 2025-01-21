public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Linkedlist db =new Linkedlist();
        db.addStudent("Mathew","5906001",3.75);//ตัวแรก
        db.addStudent("Mark","5906002",2.75);
        db.addStudent("Luke","5906003",3.75);
        db.addStudent("John","5906004",3.75);
        db.addStudent("Jame","5906005",3.75);//ตัวท้ายที่เข้าไปในlinklistทำให้เงื่อนไขในwhileออกมาเป็นตัวท้ายเนื่องจาก<=
        Student s =db.findTopStudent();
        System.out.println("The top student is "+s.name);
    }
}
