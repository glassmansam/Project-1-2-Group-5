package titan.landing;

import titan.flight.Vector3d;
import titan.interfaces.Controller;
import titan.interfaces.RateInterface;
import titan.interfaces.StateInterface;

public class OpenLoopController implements Controller {

    final double MASS = 6000;
    final double RADIUS = 3;

    @Override
    public RateInterface thrust(RateInterface windRate, StateInterface y) {
        //return predetermined thrust
        LandingState state = (LandingState) y;
        double altitude = state.getPosition().getY();
        double thru = 1.352 - altitude / 150000.0;
        double o = 0.1 / (altitude / 150000.0);
        //System.out.println(thru);

        if (altitude <= 0) {
            o = 0;
        }

        Vector3d thrust = new Vector3d(0, o, 0);

        //where u is the thrust
        //horizontal acceleration = u * sin(theta)
        //vertical acceleration = u * cos(theta) (-g)
        //angular acceleration = torque

        //double angle: TODO: not really sure where theta comes from. Maybe we define it here instead of using states
        double mainThrust = 1;
        double xAccel = mainThrust * Math.sin(state.getAngle());
        double yAccel = mainThrust * Math.cos(state.getAngle());
        double angularAccel = angularAcceleration(torque(new Vector3d(2,2,0), state));

        //

        return new LandingRate(state.getVelocity(), thrust, state.getShuttle_direction(), state.getWind_direction(), angularAccel);
    }

    public Double torque(Vector3d force, LandingState state) {
//        //FOR LEFT THRUSTER
//        //I = 1/2 * mass * radius^2 TODO: maybe we should take the moment of inertia of a sphere 2/3 * m * r^2 (hollow) or 2/5 * m * r^2 (solid)
//        //double momentInertia = 0.5 * MASS * RADIUS * RADIUS;
        double thrustAngle = 45;
//        double angle = 0;
//        //Vector3d torque = force.mul(RADIUS * Math.sin(thrustAngle));
//        Vector3d thrusterPos = new Vector3d(state.getPosition().getX() - RADIUS, state.getPosition().getY(), 0);
//        Vector3d r = thrusterPos.sub(state.getPosition());
//        //r = rotate(state.getAngle(), r);
//        angle = Math.toDegrees(Math.acos(r.dot(force)/(force.norm()*r.norm()))); //angle between force and position in degrees! TODO: is it always 45°?
//
//        return force.cross(r); //TODO: check if have to take the angle into account?
        force = rotate(state.getAngle(), force);
        double forceMagnitude = force.norm();
        return forceMagnitude*RADIUS*Math.sin(thrustAngle);

    }

    public double angularAcceleration(double torque) {
        double momentInertia = 0.5 * MASS * RADIUS * RADIUS;
        return torque/momentInertia;
    }

    public Vector3d rotate(double angle, Vector3d vector) {
        double newX = vector.getX() * Math.cos(angle) - vector.getY() * Math.sin(angle);
        double newY = vector.getX() * Math.sin(angle) + vector.getY() * Math.cos(angle);
        return new Vector3d(newX, newY, 0);
    }


}
