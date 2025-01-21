public class Stack {
    Node[] arr; // regular array
    int capacity;
    int size;

    public Stack(int cap){
        this.arr=new Node[cap];
        this.capacity = cap;
    }
    
    public void push(Node node){
        if (!isFull()){
            // do something
            arr[size]=node;
            size++;
        }else{
            System.out.println("Stack Overflow!!!");
        }
    }
    public Node pop(){
        if (!isEmpty()){
            // do something
            Node temp =arr[size-1];
            arr[size-1]=null;
            size--;
            return temp;
        }else{
            System.out.println("Stack Overflow!!!");
            return null; // fix this (out of place)
        }
        
    }
    public boolean isFull(){
        return size==capacity;
    }
    public boolean isEmpty(){
        return size==0; // fix this
    }
    
    public void printStack(){
        if (!isEmpty()) {
            System.out.print("[Bottom] ");
            // do something here
            for(int i =0;i<size;i++){
                System.out.print(arr[i].data+" ");
            }
            System.out.println("[Top]");
        } else {
            System.out.println("Empty Stack!!!");
        }
    }
}
