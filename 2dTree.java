import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import java.lang.IllegalArgumentException;
import edu.princeton.cs.algs4.Queue;



public class KdTree {
    
    private Node root;
    private int size;
    private Node nodeMin;
    private double dmin;
    
    public KdTree()                               // construct an empty set of points
    {
        size = 0;
    }
    
    private class Node
    {
        private Point2D point;
        private RectHV rec;
        private int numOfAncesters; // number of ancesters;
        // if even, compare x coordinate; if odd, compare y coordinate;
        private Node left;
        private Node right;
        
        public Node(Point2D point)
        {
            this.point = point;
            numOfAncesters = 0;
            
        }
        
        private double compareTo(Node a)
        {
            if(numOfAncesters % 2 == 0)
                return point.x() - a.point.x();
            else
                return point.y()-a.point.y();
            
        }
        
        private double compareTo(Point2D p)
        {
            if(numOfAncesters % 2 == 0)
                return point.x() - p.x();
            else
                return point.y() - p.y();
            
        }
            
    }
    
    
    
    
    public boolean isEmpty()                      // is the set empty? 
    {
        return root == null;
    }
    
    public int size()                         // number of points in the set
    {
        return size;
    }
    
    public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
    {
        if(p == null) throw new IllegalArgumentException("empty point");
        Node node = new Node(p);
        size += 1;
        if(root == null) 
        {
            root = node;
            root.rec = new RectHV(0,0,1,1);
        }
        else
        {
            Node x = root;
            Node parent = x;
            double cmp = 0;
            while(x != null)
            {
                cmp = x.compareTo(node);
                parent = x;
                if(cmp>0) x = x.left;
                else if(cmp<0) x = x.right;
                else if(x.point.equals(p))
                {
                    size -= 1;
                    return;
                }
                else x = x.right;
            }
            node.numOfAncesters = parent.numOfAncesters + 1;
            if(cmp>0) 
            {
                if(node.numOfAncesters % 2 == 0) 
                    node.rec = new RectHV(parent.rec.xmin(),parent.rec.ymin(),parent.rec.xmax(),parent.point.y());
                else node.rec = new RectHV(parent.rec.xmin(),parent.rec.ymin(),parent.point.x(),parent.rec.ymax());
                parent.left = node;
            }
            else 
            {
                if(node.numOfAncesters % 2 == 0) 
                    node.rec = new RectHV(parent.rec.xmin(),parent.point.y(),parent.rec.xmax(),parent.rec.ymax());
                else node.rec = new RectHV(parent.point.x(),parent.rec.ymin(),parent.rec.xmax(),parent.rec.ymax());
                parent.right = node;
            } 
        }
    }
    
    public boolean contains(Point2D p)            // does the set contain point p?
    {
        if(size == 0) return false;
        Node x = root;
        double cmp;
        
        while(x!=null)
        {
            cmp = x.compareTo(p);
            if(cmp>0) x = x.left;
            else if(cmp<0) x = x.right;
            else if(x.point.equals(p)) return true;
            else x = x.right;
        }
        return false;
    }
    
    public void draw()                         // draw all points to standard draw 
    {
        StdDraw.setPenRadius(0.01);
        draw(root);
        
    }
    
    private void draw(Node node)
    {
        if(node == null) return;
        
        if(node.numOfAncesters % 2 == 0)
        {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.point.x(), node.rec.ymin(), node.point.x(), node.rec.ymax());
        }
        else
        {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rec.xmin(), node.point.y(), node.rec.xmax(), node.point.y());
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.point(node.point.x(), node.point.y());
        draw(node.left);
        draw(node.right);
    }
    
    public Iterable<Point2D> range(RectHV rect)
        // all points that are inside the rectangle (or on the boundary) 
    {
        Queue<Point2D> queue = new Queue<Point2D>();
        Point2D p1 = new Point2D(rect.xmin(),rect.ymin());
        Point2D p2 = new Point2D(rect.xmax(),rect.ymax());
        Point2D p3 = new Point2D(rect.xmin(),rect.ymax());
        Point2D p4 = new Point2D(rect.xmax(),rect.ymin());
        
        searchnext(root, rect, p1,p2,p3,p4, queue);
        return queue;

    }
    
    private void searchnext(Node node, RectHV rect, Point2D p1,Point2D p2,Point2D p3,Point2D p4, Queue<Point2D> queue)
    {
        if(node == null) return;
        
            
        if(node.compareTo(p1)>=0&&node.compareTo(p2)<=0)
        {
          if(rect.contains(node.point))
          {
             queue.enqueue(node.point);
          }
            searchnext(node.left, rect, p1,p2,p3,p4, queue);
            searchnext(node.right,rect, p1,p2,p3,p4, queue);
            return;
        }
        if(node.compareTo(p3)<0)
        {
            searchnext(node.right, rect, p1,p2,p3,p4, queue);
            return;
        }
        if(node.compareTo(p4)>0)
            searchnext(node.left, rect, p1,p2,p3,p4, queue);
        
    }
    
    public Point2D nearest(Point2D p)
    // a nearest neighbor in the set to point p; null if the set is empty
    {
        if(p==null) throw new IllegalArgumentException("Empty point.");
        if(root == null) return null;
        dmin = p.distanceSquaredTo(root.point);
        nodeMin = root;
        findNearest(root, p);
        
        return nodeMin.point;
    }
    
    private void findNearest(Node node, Point2D p)
    {
        
        double d1 = 0;
        double d2 = 0;
        if(node.left != null && node.left.rec.contains(p))
        {
            double d = p.distanceSquaredTo(node.left.point);
            if(d<=dmin)
            {
                dmin = d;
                nodeMin = node.left;
            }
            findNearest(node.left, p);
            
        }
        
        if(node.right != null && node.right.rec.contains(p))
        {
            double d = p.distanceSquaredTo(node.right.point);
            if(d<=dmin)
            {
                dmin = d;
                nodeMin = node.right;
            }
            findNearest(node.right,p);
        }
        
        if(node.left != null)
        {
            d1 = node.left.rec.distanceSquaredTo(p);
            if(0<d1 && d1<=dmin)
            {
                double d = p.distanceSquaredTo(node.left.point);
                if(d<=dmin)
                {
                    dmin = d;
                    nodeMin = node.left;
                }
                findNearest(node.left, p);
            }
        }
        
        if(node.right != null)
        {
            d2 = node.right.rec.distanceSquaredTo(p);
            if(0<d2 && d2<=dmin)
            {
                double d = p.distanceSquaredTo(node.right.point);
                if(d<=dmin)
                {
                    dmin = d;
                    nodeMin = node.right;
                }
                findNearest(node.right, p);
            }
        }
        
        
    }
    

}