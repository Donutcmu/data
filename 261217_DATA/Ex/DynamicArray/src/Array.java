public class Array {
    int size;
    int cap;
    Student[] arr;
    
    public Array(int cap){ 
        size = 0;
        this.cap = cap;
        arr = new Student[cap];
    }
    public Student findTopStudent(){
        Student theTop =arr[0];
        for(int i=0;i<size;i++){
            if(arr[i].gpa>theTop.gpa){
                theTop=arr[i];
            }
        }
       return theTop;
    }
    public void addStudent(Student s){
        arr[size]=s;
        size++;
    }
}
