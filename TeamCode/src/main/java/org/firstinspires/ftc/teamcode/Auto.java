package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "AutoMain")
public class Auto extends LinearOpMode{
    static final double TICKS_PER_MOTOR_REV = 100; // needs to be changed
    static final double DRIVE_GEAR_REDUCTION = 1.0; // needs to be changed
    static final double WHEEL_DIAMETER_INCHES = 100; // needs to be changed
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);

    final private double DEFAULT_POWER = 1.0;
    Hardware hw = Hardware.getInstance(this);
//    PIDController drivePID = new PIDController(hw.encoderOdomX, 1, 0, 0);
//    PIDController turnPID = new PIDController(hw.gyro, 1, 0, 0);

    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);

        waitForStart();

        hw.frontLeft.setPower(1);
        hw.frontRight.setPower(-1);
        hw.backRight.setPower(1);
        hw.backLeft.setPower(-1);
        hw.setTargets(40000);
        while(opModeIsActive() && hw.notInRange(40000));
    }

    private void drive(double inches) {
        drive(inches, DEFAULT_POWER);
    }

    private void drive(double inches, double power) {
    }

    private void turn(double degrees) {
        turn(degrees, degrees > 0 ? 0.15 : -0.15, 0.50);
    }

    private void turn(double degrees, double minPower, double threshold) {
    }

    private void strafe(double inches) {
        strafe(inches, DEFAULT_POWER);
    }

    /**
     * This should NOT be used in regular auto, this is just here as a backup method. Strafe will not
     * have as much power as turning then driving which is the method which should be used instead
     *  of strafing.
    @param inches - left is negative, right is positive
     */
    private void strafe(double inches, double power) {
        int targetPos = (int) (inches/TICKS_PER_INCH);
        hw.frontLeft.setTargetPosition(targetPos + hw.frontLeft.getCurrentPosition());
        hw.frontRight.setTargetPosition(-targetPos + hw.frontRight.getCurrentPosition());
        hw.backLeft.setTargetPosition(-targetPos + hw.backLeft.getCurrentPosition());
        hw.backRight.setTargetPosition(targetPos + hw.backRight.getCurrentPosition());

        hw.setMotorsToPower(power);

        hw.setMotorsToRunToPosition();
        while(opModeIsActive() && hw.notInRange(targetPos)){
        }

        hw.setMotorsToPower(0);
    }
}
