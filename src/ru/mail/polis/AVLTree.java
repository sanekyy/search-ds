package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class AVLTree<T extends Comparable<T>> implements ISortedSet<T> {

    private Node root;
    private int size;
    private final Comparator<T> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException("Set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.value;
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.value;
    }

    @Override
    public List<T> inorderTraverse() {
        List<T> list = new ArrayList<>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<T> list) {
        if (curr == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean contains(T value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != null) {
            Node curr = root;
            while (curr != null) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(T value) {
        Node newNode = new Node(value);
        if(insertAVL(this.root, newNode)){
            size++;
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(T value) {
        if(removeAVL(this.root, value)){
            size--;
            return true;
        }
        return false;
    }

    private int compare(T v1, T v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }





    private boolean insertAVL(Node parent, Node q) {
        // If node to compare is null, the node is inserted. If the root is null, it is the root of the tree.
        if(parent==null) {
            this.root=q;
        } else {

            // If compare node is smaller, continue with the left node
            if(compare(q.value, parent.value)<0) {
                if(parent.left==null) {
                    parent.left = q;
                    q.parent = parent;

                    // Node is inserted now, continue checking the balance
                    recursiveBalance(parent);
                } else {
                    insertAVL(parent.left,q);
                }

            } else if(compare(q.value,parent.value)>0) {
                if(parent.right==null) {
                    parent.right = q;
                    q.parent = parent;

                    // Node is inserted now, continue checking the balance
                    recursiveBalance(parent);
                } else {
                    insertAVL(parent.right,q);
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean removeAVL(Node currNode, T value) {
        if(currNode==null) {
            return false;
        } else {
            if(compare(value, currNode.value) < 0)  {
                removeAVL(currNode.left,value);
            } else if(compare(value, currNode.value) > 0) {
                removeAVL(currNode.right,value);
            } else if(compare(currNode.value,value)==0) {
                // we found the node in the tree.. now lets go on!
                removeFoundNode(currNode);
                return true;
            }
        }
        return false;
    }

    private void removeFoundNode(Node node) {
        Node r;
        // at least one child of q, q will be removed directly
        if(node.left==null || node.right==null) {
            // the root is deleted
            if(node.parent==null) {
                if(node.left!=null){
                    this.root = node.left;
                    this.root.parent = null;
                } else if(node.right!=null){
                    this.root =node.right;
                    this.root.parent = null;
                } else {
                    this.root = null;
                }
                return;
            }
            r = node;
        } else {
            // q has two children --> will be replaced by successor
            r = successor(node);
            node.value = r.value;
        }

        Node p;
        if(r.left!=null) {
            p = r.left;
        } else {
            p = r.right;
        }

        if(p!=null) {
            p.parent = r.parent;
        }

        if(r.parent==null) {
            this.root = p;
        } else {
            if(r==r.parent.left) {
                r.parent.left=p;
            } else {
                r.parent.right = p;
            }
            // balancing must be done until the root is reached.
            recursiveBalance(r.parent);
        }
    }

    private Node successor(Node q) {
        if(q.right!=null) {
            Node rightNode = q.right;
            while(rightNode.left!=null) {
                rightNode = rightNode.left;
            }
            return rightNode;
        } else {
            Node parent = q.parent;
            while(parent!=null && q==parent.right) {
                q = parent;
                parent = q.parent;
            }
            return parent;
        }
    }


    private void recursiveBalance(Node cur) {

        // we do not use the balance in this class, but the store it anyway
        setBalance(cur);
        int balance = cur.balance;

        // check the balance
        if(balance==-2) {

            if(height(cur.left.left)>=height(cur.left.right)) {
                cur = rotateRight(cur);
            } else {
                cur = doubleRotateLeftRight(cur);
            }
        } else if(balance==2) {
            if(height(cur.right.right)>=height(cur.right.left)) {
                cur = rotateLeft(cur);
            } else {
                cur = doubleRotateRightLeft(cur);
            }
        }

        // we did not reach the root yet
        if(cur.parent!=null) {
            recursiveBalance(cur.parent);
        } else {
            this.root = cur;
            System.out.println("------------ Balancing finished ----------------");
        }
    }


    private Node rotateLeft(Node n) {

        Node v = n.right;
        v.parent = n.parent;

        n.right = v.left;

        if(n.right!=null) {
            n.right.parent=n;
        }

        v.left = n;
        n.parent = v;

        if(v.parent!=null) {
            if(v.parent.right==n) {
                v.parent.right = v;
            } else if(v.parent.left==n) {
                v.parent.left = v;
            }
        }

        setBalance(n);
        setBalance(v);

        return v;
    }

    /**
     * Right rotation using the given node.
     *
     * @param n
     *            The node for the rotation
     *
     * @return The root of the new rotated tree.
     */
    private Node rotateRight(Node n) {

        Node v = n.left;
        v.parent = n.parent;

        n.left = v.right;

        if(n.left!=null) {
            n.left.parent=n;
        }

        v.right = n;
        n.parent = v;


        if(v.parent!=null) {
            if(v.parent.right==n) {
                v.parent.right = v;
            } else if(v.parent.left==n) {
                v.parent.left = v;
            }
        }

        setBalance(n);
        setBalance(v);

        return v;
    }

    /**
     *
     * @param u The node for the rotation.
     * @return The root after the double rotation.
     */
    private Node doubleRotateLeftRight(Node u) {
        u.left = rotateLeft(u.left);
        return rotateRight(u);
    }

    /**
     *
     * @param u The node for the rotation.
     * @return The root after the double rotation.
     */
    private Node doubleRotateRightLeft(Node u) {
        u.right = rotateRight(u.right);
        return rotateLeft(u);
    }

    private int height(Node cur) {
        if(cur==null) {
            return -1;
        }
        if(cur.left==null && cur.right==null) {
            return 0;
        } else if(cur.left==null) {
            return 1+height(cur.right);
        } else if(cur.right==null) {
            return 1+height(cur.left);
        } else {
            return 1+maximum(height(cur.left),height(cur.right));
        }
    }

    private void setBalance(Node cur) {
        cur.balance = height(cur.right)-height(cur.left);
    }

    private int maximum(int a, int b) {
        if(a>=b) {
            return a;
        } else {
            return b;
        }
    }





    public class Node {
        Node left;
        Node right;
        Node parent;
        T value;
        int balance;

        Node(T value) {
            left = right = parent = null;
            balance = 0;
            this.value = value;
        }

        public String toString() {
            return "" + value;
        }
    }

    public static void main(String[] args) {
        ISortedSet<Integer> tree = new AVLTree<>();

        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size());
        System.out.println(tree);
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size());
        System.out.println(tree);
        tree.remove(5);
        System.out.println(tree.size());
        System.out.println(tree);
        tree.add(15);
        System.out.println(tree.size());
        System.out.println(tree);

        System.out.println("------------");
        Random rnd = new Random();
        tree = new BinarySearchTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new BinarySearchTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
    }

}
