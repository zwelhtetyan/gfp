case class Point3d(x: Int, y: Int, z: Int)

val xs = List(1, 2, 3)
val ys = List(-2, 7)
val zs = List(3, 4)

// xs.flatMap(x => ys.flatMap(y => zs.map(z => Point3d(x, y, z))))

// Example-1
case class Point(x: Int, y: Int)
val points = List(Point(5, 2), Point(1, 1))
val radiuses = List(2, 1)

def isInside(point: Point, radius: Int): Boolean = {
    radius * radius >= point.x * point.x + point.y * point.y
}

// for {
//   r <- radiuses
//   point <- points.filter(p => isInside(p, r))
// } yield s"$point is within a radius of $r"


// Example-2
val riskyRadiuses = List(-10, 0, 2)
// riskyRadiuses.filter(r => r > 0)
//     .flatMap(rr => points.filter(p => isInside(p, rr))
//         .map(_p => s"$_p is within a radius of $rr"))

// for {
//   r <- riskyRadiuses.filter(r => r > 0)
//   point <- points.filter(p => isInside(p, r))
// } yield s"$point is within a radius of $r"


// for {
//   r <- riskyRadiuses
//   if r > 0
//   point <- points
//   if isInside(point, r)
// } yield s"$point is within a radius of $r"

def filterRadiuses(n: Int) = if (n > 0) List(n) else List.empty
def filterInside(p: Point, r: Int) = if (isInside(p, r)) List(p) else List.empty

for {
    r <- riskyRadiuses
    _r <- filterRadiuses(r)
    p <- points
    _p <- filterInside(p, r)
} yield s"$_p is within a radius of $_r"
