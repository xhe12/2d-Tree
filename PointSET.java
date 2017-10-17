import java.util.TreeSet;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class PointSET {
    
    private TreeSet<Point2D> tree;
    
    public PointSET() // construct an empty set of points 
    {
        tree = new TreeSet<Point2D>();
        
    }
   public boolean isEmpty() // is the set empty? 
   {
       return tree.isEmpty();
   }
   public int size() // number of points in the set
   {
       return tree.size();
   }
   public void insert(Point2D p) // add the point to the set (if it is not already in the set)
   {
       if(p==null) return;
       tree.add(p);
       
   }
   public boolean contains(Point2D p) // does the set contain point p? 
   {
       return tree.contains(p);
   }
   public void draw()                         // draw all points to standard draw 
   {
       StdDraw.setPenRadius(0.01);
       for(Point2D p:tree)
       {
           StdDraw.point(p.x(),p.y());
       }
   }
   public Iterable<Point2D> range(RectHV rect) 
   // all points that are inside the rectangle (or on the boundary) 
   {
       Queue<Point2D> queue = new Queue<Point2D>();
       for(Point2D p:tree)
       {
           if(rect.contains(p)) queue.enqueue(p);
       }
       return queue;
   }
   public Point2D nearest(Point2D p)  
   // a nearest neighbor in the set to point p; null if the set is empty 
   {
       if(p==null) throw new IllegalArgumentException("Empty point.");
       if(tree == null) return null;
       double dmin = Double.POSITIVE_INFINITY;
       double d = 0;
       Point2D minPoint = null;
       for(Point2D treePoint: tree)
       {
           d = p.distanceTo(treePoint);
           if(d<dmin)
           { 
               minPoint = treePoint;
               dmin = d;
           }
       }
       return minPoint;
   }
   

}