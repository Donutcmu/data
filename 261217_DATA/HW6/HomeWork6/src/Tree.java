public class Tree  extends BTreePrinter{
    Node root;
    
  public Tree(Node root){
      this.root = root;
  }
  
  public Tree(){} // Dummy constructor (No need to edit)
  
  public void printTree(){
      //super.printTree(root);
      //System.out.println("Empty tree!!!");
      if(root != null)
        super.printTree(root);
        //เช็คว่าtreeนี้มีว่างรึป่าวโดยการเช็คที่root
        //ถ้ามี ทำการprintTree ออกมา
      else
        System.out.println("Empty tree!!!");
        //ไม่มีเป็นtreeว่าง ให้print "Empty tree!!!"
  }

  public static void printNode(Node node){
      //System.out.println("Node not found!!!");
      if(node == null)
      {
          System.out.println("Node not found!!!");
        //nodeนี้ไม่มีอยู่ print "Node not found!!!"
      }
      else
      {
          System.out.println(node.key);
          //print key ของnode
      }
  }
      
  public Node findKey(int search_key){
    Node current = root;
    return findKey(current,search_key); // Call the recursive version
  }
  
  public static Node findKey(Node node, int search_key){
      // this function should be recursive
      // You should check null in this function
      if(node == null) 
      {
          return null;
          //ถ้าnodeว่างจะทำการreturn nullกลับไป
      }
      //
      if(node.key == search_key) 
      {
          return node;
          //ถ้าเจอnodeที่มีkey=search_keyแล้วจะreturn nodeนี้ออกไป
      }
      if(node.key > search_key)  
      {
          return findKey(node.left, search_key);
          //ถ้าkeyของตัวนี้มีค่ามากกว่าsearch_keyให้เรียกตัวเองอีกครั้งโดยnodeที่ใส่เข้าไปจะเป็นnodeทางซ้าย
          //เพราะจะได้เจอกับnodeที่มีkeyน้อยลง
      } 
      else
      {
          return findKey(node.right, search_key);
          //ถ้าkeyของตัวนี้มีค่าน้อยกว่าsearch_keyให้เรียกตัวเองอีกครั้งโดยnodeที่ใส่เข้าไปจะเป็นnodeขวา
          //เพราะจะได้เจอกับnodeที่มีkeyมากกว่าkeyปัจจุบัน
      }
  }
  
  
  public Node findMin(){
    
    return findMin(root); // Call the recursive version
  }
  
  public static Node findMin(Node node){
      // this function should be recursive
      if(node.left != null)
        {
            return findMin(node.left);
            //ถ้าลูกทางซ้ายของtreeยังมีอยู่ก็ให้เรียกใช้ตัวเองต่อ
            //ที่ต้องเรียกทางซ้ายไปเรื่อยๆก็เพราะตามBTแล้วค่าที่น้อยที่สุดจะอยู่ทางซ้าย

        }
      else
      {
          return node;
          //ถ้าลูกของnodeทางซ้าย=null ก็จะทำการreturn nodeนี้ออกไปเพราะnodeนี้คือตัวที่อยู่ท้ายสุดแล้ว
      }
      // this function should be recursive
  }
  
  public Node findMax(){
    Node current = root;
    return findMax(current); // Call the recursive version
  }
  
  public static Node findMax(Node node){
      // this function should be recursive
      if(node.right != null)
      {
          return findMax(node.right);
          //ถ้าลูกทางขวาของnodeปัจจุบันยังมีอยู่ก็ให้เรียกใช้ฟังก์ชันนี้อีกรอบโดยใส่nodeลูกของทางขวาของnodeปัจจุบันเข้าไป
          //ต้องเรียกไปทางขวาเรื่อยๆก็เพราะว่าจามBTแล้วค่าที่มากที่สุดจะอยู่ทางขวา
      }
      else{
        return node;
          //หมายความว่าตัวนี้คือตัวที่อยู่ขวาสุดแล้วมีค่ามากที่สุดแล้วให้return nodeนี้กลับไป
      }
  }
  
  public Node findClosestLeaf(int search_key){
    Node current = root;
    return findClosestLeaf(current,search_key); // Call the recursive version
  }
  
  public static Node findClosestLeaf(Node node, int search_key){
      // this function should be recursive
      if (node == null) {
        return null;
        //ถ้าnodeนี้เป็นnodeว่างให้return null
    }
    
    Node closetLeaf = node;
    //กำหนดให้nodeที่ใกล้เคียงมากที่สุดเป็นnodeปัจจุบันที่รับเข้ามา
    if(node.left == null || node.right == null)
    {
        closetLeaf = node;
        //ถ้าnodeนี้ไม่มีลูกก็คือnodeนี้เป็นnodeใบแล้ว nodeที่มีค่าใกล้ที่สุด ก็คือnodeนี้แหละ
    }
    
    if(node.left != null && node.key > search_key)
    {
        return findClosestLeaf(node.left,search_key);
        //ถ้าnodeนี้มีลูกทางซ้ายอยู่ และkeyของnodeปัจจุบัน>search_keyแล้วให้เรียกตัวเองอีกทีโดยจะลงไปทางซ้ายเพื่อตามหาnodeที่มีkeyน้อยลง
    }
    else if(node.right != null && node.key < search_key)
    {
        return findClosestLeaf(node.right,search_key);
        //ถ้าnodeนี้มีลูกทางขวาอยู่ และkeyของnodeปัจจุบัน>search_keyแล้วให้เรียกตัวเองอีกทีโดยจะลงไปทางขวาเพื่อตามหาnodeที่มีkeyมากขึ้น
    }
      // this function should be recursive
    return closetLeaf;
  }
  
  public Node findClosest(int search_key){
      // Please use while loop to implement this function
      // Try not to use recursion
      Node current, closest;//ทำการสร้างnodeขึ้นมาใหม่2ตัว เพื่อเก็บค่าnodeปัจจุบันและnodeที่มีค่าใกล้ที่สุด
      closest = current = root;//โดยให้เริ่มที่root node
      int min_diff = Integer.MAX_VALUE;
      while(current != null)
      {
          int diff = Math.abs(current.key - search_key);
          //สร้างตัวแปรdiffโดย ให้มีค่าผลต่างของkeyของnodeปัจจุบันกับsearch_key
          if(diff < min_diff)
          {
              min_diff = diff;
              closest = current;
              //diff<min_diff แปรว่าเจอตัวใหม่ที่มีค่าใกล้กว่าค่าปัจจุบัน ดังนั้นmin_diffจึงเท่ากับdiffปัจจุบันแล้วต้องให้nodeปัจจุบัน เป็นnodeที่มีค่าใกล้ที่สุด
          }
          if(current.key > search_key)
          {
              current = current.left;
              //ถ้าkeyของnodeปัจจุบัน >keyที่หา ให้ลงไปทางซ้ายเพื่อหาkeyที่น้อยกว่า
          }
          else if(current.key < search_key)
          {
              current = current.right;
              //ถ้าkeyของnodeปัจจุบัน <keyที่หา ให้ลงไปทางขวาเพื่อหาkeyที่มากกว่า
          }
          else
          {
              break;
              //เงื่อนไขนี้จะทำหากเจอnodeที่มีkey=search_key แปลว่าnodeนี้ใกล้เคียงที่สุดในออกลูปเลย
          }
      }
      
      // Use while loop to traverse from root to the closest leaf
      
      return closest;
  }
  
  // Implement this function using the findClosestLeaf technique
  // Do not implement the recursive function
  public void insertKey(int key) {
      // Implement insertKey() using the non-recursive version
      // This function should call findClosestLeaf()
      Node InsertRoot = findClosestLeaf(root,key);
        //เรียกใช้ฟังก์ชันfindClosestLeaf แล้วจะได้InsertRoot=closetLeaf
      if(InsertRoot == null)
      {
          root = new Node(key);
          return;
          //ถ้าหาinsertRootไม่เจอให้สร้างnodeใหม่เลย แล้วreturn
      }
      
      if(InsertRoot.key == key)
      {
          System.out.println("Duplicated key!!!");
          return;
          //ถ้ามีkeyตัวนั้นอยู่แล้วให้return แล้วprint "Duplicated key!!!"
      }
      
      
      if(InsertRoot.key > key)
      {
         InsertRoot.left = new Node(key);
         //ถ้าinsertRoot.key มีมากค่ามากกว่าkeyให้สร้างnodeใหม่เป็นลูกทางซ้ายของinsertRootโดยมีnode.key=key
      }
      if(InsertRoot.key < key)
      {
          InsertRoot.right = new Node(key);
          //ถ้าinsertRoot.key มีมากค่ามากกว่าkeyให้สร้างnodeใหม่เป็นลูกทางซ้ายของinsertRootโดยมีnode.key=key
      }
  }
  
  public void printPreOrderDFT(){
      System.out.print("PreOrder DFT node sequence [ ");
      // Call the recursive version
      System.out.println("]");
  }
  
  public static void printPreOrderDFT(Node node){
      // this function should be recursive   
  }
  
  public void printInOrderDFT(){
      System.out.print("InOrder DFT node sequence [ ");
      // Call the recursive version
      System.out.println("]");
  }
  
  public static void printInOrderDFT(Node node){
      // this function should be recursive  
  }
  
  public void printPostOrderDFT(){
      System.out.print("PostOrder DFT node sequence [ ");
      // Call the recursive version
      System.out.println("]");
  }
  
  public static void printPostOrderDFT(Node node){
      // this function should be recursive 
  }
  
  public static int height(Node node){
      // Use recursion to implement this function
      // height = length(path{node->deepest child})
      return -2;
  }
  
  public static int size(Node node){
      // Use recursion to implement this function
      // size = #children + 1(itself)
      return -2;
  }
  
  public static int depth(Node root, Node node){
      // Use recursion to implement this function
      // Similar to height() but start from node, go up to root
      // depth = length(path{node->root})
      return -2;

  }
  
  public int treeHeight(){ // Tree height
      // Hint: call the static function
      return -2;
  }
  
  public int treeSize(){ // Tree size
      // Hint: call the static function
      return -2;
  }
  
  public int treeDepth(){ // Tree depth
      // Hint: call the static function
      return -2;
  }
  
  public Node findKthSmallest(int k){
      return null; // Call the recursive version
  }
  
  public static Node findKthSmallest(Node node, int k){
      // this function should be recursive
      return null;
  }
  
  public static Node findNext(Node node){
      //this function should call other functions
      return null;
  }
  
  public static Node leftDescendant(Node node){// Case 1 (findMin)
      // this function should be recursive
      return null;
  }
  
  public static Node rightAncestor(Node node) {// Case 1 (first right parent)
      // this function should be recursive
      return null;
  }
  
  public List rangeSearch(int x, int y){
      // This function utilizes findCloest() and findNext()
      // Use List list append(node) to add node to the list
      // List is the static Array
      return new List(100);
  }
  
       
  // This function is for deleting the root node
  // If the node is not the root, please call the recursive version
  public void deleteKey(int key) {
      // There should be 6 cases here
      // Non-root nodes should be forwarded to the static function
  }

  // Use this function to delete non-root nodes
  public static void deleteKey(Node node){
      // There should be 7 cases here
  }
}
