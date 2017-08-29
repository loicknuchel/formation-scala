package exercices.s3FunctionalTypes

object CustomTree {

  // operations are in depth first
  trait Tree[+A] {
    def isEmpty: Boolean

    def nonEmpty: Boolean = !isEmpty

    def root: A

    def children: Seq[Tree[A]]

    def size: Int

    def height: Int

    def map[B](f: A => B): Tree[B]

    def find(p: A => Boolean): Option[A]

    def filter(p: A => Boolean): Tree[A]

    def flatMap[B](f: A => Tree[B]): Tree[B]

    def fold[B](z: B)(f: (B, A) => B): B

    def toList: List[A]
  }

  object Tree {
    def apply(): Tree[Nothing] = Leaf

    def apply[A](root: A): Tree[A] = Node(root)

    def apply[A](root: A, child: A): Tree[A] = Node(root, Seq(Node(child)))

    def apply[A](root: A, child: A, children: A*): Tree[A] = Node(root, Node(child) +: children.map(c => Node(c)))

    def apply[A](root: A, child: Tree[A]): Tree[A] = Node(root, Seq(child))

    def apply[A](root: A, child: Tree[A], children: Tree[A]*): Tree[A] = Node(root, child +: children)

    def empty[A]: Tree[A] = Leaf
  }

  case class Node[A](root: A, children: Seq[Tree[A]] = Seq()) extends Tree[A] {
    def isEmpty: Boolean = false

    def size: Int = 1 + children.map(_.size).sum

    def height: Int = 1 + (if (children.isEmpty) 0 else children.map(_.height).max)

    def map[B](f: A => B): Tree[B] = Node(f(root), children.map(_.map(f)))

    def find(p: A => Boolean): Option[A] = if (p(root)) Some(root) else children.collect { case tree => tree.find(p) }.headOption.flatten

    def filter(p: A => Boolean): Tree[A] = {
      val c = children.map(_.filter(p)).filter(_.nonEmpty)
      if (p(root)) Node(root, c) else Leaf
    }

    def flatMap[B](f: A => Tree[B]): Tree[B] = {
      f(root) match {
        case Node(r, c) => Node(r, c ++ children.map(_.flatMap(f)))
        case Leaf => Leaf
      }
    }

    def fold[B](z: B)(f: (B, A) => B): B = children.foldLeft(f(z, root))((acc, item) => item.fold(acc)(f))

    def toList: List[A] = root +: children.flatMap(_.toList).toList
  }

  case object Leaf extends Tree[Nothing] {
    def isEmpty: Boolean = true

    def root: Nothing = throw new NoSuchElementException

    def children: Seq[Tree[Nothing]] = throw new UnsupportedOperationException

    def size: Int = 0

    def height: Int = 0

    def map[B](f: Nothing => B): Tree[B] = this

    def find(p: Nothing => Boolean): Option[Nothing] = None

    def filter(p: Nothing => Boolean): Tree[Nothing] = this

    def flatMap[B](f: Nothing => Tree[B]): Tree[B] = this

    def fold[B](z: B)(f: (B, Nothing) => B): B = z

    def toList: List[Nothing] = Nil
  }

}
