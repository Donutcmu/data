public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Array db =new Array(10);
        db.addStudent(new Student("Mathew","5906001",3.5));
        db.addStudent(new Student("Mark","5906002",2.75));
        db.addStudent(new Student("Luke","5906003",3.0));
        db.addStudent(new Student("John","5906004",3.75));
        db.addStudent(new Student("Jame","5906005",3.25));
        Student s =db.findTopStudent();
        System.out.println("The top student is "+s.name);
    }
}
