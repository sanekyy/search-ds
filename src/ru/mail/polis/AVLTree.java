package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    class Node {

        Node(E value) {
            this.value = value;
        }

        E value;
        int height;
        Node left;
        Node right;
        Node parent;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (parent != null) {
                sb.append(", p=").append(parent);
            }
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append("h=").append(height);
            sb.append('}');
            return sb.toString();
        }
    }


    private Node root;
    private final static int K = 1; //for AVLTree
    private int size;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }


    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.value;
    }

    @Override
    public E last() {
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
    public int size() {
        return size;
    }

    public E root() {
        return root.value;
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
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }

    public int getHeight() {
        return getHeight(root);
    }

    private int getHeight(Node node) {
        if (node == null) return 0;
        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    public Node find(E value) {
        return find(root, value);
    }

    private Node find(Node curr, E val) {
        if (curr == null || curr.value.equals(val)) {
            return curr;
        }
        if (curr.value.compareTo(val) > 0) {
            return find(curr.left, val);
        } else {
            return find(curr.right, val);
        }
    }


    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            root = new Node(value);
            root.height = 1;
        } else {
            Node curr = root;
            while (true) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return false;
                } else if (cmp < 0) {
                    if (curr.right != null) {
                        curr = curr.right;
                    } else {
                        curr.right = new Node(value);
                        curr.right.parent = curr;
                        curr.right.height = 1;
                        break;
                    }
                } else if (cmp > 0) {
                    if (curr.left != null) {
                        curr = curr.left;
                    } else {
                        curr.left = new Node(value);
                        curr.left.parent = curr;
                        curr.left.height = 1;
                        break;
                    }
                }
            }
            balancingAdd(curr);
        }
        size++;


        return true;
    }


    public void balancingAdd(Node curr) {
        int dh;
        while (true) {
            dh = diff(curr);
            if (dh == 0) break;
            else if (Math.abs(dh) == 1) {
                curr.height++;
            } else if (Math.abs(dh) == 2) {
                rotate(curr, dh);
                curr = curr.parent;
            }
            if (curr.parent != null) curr = curr.parent;
            else {
                root = curr;
                break;
            }
        }

    }

    public void rotate(Node a, int dh) {
        Node b;
        int diffB;
        if (dh == -2) {
            b = a.right;
            diffB = diff(b);
            if (diffB != 1) rotateLeft(a);
            else bigRotateLeft(a);
        } else if (dh == 2) {
            b = a.left;
            diffB = diff(b);
            if (diffB != -1) rotateRight(a);
            else bigRotateRight(a);
        }
    }

    public int diff(Node b) {
        int dh;
        if (b.left == null && b.right == null) dh = 0;
        else if (b.left == null) dh = -b.right.height;
        else if (b.right == null) dh = b.left.height;
        else {
            dh = b.left.height - b.right.height;
        }
        return dh;
    }


    public void rotateLeft(Node a) {
        Node b = a.right;
        int cmp;
        a.right = b.left;
        if (b.left != null) b.left.parent = a;
        b.left = a;
        cmp = compare(a.value, root.value);
        if (cmp == 0) {
            root = b;
            root.parent = null;
        } else {
            b.parent = a.parent;
            if (a.parent.left != null) {
                cmp = compare(a.parent.left.value, a.value);
                if (cmp == 0) a.parent.left = b;
                else a.parent.right = b;
            } else if (a.parent.right != null) a.parent.right = b;
        }
        a.parent = b;
        a.height = setHeight(a);
        b.height = setHeight(b);
    }

    public int setHeight(Node a) {
        int h;
        if (a.left != null && a.right != null) {
            h = Math.max(a.left.height, a.right.height) + 1;
        } else {
            h = Math.abs(diff(a)) + 1;
        }
        return h;
    }

    public void bigRotateLeft(Node a) {
        rotateRight(a.right);
        rotateLeft(a);
    }

    public void rotateRight(Node a) { /****/
        Node b = a.left;
        int cmp;
        a.left = b.right;
        if (b.right != null) b.right.parent = a;
        b.right = a;
        cmp = compare(a.value, root.value);
        if (cmp == 0) {
            root = b;
            root.parent = null;
        } else {
            b.parent = a.parent;
            if (a.parent.left != null) {
                cmp = compare(a.parent.left.value, a.value);
                if (cmp == 0) a.parent.left = b;
                else a.parent.right = b;
            } else if (a.parent.right != null) a.parent.right = b;
        }
        a.parent = b;
        a.height = setHeight(a);
        b.height = setHeight(b);
    }

    public void bigRotateRight(Node a) {
        rotateLeft(a.left);
        rotateRight(a);
    }


    public void balancingRemove(Node curr) {
        int dh;
        while (true) {
            dh = diff(curr);
            if (Math.abs(dh) == 1) break;
            else if (dh == 0) {
                curr.height--;
            } else if (Math.abs(dh) == 2) {
                rotate(curr, dh);
                curr=curr.parent;
            }
            if (curr.parent != null) curr = curr.parent;
            else {
                root = curr;
                break;
            }
        }

    }


    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            return false;
        }
        Node parent = root;
        Node curr = root;
        Node pRemote; //parent of remote node
        int cmp;
        while ((cmp = compare(curr.value, value)) != 0) {
            parent = curr;
            if (cmp > 0) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
            if (curr == null) {
                return false; // ничего не нашли
            }
        }
        if (curr.left != null && curr.right != null) {
            Node next = curr.right;
            Node pNext = curr;
            while (next.left != null) {
                pNext = next;
                next = next.left;
            } //next = наименьший из больших
            curr.value = next.value;
            next.value = null;
            //у правого поддерева нет левых потомков
            if (pNext == curr) {
                curr.right = next.right;
            } else {
                pNext.left = next.right;
            }
            if (next.right != null) next.right.parent = pNext;

            pRemote = pNext;
            next.right = null;
        } else {
            pRemote = parent;
            if (curr.left != null) {
                reLink(parent, curr, curr.left);
            } else if (curr.right != null) {
                reLink(parent, curr, curr.right);
            } else {
                reLink(parent, curr, null);
            }
        }
        if (root != null) balancingRemove(pRemote);
        size--;
        return true;
    }

    private void reLink(Node parent, Node curr, Node child) {
        if (parent == curr) {
            if (child != null && child.parent != null) child.parent = null;
            root = child;
        } else if (parent.left == curr) {
            parent.left = child;
            if (child != null && child.parent != null) child.parent = parent;
        } else {
            parent.right = child;
            if (child != null && child.parent != null) child.parent = parent;
        }
        curr.value = null;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }


    public static void main(String[] args) {
        Random random = new Random();
        int LEN = 100;
        int value;
        ISortedSet<Integer> set = new AVLTree<>();

        for (int i = 0; i < 10; i++) {
            value = i; //(random.nextInt(1000));
            System.out.print(i + ". " + value);
            System.out.println(": " + set.add(value) + ", " + set.contains(value));
        }

//        for (int value = 0; value < LEN; value++) {
//            set.add(value);
//        }
//        for (int value = LEN; value >= 0; value--) {
//            System.out.println(value + ": " + set.contains(value)
//                    + ", " + set.remove(value) + ", " + set.contains(value));
//        }

    }
}
