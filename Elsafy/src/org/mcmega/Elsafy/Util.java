package org.mcmega.Elsafy;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Util {
	
	public static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    public static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
	
	public static BlockFace[] getCardinalBlockFaces(){
		BlockFace[] cardinalFaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN};
		
		return cardinalFaces;
	}
	
	public static BlockFace[] getFacesForSnowPillar(){
		BlockFace[] cardinalFaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST};
		
		return cardinalFaces;
	}
	
	//Thanks WorldEdit!
    public static final double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    public static final double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }
    
    public static List<Location> makeCylinder(Location location, int height, double radius){
    	radius += 0.5;
    	
    	HashMap<Integer, List<Location>> locations = new HashMap<Integer, List<Location>>();
		//WorldEdit Make Cylinder
		final double invRadiusX = 1 / radius;
		final double invRadiusZ = 1 / radius;
			
		final int ceilRadiusX = (int) Math.ceil(radius);
		final int ceilRadiusZ = (int) Math.ceil(radius);
		
		double nextXn = 0;
		forX: for (int x = 0; x <= ceilRadiusX; ++x) {
			final double xn = nextXn;
			nextXn = (x + 1) * invRadiusX;
			double nextZn = 0;
			forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
				final double zn = nextZn;
				nextZn = (z + 1) * invRadiusZ;
				
				double distanceSq = Util.lengthSq(xn, zn);
				if (distanceSq > 1) {
					if (z == 0) {
						break forX;
					}
					break forZ;
				}

				/*if (!filled) {
					if (Util.lengthSq(nextXn, zn) <= 1 && Util.lengthSq(xn, nextZn) <= 1) {
						continue;
					}
				}*/

				for (int y = 0; y < height; ++y) {
					if (!locations.containsKey(y)){
						locations.put(y, new ArrayList<Location>());
					}
					
					locations.get(y).add(location.clone().add(x, y, z));
					locations.get(y).add(location.clone().add(-x, y, z));
					locations.get(y).add(location.clone().add(x, y, -z));
					locations.get(y).add(location.clone().add(-x, y, -z));
				}
			}
		}
		
		//Sort by Height
		List<Location> toSnow = new ArrayList<Location>();
		
		for (int i=0; i < height; i++){
			toSnow.addAll(locations.get(i));
		}
		
		return toSnow;
    }
    
    public static List<Location> makeSphere(Location location, double radius){
    	List<Location> toSnow = new ArrayList<Location>();
    	
        radius += 0.5;

        final double invRadiusX = 1 / radius;
        final double invRadiusY = 1 / radius;
        final double invRadiusZ = 1 / radius;

        final int ceilRadiusX = (int) Math.ceil(radius);
        final int ceilRadiusY = (int) Math.ceil(radius);
        final int ceilRadiusZ = (int) Math.ceil(radius);

        double nextXn = 0;
        forX: for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0;
            forY: for (int y = 0; y <= ceilRadiusY; ++y) {
                final double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0;
                forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }

                    /*if (!filled) {
                        if (lengthSq(nextXn, yn, zn) <= 1 && lengthSq(xn, nextYn, zn) <= 1 && lengthSq(xn, yn, nextZn) <= 1) {
                            continue;
                        }
                    }*/
                    
                    //Add some randomness to positive y values.
                    Random randomGenerator = new Random();
                    if (y > 0){
                    	if (randomGenerator.nextInt(10) > y){
                    		toSnow.add(location.clone().add(x, y, z));
                    	}
                    	if (randomGenerator.nextInt(10) > y){
                    		toSnow.add(location.clone().add(-x, y, z));
                    	}
                    	if (randomGenerator.nextInt(10) > y){
                    		toSnow.add(location.clone().add(x, y, -z));
                    	}
                    	if (randomGenerator.nextInt(10) > y){
                    		toSnow.add(location.clone().add(-x, y, -z));
                    	}
                    }
                    
                    //toSnow.add(location.clone().add(x, y, z));
                   // toSnow.add(location.clone().add(-x, y, z));
                    toSnow.add(location.clone().add(x, -y, z));
                    //toSnow.add(location.clone().add(x, y, -z));
                    toSnow.add(location.clone().add(-x, -y, z));
                    toSnow.add(location.clone().add(x, -y, -z));
                    //toSnow.add(location.clone().add(-x, y, -z));
                    toSnow.add(location.clone().add(-x, -y, -z));
                }
            }
        }
        return toSnow;
    }
	
    /**
     * Get the cardinal compass direction of a player.
     * 
     * @param player
     * @return
     */
    public static BlockFace getCardinalDirection(Player player) {
        double rot = (player.getLocation().getYaw() + 180) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        return getDirection(rot);
    }

    /**
     * Converts a rotation to a cardinal direction name.
     * 
     * @param rot
     * @return
     */
    private static BlockFace getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return BlockFace.NORTH;
        } else if (22.5 <= rot && rot < 67.5) {
            return BlockFace.NORTH_EAST;
        } else if (67.5 <= rot && rot < 112.5) {
            return BlockFace.EAST;
        } else if (112.5 <= rot && rot < 157.5) {
            return BlockFace.SOUTH_EAST;
        } else if (157.5 <= rot && rot < 202.5) {
            return BlockFace.SOUTH;
        } else if (202.5 <= rot && rot < 247.5) {
            return BlockFace.SOUTH_WEST;
        } else if (247.5 <= rot && rot < 292.5) {
            return BlockFace.WEST;
        } else if (292.5 <= rot && rot < 337.5) {
            return BlockFace.NORTH_WEST;
        } else if (337.5 <= rot && rot < 360.0) {
            return BlockFace.NORTH;
        } else {
            return null;
        }
    }
    
    public static BlockFace getExactDirection(Player player){
        double rot = (player.getLocation().getYaw() + 180) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
    	
        if (0 <= rot && rot < 45) {
            return BlockFace.NORTH;
        } else if (45 <= rot && rot < 135) {
            return BlockFace.EAST;
        } else if (135 <= rot && rot < 225) {
            return BlockFace.SOUTH;
        } else if (225 <= rot && rot < 315) {
            return BlockFace.WEST;
        } else if (315 <= rot && rot < 360.0) {
            return BlockFace.NORTH;
        } else {
            return null;
        }
    }
    
    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }
    
    public static BlockFace vectorToFace(Vector vector){
    	Vector v = vector.normalize();
    	
    	if (v.getX() > vector.getZ()){
    		if (v.getX() > 0){
        		return BlockFace.EAST;
        	}else if (v.getX() < 0){
        		return BlockFace.WEST;
        	}else{
        		return BlockFace.SELF;
        	}
    	}else{
    		if (v.getZ() > 0){
        		return BlockFace.SOUTH;
        	}else if (v.getZ() < 0){
        		return BlockFace.NORTH;
        	}else{
        		return BlockFace.SELF;
        	}
    	}
    	
    }
    
    public static List<Point2D> drawLine(int x1, int y1, int x2, int y2) {
    	List<Point2D> points = new ArrayList<Point2D>();
    	
        // delta of exact value and rounded value of the dependant variable
        int d = 0;
 
        int dy = Math.abs(y2 - y1);
        int dx = Math.abs(x2 - x1);
 
        int dy2 = (dy << 1); // slope scaling factors to avoid floating
        int dx2 = (dx << 1); // point
 
        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;
 
        if (dy <= dx) {
            for (;;) {
                //plot(g, x1, y1);
            	points.add(new Point(x1,y1));
                if (x1 == x2)
                    break;
                x1 += ix;
                d += dy2;
                if (d > dx) {
                    y1 += iy;
                    d -= dx2;
                }
            }
        } else {
            for (;;) {
                //plot(g, x1, y1);
            	points.add(new Point(x1,y1));
                if (y1 == y2)
                    break;
                y1 += iy;
                d += dx2;
                if (d > dy) {
                    x1 += ix;
                    d -= dy2;
                }
            }
        }
        
        return points;
    }

}
