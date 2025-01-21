public class Linkedlist {
    Student head;
    
    public void addStudent(String name,String student_id,double gpa){
        Student newStudent = new Student(name,student_id,gpa);
        if(head == null){
            head = newStudent;
            
        }else{
            newStudent.next = head;
            head = newStudent;
        }
    }
    public Student findTopStudent(){
        Student topStudent = head;
        Student current = head;
        while(current!=null){
            if(current.gpa>topStudent.gpa){
                topStudent = current;
            }
            current=current.next;
        }
        return topStudent;
    }
}
