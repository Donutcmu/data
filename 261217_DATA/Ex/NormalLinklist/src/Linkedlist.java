public class Linkedlist {
    Node head;
    
    public void pushFront(String key){
        Node node = new Node(key);
        if(head == null){
            head = node;
            
        }else{
            node.next = head;
            head = node;
        }
    }
    public void printHead2Tail(){
        for(Node current = head;current!=null;current=current.next){
            System.out.println(current.key+", ");
        }
        System.out.println("");
    }
}
