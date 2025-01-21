public class Queue {
    Node[] arr; // circular Queue
    int capacity;
    int front;
    int back;
    int size;
    
    public Queue(int cap){
        this.arr=new Node[cap];
        this.capacity = cap;
    }
    
    public void enqueue(Node node){
        if (!isFull()){
            // do something
            arr[0]=node;
            front = 0;
            back=0;
            size=1;
        }else{
            System.out.println("Queue Overflow!!!");
        }
    }
    
    public Node dequeue(){
        
        if (!isEmpty()){
            // do something
            Node temp =arr[front];
            arr[front] = null;
            front++;
            size--;
            return temp;
            
        }else{
            System.out.println("Queue Underflow!!!");
            return null; // fix this (out of place)
        }
        
    }
    
    public boolean isEmpty(){
        return size==0;
    }
    
    public boolean isFull(){
        return size==capacity;
    }
    
    public void printCircularIndices(){
        System.out.println("Front index = " + front + " Back index = " + back);
    }
    
    public void printQueue(){
        if (!isEmpty()){
            System.out.print("[Front] ");
            // do something here
            for(int i =front;i<=back;i++){
                System.out.print(arr[i].data+"");
            }
            System.out.println("[Back]");
        }else{
            System.out.println("Empty Queue!!!");
        }
    }
}
