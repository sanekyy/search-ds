package ru.mail.polis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static ru.mail.polis.RedBlackTree.Colors.BLACK;
import static ru.mail.polis.RedBlackTree.Colors.RED;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {

    enum Colors{
        RED, BLACK;
    }

    class Node {
        E value;
        Colors color;
        Node parent;
        Node left;
        Node right;

        Node(E value) {
            this.value = value;
            parent=nil;
            left=nil;
            right=nil;
            color=BLACK;
        }


        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }
    private Node root;
    private Node nil;
    private int size;
    private final Comparator<E> comparator;

    public RedBlackTree() {
        this.comparator = null;
        nil=new Node(null);
        nil.parent=null;
        nil.left=null;
        nil.right=null;
        nil.color=BLACK;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    private Node TreeMinimum(Node x){
        while(x.left!=nil)
            x=x.left;
        return x;
    }

    private void LeftRotate(Node x){
        Node y=x.right;
        x.right=y.left;
        if(y.left!=nil)
            y.left.parent=x;
        y.parent=x.parent;
        if(x.parent==nil)
            this.root=y;
        else if(x==x.parent.left)
            x.parent.left=y;
        else x.parent.right=y;
        y.left=x;
        x.parent=y;
    }

    private void RightRotate(Node x){
        Node y=x.left;
        int s;
        x.left=y.right;
        if(y.right!=nil)
            y.right.parent = x;
        y.parent=x.parent;
        if(x.parent==nil)
            this.root=y;
        else if(x==x.parent.right)
            x.parent.right=y;
        else x.parent.left=y;
        y.right=x;
        x.parent=y;
    }

    private void RBInsertFixup(Node z){
        while(z.parent.color==RED){
            if(z.parent==z.parent.parent.left){
                Node y=z.parent.parent.right;
                if(y.color==RED){
                    z.parent.color=BLACK;
                    y.color=BLACK;
                    z.parent.parent.color=RED;
                    z=z.parent.parent;
                } else if(z==z.parent.right){
                    z=z.parent;
                    LeftRotate(z);
                }
                if(z!=nil && z!=null)
                    z.parent.color=BLACK;
                if(z.parent.parent!=nil && z.parent.parent!=null) {
                    z.parent.parent.color = RED;
                    RightRotate(z.parent.parent);
                }
            } else {
                Node y=z.parent.parent.left;
                if(y.color==RED){
                    z.parent.color=BLACK;
                    y.color=BLACK;
                    z.parent.parent.color=RED;
                    z=z.parent.parent;
                } else if(z==z.parent.left){
                    z=z.parent;
                    RightRotate(z);
                }
                if(z!=nil && z!=null)
                    z.parent.color=BLACK;
                if(z.parent.parent!=nil && z.parent.parent!=null) {
                    z.parent.parent.color = RED;
                    LeftRotate(z.parent.parent);
                }
            }
        }
        this.root.color=BLACK;
    }

    private void RBTransplant(Node u, Node v){
        if(u.parent==nil)
            this.root=v;
        else if(u==u.parent.left)
            u.parent.left=v;
        else
            u.parent.right=v;
        v.parent=u.parent;
    }

    private void RBDeleteFixup(Node x){
        while((x!=this.root) && (x.color==BLACK)){
            if(x==x.parent.left){
                Node w=x.parent.right;
                if(w.color==RED){
                    w.color=BLACK;
                    x.parent.color=RED;
                    LeftRotate(x.parent);
                    w=x.parent.right;
                }
                if((w.left.color==BLACK) && (w.right.color==BLACK)){
                    w.color=RED;
                    x=x.parent;
                } else if(w.right.color==BLACK) {
                    w.right.color = BLACK;
                    w.color = RED;
                    RightRotate(w);
                    w = x.parent.right;
                }
                w.color=x.parent.color;
                x.parent.color=BLACK;
                w.right.color=BLACK;
                LeftRotate(x.parent);
                x=this.root;
            } else{
                Node w=x.parent.left;
                if(w.color==RED){
                    w.color=BLACK;
                    x.parent.color=RED;
                    RightRotate(x.parent);
                    w=x.parent.left;
                }
                if((w.right.color==BLACK) && (w.left.color==BLACK)){
                    w.color=RED;
                    x=x.parent;
                } else if(w.left.color==BLACK) {
                    w.left.color = BLACK;
                    w.color = RED;
                    RightRotate(w);
                    w = x.parent.left;
                }
                w.color=x.parent.color;
                x.parent.color=BLACK;
                w.left.color=BLACK;
                RightRotate(x.parent);
                x=this.root;
            }
        }
        x.color=BLACK;
    }

    private void RBDelete(Node z){
        Node x,y=z;
        Colors yCol=y.color;
        if(z.left==null){
            x=z.right;
            RBTransplant(z,z.right);
        } else if(z.right==null){
            x=z.left;
            RBTransplant(z,z.left);
        } else {
            y=TreeMinimum(z.right);
            yCol=y.color;
            x=y.right;
            if (y.parent==z)
                x.parent=y;
            else{
                RBTransplant(y,y.right);
                y.right=z.right;
                y.right.parent=y;
            }
            RBTransplant(z,y);
            y.left=z.left;
            y.left.parent=y;
            y.color=z.color;
        }
        if(yCol==BLACK)
            RBDeleteFixup(x);
    }

    @Override
    public E first() {
        return null;
    }

    @Override
    public E last() {
        return null;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == nil) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }


    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E value) {
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
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            root = new Node(value);
        } else {
            Node z = new Node(value);
            Node y = nil;
            Node x = this.root;
            int cmp;
            while (x != nil) {
                y = x;
                cmp=compare(z.value, x.value);
                if (cmp < 0)
                    x = x.left;
                else if(cmp == 0)
                    return false;
                else x = x.right;
            }
            z.parent = y;
            if (y == nil)
                this.root = z;
            else if (compare(z.value,y.value) < 0) {
                y.left = z;
            } else y.right = z;
            z.left = nil;
            z.right = nil;
            z.color = RED;
            RBInsertFixup(z);
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            return false;
        }
        Node z=root;
        Node parent=root;
        int cmp;
        while ((cmp = compare(z.value, value)) != 0) {
            parent = z;
            if (cmp > 0) {
                z = z.left;
            } else {
                z = z.right;
            }
            if (z == null) {
                return false; // ничего не нашли
            }
        }

        Node x= nil;
        Node y=z;
        Colors yCol=y.color;
        if(z.left==nil){
            x=z.right;
            RBTransplant(z,z.right);
        } else if(z.right==nil){
            x=z.left;
            RBTransplant(z,z.left);
        } else {
            y=TreeMinimum(z.right);
            yCol=y.color;
            x=y.right;
            if (y.parent==z)
                x.parent=y;
            else{
                RBTransplant(y,y.right);
                y.right=z.right;
                y.right.parent=y;
            }
            RBTransplant(z,y);
            y.left=z.left;
            y.left.parent=y;
            y.color=z.color;
        }
        if(yCol==BLACK && (x!=nil))
            RBDeleteFixup(x);
        size--;
        return true;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(4);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree.contains(10));
        System.out.println(tree.inorderTraverse());
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size);
        System.out.println(tree.inorderTraverse());
        tree.remove(5);
        System.out.println(tree.size);
        System.out.println(tree.inorderTraverse());
        tree.add(15);
        System.out.println(tree.size);
        System.out.println(tree.inorderTraverse());

        System.out.println("------------");
        Random rnd = new Random();
        tree = new RedBlackTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new RedBlackTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
    }
}
