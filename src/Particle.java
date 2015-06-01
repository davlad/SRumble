
public class Particle {

	private float x, y, z;
	private float vx, vy, vz;
	private float radius;
	private float maxSpeed = 2f;
	private float ox, oy, oz;
	private float limit = 5000;
	
	Particle(float xpos, float ypos, float zpos, float velx, float vely, float velz, float r) {
		x = xpos;
		y = ypos;
		z = zpos;
		ox = xpos;
		oy = ypos;
		oz = zpos;
		vx= velx;
		vy= vely;
		vz= velz;
		radius = r;
	}
	
	public void update() {
		//vy+= gravity;
		//vx+= windX;
		x+= vx;
		y+= vy;
		z+= vz;
		if (x > limit || y > limit || z > limit || x < -limit || y < -limit || z < -limit) {
			x = ox;
			y = oy;
			z = oz;
		}
		
	}
	public void updateVel(float velx, float vely, float velz) {
		if (vx < maxSpeed || vx > -maxSpeed)
		vx += velx;
		if (vy < maxSpeed || vy > -maxSpeed)
			vy += vely;
		if (vz < maxSpeed || vz > -maxSpeed)
			vz += velz;
	}
	
	public float r() {return radius;}
	public float x() {return x;}
	public float y() {return y;}
	public float z() {return z;}
	
}
