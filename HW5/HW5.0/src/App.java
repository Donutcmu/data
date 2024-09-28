public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Node tree = constructTree1();
        tree.printTree();
    }
    public static Node constructTree1(){
        Node root = new Node(3); // Fix this
    //สร้างnode root ขึ้นมาสำหรับใช้เป็นrootโดยมีdata=3
    // do something
    root.left = new Node(7);
    //สร้างnodeที่มีdata=7เพื่อนำไปใส่ทางขวาของroot
    root.right = new Node(5);
    //สร้างnodeที่มีdata=5เพื่อนำไปใส่ทางซ้ายของroot
    root.left.left = new Node(2);//สร้างnodeที่มีdata=2ไปต่อทางซ้ายของnodeที่มีค่า=7
    root.right.right = new Node(9);//สร้างnodeที่มีdata=9ไปต่อทางซ้ายของnodeที่มีค่า=5
    root.right.right.left = new Node(4);//สร้างnodeที่มีค่า=4ไปต่อnodeที่มีค่า=9 ซึ่งnode4จะกลายเป็นleaf node
    root.left.right = new Node(6);//สร้างnodeที่มีค่า=6ไปต่อทางขวาของnode 7 
    root.left.right.right = new Node(8);//สร้างnodeที่มีค่า=8ไปต่อทางขวาของnodeที่มีค่า=6ซึ่งnode8จะกลายเป็นleaf node
    root.left.right.left = new Node(1);//สร้างnodeที่มีค่า=1ไปต่อทางซ้ายของnodeที่มีค่า=6ซึ่งnode1จะกลายเป็๋นleafnodeเท่านี้ต้นไม้ก็สมบูรณ์
    return root;
    }
    public static Node constructTree2(){
        Node root = new Node(1); // Fix this
        //สร้างroot node ที่มีค่า=1
       // do something
       root.left = new Node(2);//เพิ่มnodeที่มีdata=2ไปทางซ้าย
       root.right = new Node(3);//เพิ่มnodeที่มีdata=3ไปทางขวา
       
       root.left.right = new Node(5);//เพิ่มnodeที่มีdata=5ไปทางขวาของnodeที่มีdata=2
       root.right.right = new Node(6);//เพิ่มnodeที่มีdata=6ไปทางขวาต่อจากnodeที่มีdata=3
       root.right.right.left = new Node(9);//เพิ่มnodeที่มีdata=9ไปทางซ้ายต่อจากnodeที่มีdata=6ทำให้nodeนี้เป็นleaf node
       root.left.left = new Node(4);//เพิ่มnodeที่มีค่า=4ไปทางซ้ายของnodeที่มีค่า=2 ซึ่งnodeนี้เป็นleaf node
       root.left.right.left = new Node(7);//เพิ่มnodeที่มีค่า=7ไปทางซ้ายของnodeที่มีค่าเท่ากับ5nodeนี้เป็นleaf node
       root.left.right.right = new Node(8);//เพิ่มnodeที่มีค่า=8ไปทางขวาของnodeที่มีค่า=5
       root.left.right.right.right = new Node(10);//เพิ่มnodeที่มีค่า=10ไปทางขวาต่อจากnodeที่มีค่า=8และnodeนี้เป็นleaf node
       
       return root;
   }
}
