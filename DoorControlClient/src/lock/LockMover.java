package lock;

import com.phidget22.PhidgetException;
import com.phidget22.RCServo;
import com.phidget22.RCServoPositionChangeEvent;
import com.phidget22.RCServoPositionChangeListener;
import com.phidget22.RCServoTargetPositionReachedEvent;
import com.phidget22.RCServoTargetPositionReachedListener;
import com.phidget22.RCServoVelocityChangeEvent;
import com.phidget22.RCServoVelocityChangeListener;

public class LockMover {
    static RCServo servo = null;

    public static RCServo getInstance() {
        if (servo == null) {
            servo = PhidgetLockMover();
        }
        return servo;
    }

    // Construct LockMover
    private static RCServo PhidgetLockMover() {
        try {
            servo = new RCServo();

            // Add listeners for various motor states
            servo.addVelocityChangeListener(new RCServoVelocityChangeListener() {
                public void onVelocityChange(RCServoVelocityChangeEvent e) {}
            });

            servo.addPositionChangeListener(new RCServoPositionChangeListener() {
                public void onPositionChange(RCServoPositionChangeEvent e) {}
            });

            servo.addTargetPositionReachedListener(new RCServoTargetPositionReachedListener() {
                public void onTargetPositionReached(RCServoTargetPositionReachedEvent e) {}
            });

            servo.open(2000);
        } catch (PhidgetException e) {
            e.printStackTrace();
        }
        return servo;
    }

    // Take control of motor servo and move to set position
    public static void moveServoTo(double motorPosition) {
        try {
            LockMover.getInstance();
            System.out.println("Moving lock to " + motorPosition);
            servo.setMaxPosition(210.0);
            servo.setTargetPosition(motorPosition);
            servo.setEngaged(true);
        } catch (PhidgetException e) {
            e.printStackTrace();
        }
    }
}