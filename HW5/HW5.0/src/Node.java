
public class Node extends BTreePrinter{
    Node left;
    Node right;
    int data;

    public Node(int data) {
        this.data=data;
    }

    public void printTree() {
        // uncomment the following line and fix the errors
        super.printTree(this);
    }

    public void printBFT() {
        Queue q = new Queue(50);
        System.out.print("BFT node sequence [ ");
        Node current = this;
        q.enqueue(current);
        while(!q.isEmpty()){
            current = q.dequeue();
            System.out.print(current.data+"");
            if(current.left!=null){
                q.enqueue(current.left);
            }
            if(current.right!=null){
                q.enqueue(current.right);
            }
        }
        System.out.println("]");
    }

    public void printDFT() { // PreOrder
        Stack s = new Stack(50);
        System.out.print("DFT node sequence [ ");
        Node root = this;
        s.push(root);
        while(!s.isEmpty()){
            root = s.pop();
            System.err.print(root.data+" ");
            if(root.right!=null){
                s.push(root.right);
            }
            if(root.left!=null){
                s.push(root.left);
            }
        }
        System.out.println("]");
    }
}
