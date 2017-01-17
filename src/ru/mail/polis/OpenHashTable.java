package ru.mail.polis;

import java.util.Comparator;
import java.util.Random;

//TODO: write code here
public class OpenHashTable<T extends Comparable<T>> implements ISet<T> {

    private final Entity REMOVED = new Entity<T>(null);



    private Entity<T>[] table;

    private int size;

    private int capacity = 8;
    private double loadFactor = 0.5;
    private int threshold;

    private Comparator<T> comparator;

    public OpenHashTable() {
        this(null);
    }

    public OpenHashTable(Comparator<T> comparator) {
        this.comparator = comparator;
        table = (Entity<T>[]) new Entity[capacity];
        threshold = (int) (capacity* loadFactor);
    }

    private int compare(T v1, T v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
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
        int currIndex;

        for(int i=0; i<table.length; i++){
            if(table[currIndex = indexFor(value, i)]==null){
                return false;
            } else if(table[currIndex]!=REMOVED &&
                    (table[currIndex].value == value || compare(table[currIndex].value, value) == 0)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(T value) {
        if(insert(new Entity<>(value))) {
            if (++size >= threshold) {
                resize();
            }
            return true;
        }
        else
            return false;
    }

    private boolean insert(Entity<T> entity) {
        int currIndex = 0;

        for(int i=0; true; i++){
            System.out.println(currIndex);
            if(table[currIndex = indexFor(entity.value, i)]==null || table[currIndex] == REMOVED){
                table[currIndex] = entity;
                return true;
            } else if(table[currIndex]!=REMOVED &&
                    (table[currIndex].value == entity.value || compare(table[currIndex].value, entity.value) == 0)){
                return false;
            }
        }
    }

    private void resize() {
        capacity = capacity << 1;
        threshold = (int) (capacity*loadFactor);
        Entity<T>[] oldTable = table;
        table = (Entity<T>[]) new Entity[capacity];
        for (Entity<T> item : oldTable) {
            if(item==null || item==REMOVED)
                continue;

            insert(item);
        }
    }

    @Override
    public boolean remove(T value) {
        int currIndex;

        for(int i=0; i<table.length; i++){
            if(table[currIndex = indexFor(value, i)]==null){
                return false;
            } else if(table[currIndex]!=REMOVED &&
                    (table[currIndex].value == value || compare(table[currIndex].value, value) == 0)){
                table[currIndex] = REMOVED;
                return true;
            }
        }
        return false;
    }

    private int indexFor(T value, int attempt){
        return (value.hashCode() + attempt) % capacity;
    }

    class Entity<T> {
        T value;
        Entity(T value){
            this.value = value;
        }
    }

    public static void main(String[] args) {
        ISet<Integer> tree = new OpenHashTable<>();

        tree.add(10);
        tree.add(5);
        tree.add(15);
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
        tree = new BinarySearchTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
    }
}
